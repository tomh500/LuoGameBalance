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
        if (args.length == 0) { sender.sendMessage(plugin.getConfig().getString("locale", "zh_CN").equals("zh_CN") ? "§e/luobalance <anvil|smithing|extract|split|mode|status|reload|experimental>" : "§e/luobalance <anvil|smithing|extract|split|mode|status|reload|experimental>"); return true; }
        String sub = args[0].toLowerCase();
        if (sub.equals("reload")) { if (!sender.hasPermission("luogamebalance.admin.reload")) { sender.sendMessage("§cNo permission."); return true; } plugin.reloadAll(); sender.sendMessage("§aReloaded."); return true; }
        if (sub.equals("status") || sub.equals("experimental")) { if (!sender.hasPermission("luogamebalance.admin.status")) { sender.sendMessage("§cNo permission."); return true; } sender.sendMessage("§eExperimental: extract=" + plugin.config().experimental("extract") + ", split=" + plugin.config().experimental("split") + ", merge=" + plugin.config().experimental("merge")); return true; }
        if (!(sender instanceof Player p)) { sender.sendMessage("§cPlayer-only command."); return true; }
        if (!p.hasPermission("luogamebalance.use." + sub) && !p.hasPermission("luogamebalance.use.*")) { plugin.tell(p, "no-permission"); return true; }
        switch (sub) {
            case "anvil" -> BalanceListener.open(p, GuiHolder.Kind.ANVIL);
            case "smithing" -> BalanceListener.open(p, GuiHolder.Kind.SMITHING);
            case "extract" -> plugin.service().extract(p, p.getInventory().getItemInMainHand());
            case "split" -> plugin.service().split(p, p.getInventory().getItemInMainHand());
            case "mode" -> { if (args.length < 2) { plugin.tell(p, "usage"); return true; } GuiHolder.Kind kind = args[1].equalsIgnoreCase("smithing") ? GuiHolder.Kind.SMITHING : GuiHolder.Kind.ANVIL; plugin.toggle(p.getUniqueId(), kind); plugin.tell(p, "mode", kind.name().toLowerCase(), "toggled"); }
            default -> plugin.tell(p, "usage");
        }
        return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { return args.length == 1 ? List.of("anvil", "smithing", "extract", "split", "mode", "status", "reload", "experimental").stream().filter(x -> x.startsWith(args[0].toLowerCase())).toList() : args.length == 2 && args[0].equalsIgnoreCase("mode") ? List.of("anvil", "smithing") : List.of(); }
}
