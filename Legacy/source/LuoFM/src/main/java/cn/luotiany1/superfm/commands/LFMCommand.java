/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package cn.luotiany1.superfm.commands;

import cn.luotiany1.superfm.divi.DiviCommandHandler;
import cn.luotiany1.superfm.forge.FOpenCommandHandler;
import cn.luotiany1.superfm.forge.FOverlayCommandHandler;
import cn.luotiany1.superfm.main;
import cn.luotiany1.superfm.open.OpenCommandHandler;
import cn.luotiany1.superfm.select.SelectCommandHandler;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LFMCommand
implements CommandExecutor,
TabCompleter {
    private main plugin;

    public LFMCommand(main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sub;
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u53ea\u6709\u73a9\u5bb6\u624d\u80fd\u4f7f\u7528\u8be5\u547d\u4ee4\u3002");
            return true;
        }
        Player player = (Player)sender;
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "\u7528\u6cd5: /lfm <open|select|divi|overlay|fopen|foverlay|reload|preload>");
            return true;
        }
        switch (sub = args[0].toLowerCase()) {
            case "open": {
                new OpenCommandHandler(this.plugin).handle(player);
                break;
            }
            case "select": {
                new SelectCommandHandler(this.plugin).handle(player);
                break;
            }
            case "divi": {
                new DiviCommandHandler(this.plugin).handle(player);
                break;
            }
            case "overlay": {
                this.handleOverlayCommand(player, args);
                break;
            }
            case "fopen": {
                new FOpenCommandHandler(this.plugin).handle(player);
                break;
            }
            case "foverlay": {
                new FOverlayCommandHandler(this.plugin).handle(player, args);
                break;
            }
            case "reload": {
                this.reloadPlugin(player);
                break;
            }
            case "preload": {
                this.reloadAllPlugins(player);
                break;
            }
            default: {
                player.sendMessage(ChatColor.RED + "\u672a\u77e5\u5b50\u547d\u4ee4\u3002\u7528\u6cd5: /lfm <open|select|divi|overlay|fopen|foverlay|reload|preload>");
            }
        }
        return true;
    }

    private void reloadPlugin(Player player) {
        try {
            this.plugin.reloadConfig();
            player.sendMessage(ChatColor.GREEN + "\u63d2\u4ef6\u5df2\u91cd\u65b0\u52a0\u8f7d\uff01");
        }
        catch (Exception e) {
            player.sendMessage(ChatColor.RED + "\u91cd\u8f7d\u63d2\u4ef6\u65f6\u53d1\u751f\u9519\u8bef\uff1a" + e.getMessage());
        }
    }

    private void reloadAllPlugins(Player player) {
        try {
            for (Plugin plugin : this.plugin.getServer().getPluginManager().getPlugins()) {
                plugin.getPluginLoader().disablePlugin(plugin);
                plugin.getPluginLoader().enablePlugin(plugin);
            }
            player.sendMessage(ChatColor.GREEN + "\u6240\u6709\u63d2\u4ef6\u5df2\u91cd\u65b0\u52a0\u8f7d\uff01");
        }
        catch (Exception e) {
            player.sendMessage(ChatColor.RED + "\u91cd\u8f7d\u6240\u6709\u63d2\u4ef6\u65f6\u53d1\u751f\u9519\u8bef\uff1a" + e.getMessage());
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            if ("open".startsWith(partial)) {
                completions.add("open");
            }
            if ("select".startsWith(partial)) {
                completions.add("select");
            }
            if ("divi".startsWith(partial)) {
                completions.add("divi");
            }
            if ("overlay".startsWith(partial)) {
                completions.add("overlay");
            }
            if ("fopen".startsWith(partial)) {
                completions.add("fopen");
            }
            if ("foverlay".startsWith(partial)) {
                completions.add("foverlay");
            }
            if ("reload".startsWith(partial)) {
                completions.add("reload");
            }
            if ("preload".startsWith(partial)) {
                completions.add("preload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("foverlay")) {
            completions.add("1");
            completions.add("0");
        }
        return completions;
    }

    private void handleOverlayCommand(Player player, String[] args) {
        if (!player.hasPermission("superfm.command.overlay")) {
            player.sendMessage(ChatColor.RED + "\u4f60\u6ca1\u6709\u6743\u9650\u4f7f\u7528\u6b64\u547d\u4ee4");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "\u7528\u6cd5: /lfm overlay <0|1> [\u6301\u7eed\u65f6\u95f4]");
            return;
        }
        try {
            int state = Integer.parseInt(args[1]);
            if (state != 0 && state != 1) {
                throw new NumberFormatException();
            }
            int duration = 30;
            if (args.length >= 3) {
                duration = Integer.parseInt(args[2]);
            }
            this.plugin.setPlayerOverride(player.getUniqueId(), state == 1, duration);
            String action = state == 1 ? "\u542f\u7528" : "\u7981\u7528";
            player.sendMessage(ChatColor.GREEN + "\u5df2" + action + "\u94c1\u7827\u8986\u76d6\uff0c\u6301\u7eed\u65f6\u95f4: " + duration + "\u79d2");
        }
        catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "\u53c2\u6570\u9519\u8bef\uff01\u5fc5\u987b\u4e3a0\u62161");
        }
    }
}

