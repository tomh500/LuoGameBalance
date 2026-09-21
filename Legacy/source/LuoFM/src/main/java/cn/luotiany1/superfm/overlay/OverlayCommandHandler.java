/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package cn.luotiany1.superfm.overlay;

import cn.luotiany1.superfm.main;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OverlayCommandHandler {
    private main plugin;

    public OverlayCommandHandler(main plugin) {
        this.plugin = plugin;
    }

    public void handle(Player player, String[] args) {
        UUID playerUUID = player.getUniqueId();
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "\u7528\u6cd5: /lfm overlay <1|0>");
            return;
        }
        String value = args[1];
        if (!value.equals("1") && !value.equals("0")) {
            player.sendMessage(ChatColor.RED + "\u53c2\u6570\u9519\u8bef\uff0c\u8bf7\u8f93\u5165 1 \u6216 0");
            return;
        }
        boolean overlayEnabled = value.equals("1");
        this.plugin.getOverlaySettings().put(playerUUID, overlayEnabled);
        player.sendMessage(ChatColor.GREEN + "Overlay \u529f\u80fd\u5df2" + (overlayEnabled ? "\u5f00\u542f" : "\u5173\u95ed") + "\u3002");
    }
}

