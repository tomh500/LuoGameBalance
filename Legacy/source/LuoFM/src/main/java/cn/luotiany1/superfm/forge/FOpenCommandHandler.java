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
package cn.luotiany1.superfm.forge;

import cn.luotiany1.superfm.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FOpenCommandHandler {
    private main plugin;

    public FOpenCommandHandler(main plugin) {
        this.plugin = plugin;
    }

    public void handle(Player player) {
        Inventory inv = Bukkit.createInventory(null, (int)9, (String)(ChatColor.GREEN + "SuperFM FOpen"));
        ItemStack forgeIcon = new ItemStack(Material.SMITHING_TABLE);
        ItemMeta meta = forgeIcon.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "\u70b9\u51fb\u8fdb\u884c\u5347\u7ea7");
        forgeIcon.setItemMeta(meta);
        inv.setItem(2, forgeIcon);
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta bmeta = barrier.getItemMeta();
        bmeta.setDisplayName(ChatColor.RED + "\u4e0d\u53ef\u64cd\u4f5c");
        barrier.setItemMeta(bmeta);
        for (int i = 3; i < 8; ++i) {
            inv.setItem(i, barrier);
        }
        ItemStack nameTag = new ItemStack(Material.NAME_TAG);
        ItemMeta nmeta = nameTag.getItemMeta();
        nmeta.setDisplayName(ChatColor.AQUA + "\u6253\u5f00\u539f\u7248\u953b\u9020\u53f0");
        nameTag.setItemMeta(nmeta);
        inv.setItem(8, nameTag);
        player.openInventory(inv);
    }
}

