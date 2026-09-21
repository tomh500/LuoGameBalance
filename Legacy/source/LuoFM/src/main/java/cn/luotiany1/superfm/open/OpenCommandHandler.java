/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package cn.luotiany1.superfm.open;

import cn.luotiany1.superfm.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OpenCommandHandler {
    private main plugin;

    public OpenCommandHandler(main plugin) {
        this.plugin = plugin;
    }

    public void handle(Player player) {
        Inventory inv = Bukkit.createInventory(null, (int)9, (String)(ChatColor.GREEN + "SuperFM Open"));
        ItemStack anvil = new ItemStack(Material.ANVIL);
        ItemMeta anvilMeta = anvil.getItemMeta();
        anvilMeta.setDisplayName(ChatColor.YELLOW + "\u70b9\u51fb\u8fdb\u884c\u64cd\u4f5c");
        anvil.setItemMeta(anvilMeta);
        inv.setItem(2, anvil);
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.setDisplayName(ChatColor.RED + "\u4e0d\u53ef\u64cd\u4f5c");
        barrier.setItemMeta(barrierMeta);
        inv.setItem(3, barrier);
        inv.setItem(4, barrier);
        ItemStack expBottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta expMeta = expBottle.getItemMeta();
        expMeta.setDisplayName(ChatColor.GOLD + "\u63d0\u53d6\u9644\u9b54");
        expBottle.setItemMeta(expMeta);
        inv.setItem(5, expBottle);
        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta bookMeta = enchantedBook.getItemMeta();
        bookMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "\u62c6\u5206\u9644\u9b54");
        enchantedBook.setItemMeta(bookMeta);
        inv.setItem(6, enchantedBook);
        inv.setItem(7, barrier);
        ItemStack nameTag = new ItemStack(Material.NAME_TAG);
        ItemMeta nameTagMeta = nameTag.getItemMeta();
        nameTagMeta.setDisplayName(ChatColor.AQUA + "\u6253\u5f00\u539f\u7248\u94c1\u7827");
        nameTag.setItemMeta(nameTagMeta);
        inv.setItem(8, nameTag);
        player.openInventory(inv);
    }
}

