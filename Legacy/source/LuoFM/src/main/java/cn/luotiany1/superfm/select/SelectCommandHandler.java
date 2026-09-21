/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 */
package cn.luotiany1.superfm.select;

import cn.luotiany1.superfm.main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SelectCommandHandler {
    private main plugin;

    public SelectCommandHandler(main plugin) {
        this.plugin = plugin;
    }

    public void handle(Player player) {
        int bookSlot = player.getInventory().first(Material.BOOK);
        if (bookSlot == -1) {
            player.sendMessage(ChatColor.RED + "\u4f60\u7684\u80cc\u5305\u4e2d\u81f3\u5c11\u9700\u8981\u4e00\u672c\u4e66\uff01");
            return;
        }
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || held.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "\u4f60\u5fc5\u987b\u624b\u6301\u4e00\u4e2a\u7269\u54c1\u3002");
            return;
        }
        if (held.getEnchantments().isEmpty()) {
            player.sendMessage(ChatColor.RED + "\u624b\u6301\u7269\u54c1\u6ca1\u6709\u9644\u9b54\u3002");
            return;
        }
        ItemStack newBook = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)newBook.getItemMeta();
        held.getEnchantments().forEach((ench, level) -> bookMeta.addStoredEnchant(ench, level.intValue(), true));
        newBook.setItemMeta((ItemMeta)bookMeta);
        ItemMeta heldMeta = held.getItemMeta();
        held.getEnchantments().keySet().forEach(arg_0 -> ((ItemMeta)heldMeta).removeEnchant(arg_0));
        held.setItemMeta(heldMeta);
        held.setType(held.getType());
        ItemStack bookStack = player.getInventory().getItem(bookSlot);
        if (bookStack.getAmount() > 1) {
            bookStack.setAmount(bookStack.getAmount() - 1);
        } else {
            player.getInventory().setItem(bookSlot, null);
        }
        double cost = this.plugin.getConfig().getDouble("select_experience_cost");
        if ((double)player.getLevel() < cost) {
            player.sendMessage(ChatColor.RED + "\u4f60\u7684\u7ecf\u9a8c\u4e0d\u8db3\u4ee5\u8fdb\u884c\u9b54\u5492\u63d0\u53d6\u3002");
            return;
        }
        player.setLevel((int)((double)player.getLevel() - cost));
        player.getInventory().addItem(new ItemStack[]{newBook});
        player.sendMessage(ChatColor.GREEN + "\u9b54\u5492\u63d0\u53d6\u6210\u529f\uff01");
    }
}

