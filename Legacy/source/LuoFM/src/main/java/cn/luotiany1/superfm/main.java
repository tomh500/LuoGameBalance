/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitTask
 */
package cn.luotiany1.superfm;

import cn.luotiany1.superfm.commands.LFMCommand;
import cn.luotiany1.superfm.forge.FInventoryListener;
import cn.luotiany1.superfm.listeners.LFMInventoryListener;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class main
extends JavaPlugin {
    private Map<UUID, Boolean> playerOverrides = new HashMap<UUID, Boolean>();
    private Map<UUID, BukkitTask> resetTasks = new HashMap<UUID, BukkitTask>();
    private Map<UUID, Boolean> overlaySettings = new HashMap<UUID, Boolean>();
    private Map<UUID, Boolean> fOverlaySettings = new HashMap<UUID, Boolean>();
    private boolean overlayDefault;
    private boolean fOverlayDefault;

    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();
        this.overlayDefault = this.getConfig().getBoolean("overlay_default", true);
        this.fOverlayDefault = this.getConfig().getBoolean("foverlay_default", true);
        this.getCommand("lfm").setExecutor((CommandExecutor)new LFMCommand(this));
        this.getCommand("lfm").setTabCompleter((TabCompleter)new LFMCommand(this));
        this.getServer().getPluginManager().registerEvents((Listener)new LFMInventoryListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new FInventoryListener(this), (Plugin)this);
    }

    public void onDisable() {
        this.resetTasks.values().forEach(BukkitTask::cancel);
        this.resetTasks.clear();
    }

    public boolean getOverlayState(UUID uuid) {
        if (this.playerOverrides.containsKey(uuid)) {
            return this.playerOverrides.get(uuid);
        }
        return this.overlaySettings.getOrDefault(uuid, this.overlayDefault);
    }

    public void setPlayerOverride(UUID uuid, boolean state, int duration) {
        this.playerOverrides.put(uuid, state);
        if (this.resetTasks.containsKey(uuid)) {
            this.resetTasks.get(uuid).cancel();
        }
        if (duration > 0) {
            BukkitTask task = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                this.playerOverrides.remove(uuid);
                this.resetTasks.remove(uuid);
            }, (long)duration * 20L);
            this.resetTasks.put(uuid, task);
        }
    }

    public Map<UUID, Boolean> getOverlaySettings() {
        return this.overlaySettings;
    }

    public boolean getOverlayDefault() {
        return this.overlayDefault;
    }

    public Map<UUID, Boolean> getFOverlaySettings() {
        return this.fOverlaySettings;
    }

    public boolean getFOverlayDefault() {
        return this.fOverlayDefault;
    }
}

