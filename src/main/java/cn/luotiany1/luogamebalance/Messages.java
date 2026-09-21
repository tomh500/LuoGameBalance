package cn.luotiany1.luogamebalance;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Messages {
    private final LuoGameBalancePlugin plugin;
    private YamlConfiguration data;
    public Messages(LuoGameBalancePlugin plugin) { this.plugin = plugin; reload(); }
    public void reload() {
        String locale = plugin.getConfig().getString("locale", "zh_CN");
        try (var in = plugin.getResource("messages_" + locale + ".yml")) {
            data = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception e) { throw new IllegalStateException("Cannot load messages for " + locale, e); }
    }
    public String get(String key, Object... values) {
        String text = data.getString(key, key);
        return ChatColor.translateAlternateColorCodes('&', data.getString("prefix", "") + String.format(text, values));
    }
    public String plain(String key, Object... values) { return ChatColor.translateAlternateColorCodes('&', String.format(data.getString(key, key), values)); }
    public List<String> list(String key, Object... values) {
        return data.getStringList(key).stream().map(x -> ChatColor.translateAlternateColorCodes('&', String.format(x, values))).toList();
    }
}
