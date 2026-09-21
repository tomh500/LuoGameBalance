package cn.luotiany1.luogamebalance;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class BalanceCommand implements CommandExecutor, TabCompleter {
    private final LuoGameBalancePlugin plugin;
    public BalanceCommand(LuoGameBalancePlugin plugin) { this.plugin = plugin; }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { if (sender instanceof Player p && !p.hasPermission("luogamebalance.admin.*")) plugin.tell(p, "use-workstation"); else sender.sendMessage(plugin.message("usage")); return true; }
        String sub = args[0].toLowerCase();
        if (!sender.hasPermission("luogamebalance.admin.*")) { if (sender instanceof Player p) plugin.tell(p, "use-workstation"); else sender.sendMessage(plugin.message("no-permission")); return true; }
        if (sub.equals("reload")) { plugin.reloadAll(); sender.sendMessage(plugin.message("reloaded")); return true; }
        if (sub.equals("status") || sub.equals("experimental")) { sender.sendMessage(plugin.message("status", plugin.config().experimental("extract"), plugin.config().experimental("split"), plugin.config().experimental("merge"), plugin.config().experimental("repair"), plugin.config().experimental("upgrade"), plugin.config().experimental("death"))); return true; }
        sender.sendMessage(plugin.message("usage"));
        return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { return sender.hasPermission("luogamebalance.admin.*") && args.length == 1 ? List.of("status", "reload", "experimental").stream().filter(x -> x.startsWith(args[0].toLowerCase())).toList() : List.of(); }
}
