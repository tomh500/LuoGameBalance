package cn.luotiany1.luogamebalance;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class LuoGameBalancePlugin extends JavaPlugin {
    private final Map<UUID, Boolean> anvilMode = new HashMap<>(), smithingMode = new HashMap<>(), nativeAnvil = new HashMap<>(), nativeSmithing = new HashMap<>();
    private BalanceConfig config; private Messages messages; private BalanceService service;
    @Override public void onEnable() {
        saveDefaultConfig(); reloadAll();
        var command = new BalanceCommand(this);
        getCommand("luogamebalance").setExecutor(command); getCommand("luogamebalance").setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new BalanceListener(this), this);
    }
    @Override public void onDisable() { anvilMode.clear(); smithingMode.clear(); nativeAnvil.clear(); nativeSmithing.clear(); }
    public void reloadAll() { reloadConfig(); config = new BalanceConfig(this); messages = new Messages(this); service = new BalanceService(this); }
    public BalanceConfig config() { return config; } public BalanceService service() { return service; }
    public void tell(Player p, String key, Object... values) { p.sendMessage(messages.get(key, values)); }
    public boolean anvil(UUID id) { return anvilMode.getOrDefault(id, config.anvilOverlay()); }
    public boolean smithing(UUID id) { return smithingMode.getOrDefault(id, config.smithingOverlay()); }
    public void toggle(UUID id, GuiHolder.Kind kind) { Map<UUID, Boolean> map = kind == GuiHolder.Kind.ANVIL ? anvilMode : smithingMode; boolean old = kind == GuiHolder.Kind.ANVIL ? anvil(id) : smithing(id); map.put(id, !old); }
    public void allowNativeOnce(UUID id, GuiHolder.Kind kind) { (kind == GuiHolder.Kind.ANVIL ? nativeAnvil : nativeSmithing).put(id, true); }
    public boolean consumeNativeOnce(UUID id, GuiHolder.Kind kind) { return (kind == GuiHolder.Kind.ANVIL ? nativeAnvil : nativeSmithing).remove(id) != null; }
    public String message(String key, Object... values) { return messages.get(key, values); }
    public String plainMessage(String key, Object... values) { return messages.plain(key, values); }
    public java.util.List<String> messageList(String key, Object... values) { return messages.list(key, values); }
}
