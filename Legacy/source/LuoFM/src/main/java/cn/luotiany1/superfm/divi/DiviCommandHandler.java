/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 */
package cn.luotiany1.superfm.divi;

import cn.luotiany1.superfm.main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class DiviCommandHandler {
    private main plugin;

    public DiviCommandHandler(main plugin) {
        this.plugin = plugin;
    }

    public void handle(Player player) {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || held.getType() != Material.ENCHANTED_BOOK) {
            player.sendMessage(ChatColor.RED + "\u4f60\u5fc5\u987b\u624b\u6301\u4e00\u672c\u9644\u9b54\u4e66\u3002");
            return;
        }
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta)held.getItemMeta();
        if (meta.getStoredEnchants().isEmpty()) {
            player.sendMessage(ChatColor.RED + "\u8be5\u9644\u9b54\u4e66\u6ca1\u6709\u9644\u9b54\u3002");
            return;
        }
        int enchantCount = meta.getStoredEnchants().size();
        int bookCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != Material.BOOK) continue;
            bookCount += item.getAmount();
        }
        if (bookCount < enchantCount) {
            player.sendMessage(ChatColor.RED + "\u4f60\u7684\u80cc\u5305\u4e2d\u666e\u901a\u4e66\u6570\u91cf\u4e0d\u8db3\uff0c\u9700\u8981\u81f3\u5c11 " + enchantCount + " \u672c\u3002");
            return;
        }
        double cost = this.plugin.getConfig().getDouble("divi_experience_cost");
        if ((double)player.getLevel() < cost) {
            player.sendMessage(ChatColor.RED + "\u4f60\u7684\u7ecf\u9a8c\u4e0d\u8db3\u4ee5\u8fdb\u884c\u9644\u9b54\u4e66\u5206\u89e3\u3002");
            return;
        }
        player.setLevel((int)((double)player.getLevel() - cost));
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        int remaining = enchantCount;
        for (int i = 0; i < player.getInventory().getSize(); ++i) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() != Material.BOOK) continue;
            int amt = item.getAmount();
            if (amt <= remaining) {
                remaining -= amt;
                player.getInventory().clear(i);
            } else {
                item.setAmount(amt - remaining);
                remaining = 0;
            }
            if (remaining <= 0) break;
        }
        for (Enchantment ench : meta.getStoredEnchants().keySet()) {
            int level = (Integer)meta.getStoredEnchants().get(ench);
            ItemStack newBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta newMeta = (EnchantmentStorageMeta)newBook.getItemMeta();
            newMeta.addStoredEnchant(ench, level, true);
            newBook.setItemMeta((ItemMeta)newMeta);
            player.getInventory().addItem(new ItemStack[]{newBook});
        }
        player.sendMessage(ChatColor.GREEN + "\u9644\u9b54\u4e66\u5206\u89e3\u6210\u529f\uff01");
    }
}

