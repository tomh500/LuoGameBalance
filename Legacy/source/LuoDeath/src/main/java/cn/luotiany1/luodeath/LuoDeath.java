/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package cn.luotiany1.luodeath;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LuoDeath
extends JavaPlugin
implements Listener {
    private float multiplier;
    private final HashMap<UUID, Integer> storedExp = new HashMap();

    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadPluginConfig();
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getLogger().info("LuoDeath \u63d2\u4ef6\u5df2\u52a0\u8f7d\uff0c\u7ecf\u9a8c\u4fdd\u7559\u500d\u7387: " + this.multiplier);
    }

    private void reloadPluginConfig() {
        this.reloadConfig();
        double val = this.getConfig().getDouble("multiplier", 1.0);
        if (val < 0.0 || val > 1.0) {
            this.getLogger().warning("\u914d\u7f6e\u6587\u4ef6\u4e2d\u7684 multiplier \u8d85\u51fa\u8303\u56f4(0~1)\uff0c\u4f7f\u7528\u9ed8\u8ba4\u503c 1.0");
            this.multiplier = 1.0f;
        } else {
            this.multiplier = (float)val;
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((command.getName().equalsIgnoreCase("Luodeath") || command.getName().equalsIgnoreCase("ld")) && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            this.reloadPluginConfig();
            sender.sendMessage("LuoDeath \u914d\u7f6e\u5df2\u91cd\u8f7d\uff0c\u5f53\u524d\u7ecf\u9a8c\u4fdd\u7559\u500d\u7387: " + this.multiplier);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int totalExp = this.getTotalExperience(player);
        int restoredExp = Math.round((float)totalExp * this.multiplier);
        this.storedExp.put(player.getUniqueId(), restoredExp);
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (this.storedExp.containsKey(playerId)) {
            int expToGive = this.storedExp.remove(playerId);
            this.getServer().getScheduler().runTaskLater((Plugin)this, () -> this.giveExperience(player, expToGive), 1L);
        }
    }

    private int getTotalExperience(Player player) {
        int level = player.getLevel();
        float progress = player.getExp();
        return this.getExpAtLevel(level) + Math.round(progress * (float)this.getExpToNextLevel(level));
    }

    private int getExpAtLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        }
        if (level <= 31) {
            return (int)(2.5 * (double)level * (double)level - 40.5 * (double)level + 360.0);
        }
        return (int)(4.5 * (double)level * (double)level - 162.5 * (double)level + 2220.0);
    }

    private int getExpToNextLevel(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        }
        if (level <= 30) {
            return 5 * level - 38;
        }
        return 9 * level - 158;
    }

    private void giveExperience(Player player, int exp) {
        player.setExp(0.0f);
        player.setLevel(0);
        player.setTotalExperience(0);
        int level = 0;
        while (exp >= this.getExpToNextLevel(level)) {
            exp -= this.getExpToNextLevel(level);
            ++level;
        }
        player.setLevel(level);
        player.setExp((float)exp / (float)this.getExpToNextLevel(level));
    }
}

