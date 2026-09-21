/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package cn.luotiany1.superfm.forge;

import cn.luotiany1.superfm.forge.FOpenCommandHandler;
import cn.luotiany1.superfm.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class FInventoryListener
implements Listener {
    private main plugin;

    public FInventoryListener(main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSmithingTableRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() == Material.SMITHING_TABLE) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
            new FOpenCommandHandler(this.plugin).handle(player);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @EventHandler
    public void onFInventoryClick(InventoryClickEvent event) {
        String base;
        String title = event.getView().getTitle();
        if (title == null || !title.equals(ChatColor.GREEN + "SuperFM FOpen")) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        Inventory inv = event.getInventory();
        int slot = event.getRawSlot();
        if (event.getClickedInventory() == player.getInventory()) {
            return;
        }
        if (slot >= inv.getSize()) return;
        if (slot == 0 || slot == 1) {
            event.setCancelled(false);
        } else if (slot == 2 || slot == 8) {
            event.setCancelled(true);
        } else {
            event.setCancelled(true);
            return;
        }
        boolean foverlayEnabled = this.plugin.getFOverlaySettings().getOrDefault(player.getUniqueId(), false);
        if (slot == 8 && !foverlayEnabled) {
            player.playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> player.openInventory(Bukkit.createInventory((InventoryHolder)player, (InventoryType)InventoryType.SMITHING)));
            return;
        }
        if (slot != 2) return;
        ItemStack toolItem = inv.getItem(0);
        ItemStack materialItem = inv.getItem(1);
        if (toolItem == null || materialItem == null) {
            player.sendMessage(ChatColor.RED + "\u8bf7\u5728\u683c 0 \u653e\u5165\u5de5\u5177\uff0c\u5728\u683c 1 \u653e\u5165\u5347\u7ea7\u6750\u6599\u3002");
            return;
        }
        Material toolType = toolItem.getType();
        String toolName = toolType.name();
        Material targetType = null;
        Material requiredMaterial = null;
        if (toolName.startsWith("WOODEN_")) {
            base = toolName.substring("WOODEN_".length());
            targetType = Material.valueOf((String)("STONE_" + base));
            requiredMaterial = Material.COBBLESTONE;
        } else if (toolName.startsWith("STONE_")) {
            base = toolName.substring("STONE_".length());
            targetType = Material.valueOf((String)("IRON_" + base));
            requiredMaterial = Material.IRON_INGOT;
        } else if (toolName.startsWith("IRON_")) {
            base = toolName.substring("IRON_".length());
            targetType = Material.valueOf((String)("GOLDEN_" + base));
            requiredMaterial = Material.GOLD_INGOT;
        } else if (toolName.startsWith("GOLDEN_")) {
            base = toolName.substring("GOLDEN_".length());
            targetType = Material.valueOf((String)("DIAMOND_" + base));
            requiredMaterial = Material.DIAMOND;
        } else {
            player.sendMessage(ChatColor.RED + "\u8be5\u5de5\u5177\u65e0\u6cd5\u5347\u7ea7\u3002");
            return;
        }
        if (materialItem.getType() != requiredMaterial) {
            player.sendMessage(ChatColor.RED + "\u5347\u7ea7 " + toolType.name() + " \u9700\u8981 " + requiredMaterial.name() + " \u4f5c\u4e3a\u6750\u6599\u3002");
            return;
        }
        short maxDurability = toolType.getMaxDurability();
        short damage0 = toolItem.getDurability();
        short damage1 = materialItem.getDurability();
        int bonus = (int)((double)maxDurability * 0.12);
        short newDamage = (short)Math.max(0, damage0 + damage1 - bonus);
        ItemStack upgraded = toolItem.clone();
        upgraded.setType(targetType);
        upgraded.setDurability(newDamage);
        if (toolItem.getAmount() > 1) {
            toolItem.setAmount(toolItem.getAmount() - 1);
        } else {
            inv.setItem(0, null);
        }
        if (materialItem.getAmount() > 1) {
            materialItem.setAmount(materialItem.getAmount() - 1);
        } else {
            inv.setItem(1, null);
        }
        inv.setItem(0, upgraded);
        player.updateInventory();
        player.sendMessage(ChatColor.GREEN + "\u5347\u7ea7\u6210\u529f\uff01\u4f60\u83b7\u5f97\u4e86 " + upgraded.getType().name());
        player.playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
    }
}

