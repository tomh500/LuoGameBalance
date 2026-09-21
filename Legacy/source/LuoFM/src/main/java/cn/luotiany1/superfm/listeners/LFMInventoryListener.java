/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 */
package cn.luotiany1.superfm.listeners;

import cn.luotiany1.superfm.main;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class LFMInventoryListener
implements Listener {
    private main plugin;

    public LFMInventoryListener(main plugin) {
        this.plugin = plugin;
    }

    private void executeCommandAsOp(Player player, String command) {
        boolean wasOp = player.isOp();
        player.setOp(true);
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            player.performCommand(command);
            if (!wasOp) {
                player.setOp(false);
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HashMap leftover;
        Player player = (Player)event.getPlayer();
        Inventory closedInv = event.getInventory();
        if (closedInv.getType() != InventoryType.ANVIL) {
            return;
        }
        ItemStack anvilInput0 = closedInv.getItem(0);
        ItemStack anvilInput1 = closedInv.getItem(1);
        ItemStack anvilResult = closedInv.getItem(2);
        if (anvilResult != null && !(leftover = player.getInventory().addItem(new ItemStack[]{anvilResult})).isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), (ItemStack)leftover.get(0));
        }
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            if (anvilInput0 != null || anvilInput1 != null) {
                Inventory customInv = Bukkit.createInventory((InventoryHolder)player, (int)9, (String)(ChatColor.GREEN + "SuperFM Open"));
                this.setupCustomGUI(customInv);
                customInv.setItem(0, anvilInput0);
                customInv.setItem(1, anvilInput1);
                player.openInventory(customInv);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 1.0f);
            }
        }, 1L);
    }

    private void setupCustomGUI(Inventory inv) {
        ItemStack anvilButton = new ItemStack(Material.ANVIL);
        ItemMeta anvilMeta = anvilButton.getItemMeta();
        anvilMeta.setDisplayName(ChatColor.GREEN + "\u5408\u5e76/\u9644\u9b54");
        anvilButton.setItemMeta(anvilMeta);
        inv.setItem(2, anvilButton);
        ItemStack extractButton = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta extractMeta = extractButton.getItemMeta();
        extractMeta.setDisplayName(ChatColor.BLUE + "\u63d0\u53d6\u9644\u9b54");
        extractButton.setItemMeta(extractMeta);
        inv.setItem(5, extractButton);
        ItemStack splitButton = new ItemStack(Material.BOOK);
        ItemMeta splitMeta = splitButton.getItemMeta();
        splitMeta.setDisplayName(ChatColor.YELLOW + "\u62c6\u5206\u9644\u9b54");
        splitButton.setItemMeta(splitMeta);
        inv.setItem(6, splitButton);
        ItemStack nameTagButton = new ItemStack(Material.NAME_TAG);
        ItemMeta nameTagMeta = nameTagButton.getItemMeta();
        nameTagMeta.setDisplayName(ChatColor.GOLD + "\u91cd\u547d\u540d/\u539f\u7248\u64cd\u4f5c");
        nameTagButton.setItemMeta(nameTagMeta);
        inv.setItem(8, nameTagButton);
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta barrierMeta = barrier.getItemMeta();
        barrierMeta.setDisplayName(ChatColor.RED + "\u4e0d\u53ef\u64cd\u4f5c");
        barrier.setItemMeta(barrierMeta);
        inv.setItem(3, barrier.clone());
        inv.setItem(4, barrier.clone());
        inv.setItem(7, barrier.clone());
    }

    @EventHandler
    public void onAnvilInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() != Material.ANVIL && block.getType() != Material.CHIPPED_ANVIL && block.getType() != Material.DAMAGED_ANVIL) {
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean shouldOverride = this.plugin.getOverlayState(uuid);
        if (shouldOverride) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                Inventory customInv = Bukkit.createInventory((InventoryHolder)player, (int)9, (String)(ChatColor.GREEN + "SuperFM Open"));
                this.setupCustomGUI(customInv);
                player.openInventory(customInv);
            });
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
            player.sendMessage(ChatColor.GREEN + "\u4f60\u6253\u5f00\u4e86 SuperFM GUI\uff01");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            event.setCancelled(false);
            return;
        }
        String title = event.getView().getTitle();
        if (title == null || !title.equals(ChatColor.GREEN + "SuperFM Open")) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        Inventory topInventory = event.getView().getTopInventory();
        int slot = event.getRawSlot();
        if (slot < topInventory.getSize()) {
            if (slot == 0 || slot == 1) {
                event.setCancelled(false);
            } else if (slot == 2 || slot == 3 || slot == 4 || slot == 5 || slot == 6 || slot == 7 || slot == 8) {
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
                return;
            }
        }
        if (slot == 8) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                ItemStack slot0 = event.getInventory().getItem(0);
                ItemStack slot1 = event.getInventory().getItem(1);
                player.closeInventory();
                this.executeCommandAsOp(player, "lfm overlay 0");
                if (slot0 != null) {
                    player.getInventory().addItem(new ItemStack[]{slot0});
                }
                if (slot1 != null) {
                    player.getInventory().addItem(new ItemStack[]{slot1});
                }
                player.sendMessage(ChatColor.YELLOW + "\u5df2\u5207\u6362\u56de\u539f\u7248\u94c1\u7827\u6a21\u5f0f");
            });
            return;
        }
        if (slot == 5) {
            this.executeCommandAsOp(player, "lfm select");
        } else if (slot == 6) {
            this.executeCommandAsOp(player, "lfm divi");
        }
        if (slot == 2) {
            Inventory inv = event.getInventory();
            ItemStack item0 = inv.getItem(0);
            ItemStack item1 = inv.getItem(1);
            if (item0 == null || item1 == null) {
                player.sendMessage(ChatColor.RED + "\u8bf7\u5728\u683c0\u548c\u683c1\u653e\u5165\u7269\u54c1\u3002");
                return;
            }
            if (item1.getType() == Material.ENCHANTED_BOOK && item1.getItemMeta() instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)item1.getItemMeta();
                if (bookMeta.getStoredEnchants().isEmpty()) {
                    player.sendMessage(ChatColor.RED + "\u9644\u9b54\u4e66\u4e2d\u6ca1\u6709\u9644\u9b54\u3002");
                    return;
                }
                int enchantCount = Math.max(item0.getEnchantments().size(), 1);
                double cost = this.plugin.getConfig().getDouble("open_experience_cost") * (double)enchantCount;
                if ((double)player.getLevel() < cost) {
                    player.sendMessage(ChatColor.RED + "\u4f60\u7684\u7ecf\u9a8c\u4e0d\u8db3\u4ee5\u5b8c\u6210\u8be5\u64cd\u4f5c\u3002");
                    return;
                }
                player.setLevel(player.getLevel() - (int)cost);
                for (Enchantment ench : bookMeta.getStoredEnchants().keySet()) {
                    int level = (Integer)bookMeta.getStoredEnchants().get(ench);
                    item0.addUnsafeEnchantment(ench, level);
                }
                inv.setItem(1, null);
                player.updateInventory();
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                player.sendMessage(ChatColor.GREEN + "\u9644\u9b54\u6210\u529f\uff01");
            } else if (item0.getType() == item1.getType()) {
                int enchCount0 = item0.getEnchantments().size();
                int enchCount1 = item1.getEnchantments().size();
                if (enchCount0 > 0 || enchCount1 > 0) {
                    float mergeCostMultiplier = (float)this.plugin.getConfig().getDouble("merge_experience_cost", 1.0);
                    int totalEnchants = enchCount0 + enchCount1;
                    double xpCost = mergeCostMultiplier * (float)totalEnchants;
                    if ((double)player.getLevel() < xpCost) {
                        player.sendMessage(ChatColor.RED + "\u4f60\u7684\u7ecf\u9a8c\u4e0d\u8db3\u4ee5\u8fdb\u884c\u7269\u54c1\u5408\u5e76\u3002");
                        return;
                    }
                    player.setLevel(player.getLevel() - (int)xpCost);
                    ItemStack merged = item0.clone();
                    for (Enchantment ench : item0.getEnchantments().keySet()) {
                        int lvl = item0.getEnchantmentLevel(ench);
                        merged.addUnsafeEnchantment(ench, lvl);
                    }
                    for (Enchantment ench : item1.getEnchantments().keySet()) {
                        int lvlExisting = merged.getEnchantmentLevel(ench);
                        int lvlNew = item1.getEnchantmentLevel(ench);
                        int mergedLevel = Math.max(lvlExisting, lvlNew);
                        merged.addUnsafeEnchantment(ench, mergedLevel);
                    }
                    short maxDurability = item0.getType().getMaxDurability();
                    short damage0 = item0.getDurability();
                    short damage1 = item1.getDurability();
                    int bonus = (int)((double)maxDurability * 0.12);
                    short newDamage = (short)Math.max(0, damage0 + damage1 - bonus);
                    merged.setDurability(newDamage);
                    inv.setItem(0, merged);
                    inv.setItem(1, null);
                    player.updateInventory();
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                    player.sendMessage(ChatColor.GREEN + "\u5408\u5e76\u9644\u9b54\u5e76\u4fee\u590d\u8010\u4e45\u6210\u529f\uff01");
                } else if (this.isRepairCombination(item0, item1)) {
                    int repairCost = this.plugin.getConfig().getInt("repair_experience_cost", 5);
                    if (player.getLevel() < repairCost) {
                        player.sendMessage(ChatColor.RED + "\u4f60\u7684\u7ecf\u9a8c\u4e0d\u8db3\u4ee5\u4fee\u590d\u8be5\u7269\u54c1\u3002");
                        return;
                    }
                    player.setLevel(player.getLevel() - repairCost);
                    item0.setDurability((short)0);
                    inv.setItem(0, item0);
                    inv.setItem(1, null);
                    player.updateInventory();
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                    player.sendMessage(ChatColor.GREEN + "\u7269\u54c1\u4fee\u590d\u6210\u529f\uff01");
                } else {
                    player.sendMessage(ChatColor.RED + "\u7269\u54c1\u4e0d\u5339\u914d\uff0c\u65e0\u6cd5\u8fdb\u884c\u64cd\u4f5c\u3002");
                }
            } else {
                player.sendMessage(ChatColor.RED + "\u7269\u54c1\u7c7b\u578b\u4e0d\u5339\u914d\u6216\u65e0\u6548\u7ec4\u5408\u3002");
            }
        }
    }

    private boolean isRepairCombination(ItemStack tool, ItemStack material) {
        Material toolType = tool.getType();
        Material matType = material.getType();
        switch (toolType) {
            case WOODEN_PICKAXE: 
            case WOODEN_AXE: 
            case WOODEN_SWORD: 
            case WOODEN_SHOVEL: 
            case WOODEN_HOE: {
                return matType == Material.OAK_PLANKS;
            }
            case STONE_PICKAXE: 
            case STONE_AXE: 
            case STONE_SWORD: 
            case STONE_SHOVEL: 
            case STONE_HOE: {
                return matType == Material.COBBLESTONE;
            }
            case IRON_PICKAXE: 
            case IRON_AXE: 
            case IRON_SWORD: 
            case IRON_SHOVEL: 
            case IRON_HOE: 
            case IRON_HELMET: 
            case IRON_CHESTPLATE: 
            case IRON_LEGGINGS: 
            case IRON_BOOTS: 
            case CHAINMAIL_HELMET: 
            case CHAINMAIL_CHESTPLATE: 
            case CHAINMAIL_LEGGINGS: 
            case CHAINMAIL_BOOTS: 
            case BUCKET: 
            case SHEARS: {
                return matType == Material.IRON_INGOT;
            }
            case GOLDEN_PICKAXE: 
            case GOLDEN_AXE: 
            case GOLDEN_SWORD: 
            case GOLDEN_SHOVEL: 
            case GOLDEN_HOE: 
            case GOLDEN_HELMET: 
            case GOLDEN_CHESTPLATE: 
            case GOLDEN_LEGGINGS: 
            case GOLDEN_BOOTS: {
                return matType == Material.GOLD_INGOT;
            }
            case DIAMOND_PICKAXE: 
            case DIAMOND_AXE: 
            case DIAMOND_SWORD: 
            case DIAMOND_SHOVEL: 
            case DIAMOND_HOE: 
            case DIAMOND_HELMET: 
            case DIAMOND_CHESTPLATE: 
            case DIAMOND_LEGGINGS: 
            case DIAMOND_BOOTS: {
                return matType == Material.DIAMOND;
            }
            case NETHERITE_PICKAXE: 
            case NETHERITE_AXE: 
            case NETHERITE_SWORD: 
            case NETHERITE_SHOVEL: 
            case NETHERITE_HOE: 
            case NETHERITE_HELMET: 
            case NETHERITE_CHESTPLATE: 
            case NETHERITE_LEGGINGS: 
            case NETHERITE_BOOTS: {
                return matType == Material.NETHERITE_SCRAP;
            }
            case LEATHER_HELMET: 
            case LEATHER_CHESTPLATE: 
            case LEATHER_LEGGINGS: 
            case LEATHER_BOOTS: 
            case LEATHER_HORSE_ARMOR: {
                return matType == Material.LEATHER;
            }
            case BOW: 
            case CROSSBOW: {
                return matType == Material.STRING;
            }
            case FISHING_ROD: {
                return matType == Material.STRING || matType == Material.BAMBOO;
            }
            case TRIDENT: {
                return matType == Material.PRISMARINE_SHARD;
            }
            case SHIELD: {
                return matType == Material.OAK_PLANKS;
            }
            case ELYTRA: {
                return matType == Material.PHANTOM_MEMBRANE;
            }
            case TURTLE_HELMET: {
                return matType == Material.SCUTE;
            }
            case CARROT_ON_A_STICK: 
            case WARPED_FUNGUS_ON_A_STICK: {
                return matType == Material.FISHING_ROD;
            }
        }
        return false;
    }
}

