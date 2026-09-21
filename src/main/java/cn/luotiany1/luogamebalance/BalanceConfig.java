package cn.luotiany1.luogamebalance;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Material;

public final class BalanceConfig {
    private final LuoGameBalancePlugin plugin;
    public BalanceConfig(LuoGameBalancePlugin plugin) { this.plugin = plugin; }
    private FileConfiguration c() { return plugin.getConfig(); }
    public boolean anvilOverlay() { return c().getBoolean("gui.anvil-overlay-default", true); }
    public boolean smithingOverlay() { return c().getBoolean("gui.smithing-overlay-default", true); }
    public boolean experimental(String feature) { return c().getBoolean("experimental." + feature + ".enabled", false); }
    public int cost(String feature, int units) {
        int stable = switch (feature) {
            case "extract" -> c().getInt("stable.costs.extract", 7);
            case "split" -> c().getInt("stable.costs.split-per-enchantment", 7) * units;
            case "merge" -> c().getInt("stable.costs.merge-per-enchantment", 7) * units;
            default -> c().getInt("stable.costs." + feature, 35);
        };
        if (!experimental(feature)) return Math.max(0, stable);
        return Math.max(0, c().getInt("experimental." + feature + ".base-xp", stable) * Math.max(1, units));
    }
    public double deathRatio() { return Math.clamp(experimental("death") ? c().getDouble("experimental.death.retain-ratio", .5) : c().getDouble("stable.death-xp-retain-ratio", 1), 0, 1); }
    public Material upgradeTemplate() {
        String key = c().getString("experimental.upgrade.template", "NETHERITE_UPGRADE_SMITHING_TEMPLATE");
        try { return Material.valueOf(key); } catch (IllegalArgumentException e) { plugin.getLogger().warning("Invalid experimental upgrade template: " + key); return Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE; }
    }
}
