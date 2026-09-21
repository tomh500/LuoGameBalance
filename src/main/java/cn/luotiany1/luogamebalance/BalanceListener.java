package cn.luotiany1.luogamebalance;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class BalanceListener implements Listener {
    private final LuoGameBalancePlugin plugin;
    private final HashMap<java.util.UUID, Integer> deathXp = new HashMap<>();
    public BalanceListener(LuoGameBalancePlugin plugin) { this.plugin = plugin; }
    public void open(Player p, GuiHolder.Kind kind) {
        Inventory inv = Bukkit.createInventory(new GuiHolder(kind), 9, plugin.plainMessage(kind == GuiHolder.Kind.ANVIL ? "gui.anvil-title" : "gui.smithing-title"));
        refresh(p, inv, kind); p.openInventory(inv);
    }
    private void button(Inventory inv, int slot, Material type, String name, java.util.List<String> lore) { ItemStack item = new ItemStack(type); ItemMeta meta = item.getItemMeta(); meta.setDisplayName(plugin.plainMessage(name)); meta.setLore(lore); item.setItemMeta(meta); inv.setItem(slot, item); }
    private void refresh(Player p, Inventory inv, GuiHolder.Kind kind) {
        ItemStack input = inv.getItem(0); int count = input == null ? 1 : Math.max(1, input.getEnchantments().size());
        int mergeCost = plugin.config().cost("merge", count), extractCost = plugin.config().cost("extract", count), splitCost = plugin.config().cost("split", count);
        button(inv, 2, kind == GuiHolder.Kind.ANVIL ? Material.ANVIL : Material.SMITHING_TABLE, "gui.confirm", kind == GuiHolder.Kind.ANVIL ? plugin.messageList("gui.lore-base", mergeCost, Xp.total(p)) : upgradeLore(p));
        if (kind == GuiHolder.Kind.ANVIL) { button(inv, 5, Material.ENCHANTED_BOOK, "gui.extract", plugin.messageList("gui.lore-base", extractCost, Xp.total(p))); button(inv, 6, Material.BOOK, "gui.split", plugin.messageList("gui.lore-base", splitCost, Xp.total(p))); }
        button(inv, 8, Material.NAME_TAG, "gui.native", plugin.messageList("gui.lore-native"));
    }
    private java.util.List<String> upgradeLore(Player p) { Material template = plugin.config().upgradeTemplate(); String state = plugin.config().experimental("upgrade") ? template.name() + (p.getInventory().contains(template) ? " ✓" : " ✗") : "-"; return plugin.messageList("gui.lore-upgrade", "见输入物品", plugin.config().cost("upgrade", 1), Xp.total(p), state); }
    private GuiHolder holder(Inventory inv) { return inv.getHolder() instanceof GuiHolder h ? h : null; }
    @EventHandler public void interact(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null) return;
        Player p = e.getPlayer(); Material m = e.getClickedBlock().getType();
        if (m == Material.ANVIL || m == Material.CHIPPED_ANVIL || m == Material.DAMAGED_ANVIL) { if (plugin.consumeNativeOnce(p.getUniqueId(), GuiHolder.Kind.ANVIL)) return; if (plugin.anvil(p.getUniqueId())) { e.setCancelled(true); Bukkit.getScheduler().runTask(plugin, () -> open(p, GuiHolder.Kind.ANVIL)); } }
        if (m == Material.SMITHING_TABLE) { if (plugin.consumeNativeOnce(p.getUniqueId(), GuiHolder.Kind.SMITHING)) return; if (plugin.smithing(p.getUniqueId())) { e.setCancelled(true); Bukkit.getScheduler().runTask(plugin, () -> open(p, GuiHolder.Kind.SMITHING)); } }
    }
    @EventHandler public void click(InventoryClickEvent e) {
        GuiHolder h = holder(e.getView().getTopInventory()); if (h == null) return;
        int raw = e.getRawSlot(); if (raw < 0 || raw >= 9) return;
        if (raw == 0 || raw == 1) { Bukkit.getScheduler().runTask(plugin, () -> refresh((Player) e.getWhoClicked(), e.getView().getTopInventory(), h.kind())); return; }
        e.setCancelled(true); Player p = (Player) e.getWhoClicked(); Inventory inv = e.getView().getTopInventory();
        if (raw == 8) { plugin.allowNativeOnce(p.getUniqueId(), h.kind()); plugin.tell(p, "native-ready", plugin.plainMessage(h.kind() == GuiHolder.Kind.ANVIL ? "gui.anvil-name" : "gui.smithing-name")); p.closeInventory(); return; }
        if (h.kind() == GuiHolder.Kind.ANVIL && raw == 5) { plugin.service().extract(p, inv.getItem(0)); return; }
        if (h.kind() == GuiHolder.Kind.ANVIL && raw == 6) { if (plugin.service().split(p, inv.getItem(0))) inv.setItem(0, null); return; }
        if (raw != 2) return;
        if (h.kind() == GuiHolder.Kind.ANVIL) { if (inv.getItem(0) != null && inv.getItem(1) != null && plugin.service().repairMaterial(inv.getItem(0).getType()) == inv.getItem(1).getType()) { if (plugin.service().repair(p, inv.getItem(0), inv.getItem(1).getType())) inv.setItem(1, null); refresh(p, inv, h.kind()); return; } ItemStack result = plugin.service().merge(p, inv.getItem(0), inv.getItem(1)); if (result != null) { inv.setItem(0, result); inv.setItem(1, null); } refresh(p, inv, h.kind()); return; }
        upgrade(p, inv);
    }
    @EventHandler public void drag(InventoryDragEvent e) { GuiHolder h = holder(e.getView().getTopInventory()); if (h != null && e.getRawSlots().stream().anyMatch(x -> x < 9 && x != 0 && x != 1)) e.setCancelled(true); }
    @EventHandler public void close(InventoryCloseEvent e) { GuiHolder h = holder(e.getInventory()); if (h == null) return; Player p = (Player) e.getPlayer(); for (int i = 0; i < 2; i++) { ItemStack x = e.getInventory().getItem(i); if (x == null || x.getType().isAir()) continue; HashMap<Integer, ItemStack> left = p.getInventory().addItem(x); left.values().forEach(v -> p.getWorld().dropItemNaturally(p.getLocation(), v)); e.getInventory().setItem(i, null); } }
    @EventHandler public void death(PlayerDeathEvent e) { int total = Xp.total(e.getEntity()); deathXp.put(e.getEntity().getUniqueId(), Math.round((float) (total * Math.clamp(plugin.config().deathRatio(), 0, 1)))); e.setDroppedExp(0); }
    @EventHandler public void quit(PlayerQuitEvent e) { deathXp.remove(e.getPlayer().getUniqueId()); }
    @EventHandler public void respawn(org.bukkit.event.player.PlayerRespawnEvent e) { Integer xp = deathXp.remove(e.getPlayer().getUniqueId()); if (xp != null) Bukkit.getScheduler().runTask(plugin, () -> Xp.set(e.getPlayer(), xp)); }
    private void upgrade(Player p, Inventory inv) { ItemStack tool = inv.getItem(0), material = inv.getItem(1); if (tool == null || material == null) { plugin.tell(p, "invalid-item"); return; } Material target = plugin.service().upgrade(tool.getType()), required = plugin.service().upgradeMaterial(tool.getType()); if (target == null || required == null || material.getType() != required || material.getAmount() < 1) { plugin.tell(p, "invalid-item"); return; } Material template = plugin.config().upgradeTemplate(); if (plugin.config().experimental("upgrade") && !p.getInventory().contains(template)) { plugin.tell(p, "need-template", template.name()); return; } int cost = plugin.config().cost("upgrade", 1); if (!Xp.canPay(p, cost)) { plugin.tell(p, "not-enough-xp", cost, Xp.total(p)); return; } Xp.take(p, cost); ItemStack out = tool.clone(); out.setType(target); inv.setItem(0, out); material.setAmount(material.getAmount() - 1); if (material.getAmount() == 0) inv.setItem(1, null); plugin.tell(p, "success", cost); refresh(p, inv, GuiHolder.Kind.SMITHING); }
}
