package cn.luotiany1.luogamebalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class BalanceService {
    private final LuoGameBalancePlugin plugin;
    public BalanceService(LuoGameBalancePlugin plugin) { this.plugin = plugin; }

    private boolean pay(Player p, String feature, int units) {
        int cost = plugin.config().cost(feature, units);
        if (!Xp.take(p, cost)) { plugin.tell(p, "not-enough-xp", cost, Xp.total(p)); return false; }
        plugin.tell(p, "success", cost); return true;
    }

    private boolean give(Player p, List<ItemStack> items) {
        if (p.getInventory().firstEmpty() == -1 && !items.isEmpty()) { plugin.tell(p, "inventory-full"); return false; }
        return true;
    }

    public void extract(Player p, ItemStack item) {
        if (item == null || item.getType().isAir() || item.getEnchantments().isEmpty()) { plugin.tell(p, "need-enchanted-item"); return; }
        if (p.getInventory().first(Material.BOOK) < 0) { plugin.tell(p, "need-book"); return; }
        if (!give(p, List.of(new ItemStack(Material.ENCHANTED_BOOK)))) return;
        int cost = plugin.config().cost("extract", item.getEnchantments().size());
        if (Xp.total(p) < cost) { plugin.tell(p, "not-enough-xp", cost, Xp.total(p)); return; }
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        item.getEnchantments().forEach((e, level) -> meta.addStoredEnchant(e, level, false));
        book.setItemMeta(meta);
        // Re-apply a clean metadata component rather than only mutating the stack.
        // Modern servers use this component to decide whether an item is enchantable.
        ItemMeta cleanMeta = item.getItemMeta();
        for (Enchantment e : new ArrayList<>(cleanMeta.getEnchants().keySet())) cleanMeta.removeEnchant(e);
        cleanMeta.setEnchantmentGlintOverride(false);
        item.setItemMeta(cleanMeta);
        int slot = p.getInventory().first(Material.BOOK);
        ItemStack blank = p.getInventory().getItem(slot);
        blank.setAmount(blank.getAmount() - 1);
        if (blank.getAmount() == 0) p.getInventory().setItem(slot, null);
        Xp.take(p, cost); p.getInventory().addItem(book); plugin.tell(p, "success", cost);
    }

    public boolean split(Player p, ItemStack item) {
        if (item == null || item.getType() != Material.ENCHANTED_BOOK || !(item.getItemMeta() instanceof EnchantmentStorageMeta meta) || meta.getStoredEnchants().isEmpty()) { plugin.tell(p, "need-enchanted-book"); return false; }
        int n = meta.getStoredEnchants().size(), books = 0;
        for (ItemStack stack : p.getInventory().getStorageContents()) if (stack != null && stack.getType() == Material.BOOK) books += stack.getAmount();
        List<ItemStack> out = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> e : meta.getStoredEnchants().entrySet()) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK); EnchantmentStorageMeta b = (EnchantmentStorageMeta) book.getItemMeta(); b.addStoredEnchant(e.getKey(), e.getValue(), false); book.setItemMeta(b); out.add(book);
        }
        int cost = plugin.config().cost("split", n);
        if (books < n) { plugin.tell(p, "need-more-books", n, books); return false; }
        if (Xp.total(p) < cost) { plugin.tell(p, "not-enough-xp", cost, Xp.total(p)); return false; }
        if (!give(p, out)) return false;
        consume(p, Material.BOOK, n); item.setAmount(0); Xp.take(p, cost); out.forEach(x -> p.getInventory().addItem(x)); plugin.tell(p, "success", cost); return true;
    }

    public ItemStack merge(Player p, ItemStack first, ItemStack second) {
        if (first == null || second == null || first.getType().isAir() || second.getType().isAir()) { plugin.tell(p, "invalid-item"); return null; }
        Map<Enchantment, Integer> add = second.getType() == Material.ENCHANTED_BOOK && second.getItemMeta() instanceof EnchantmentStorageMeta m ? m.getStoredEnchants() : second.getEnchantments();
        if (add.isEmpty() || (second.getType() != Material.ENCHANTED_BOOK && first.getType() != second.getType())) { plugin.tell(p, "invalid-item"); return null; }
        ItemStack result = first.clone();
        for (Map.Entry<Enchantment, Integer> e : add.entrySet()) if (!safeAdd(result, e.getKey(), e.getValue())) { plugin.tell(p, "invalid-item"); return null; }
        int cost = plugin.config().cost("merge", add.size());
        if (Xp.total(p) < cost) { plugin.tell(p, "not-enough-xp", cost, Xp.total(p)); return null; }
        setDamage(result, Math.max(0, damage(first) + damage(second) - first.getType().getMaxDurability() / 8));
        Xp.take(p, cost); plugin.tell(p, "success", cost); return result;
    }

    public boolean repair(Player p, ItemStack tool, Material material) {
        if (tool == null || tool.getType().isAir() || !repairMaterial(tool.getType()).equals(material)) { plugin.tell(p, "invalid-item"); return false; }
        int cost = plugin.config().cost("repair", 1); if (Xp.total(p) < cost) { plugin.tell(p, "not-enough-xp", cost, Xp.total(p)); return false; }
        setDamage(tool, 0); Xp.take(p, cost); plugin.tell(p, "success", cost); return true;
    }

    public Material upgrade(Material type) {
        String n = type.name(); String base;
        if (n.startsWith("WOODEN_")) { base = n.substring(7); return material("STONE_" + base); }
        if (n.startsWith("STONE_")) { base = n.substring(6); return material("IRON_" + base); }
        if (n.startsWith("IRON_")) { base = n.substring(5); return material("GOLDEN_" + base); }
        if (n.startsWith("GOLDEN_")) { base = n.substring(7); return material("DIAMOND_" + base); }
        return null;
    }
    public Material upgradeMaterial(Material type) {
        String n = type.name();
        if (n.startsWith("WOODEN_")) return Material.COBBLESTONE;
        if (n.startsWith("STONE_")) return Material.IRON_INGOT;
        if (n.startsWith("IRON_")) return Material.GOLD_INGOT;
        if (n.startsWith("GOLDEN_")) return Material.DIAMOND;
        return null;
    }
    private Material material(String key) { try { return Material.valueOf(key); } catch (IllegalArgumentException e) { return null; } }
    private boolean safeAdd(ItemStack item, Enchantment ench, int level) {
        for (Enchantment old : item.getEnchantments().keySet()) if (!old.equals(ench) && old.conflictsWith(ench)) return false;
        if (!ench.canEnchantItem(item) || level > ench.getMaxLevel()) return false;
        item.addEnchantment(ench, Math.max(level, item.getEnchantmentLevel(ench)));
        return true;
    }
    private void consume(Player p, Material type, int amount) { for (int i = 0; i < p.getInventory().getSize() && amount > 0; i++) { ItemStack x = p.getInventory().getItem(i); if (x == null || x.getType() != type) continue; int use = Math.min(amount, x.getAmount()); x.setAmount(x.getAmount() - use); if (x.getAmount() == 0) p.getInventory().setItem(i, null); amount -= use; } }
    private int damage(ItemStack x) { return x.getItemMeta() instanceof Damageable d ? d.getDamage() : 0; }
    private void setDamage(ItemStack x, int amount) { if (x.getItemMeta() instanceof Damageable d) { d.setDamage(amount); x.setItemMeta((ItemMeta) d); } }
    public Material repairMaterial(Material type) { String n = type.name(); if (n.contains("DIAMOND")) return Material.DIAMOND; if (n.contains("IRON")) return Material.IRON_INGOT; if (n.contains("GOLDEN")) return Material.GOLD_INGOT; if (n.contains("NETHERITE")) return Material.NETHERITE_INGOT; if (n.contains("WOODEN")) return Material.OAK_PLANKS; return Material.COBBLESTONE; }
}
