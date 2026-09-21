/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package cn.luotiany1.superfm.forge;

import cn.luotiany1.superfm.main;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FOverlayCommandHandler {
    private main plugin;

    public FOverlayCommandHandler(main plugin) {
        this.plugin = plugin;
    }

    public void handle(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "\u7528\u6cd5: /lfm foverlay <1|0>");
            return;
        }
        String value = args[1];
        if (!value.equals("1") && !value.equals("0")) {
            player.sendMessage(ChatColor.RED + "\u53c2\u6570\u9519\u8bef\uff0c\u8bf7\u8f93\u5165 1 \u6216 0");
            return;
        }
        boolean enabled = value.equals("1");
        this.plugin.getFOverlaySettings().put(playerUUID, enabled);
        player.sendMessage(ChatColor.GREEN + "\u953b\u9020\u53f0 Overlay \u8bbe\u7f6e\u5df2\u66f4\u65b0: " + (enabled ? "\u542f\u7528" : "\u7981\u7528"));
    }
}

