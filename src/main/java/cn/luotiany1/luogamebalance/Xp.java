package cn.luotiany1.luogamebalance;

import org.bukkit.entity.Player;
import org.bukkit.GameMode;

public final class Xp {
    private Xp() {}

    public static int total(Player p) {
        return atLevel(p.getLevel()) + Math.round(p.getExp() * toNext(p.getLevel()));
    }

    public static int atLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    public static int toNext(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    public static boolean take(Player p, int amount) {
        if (!canPay(p, amount)) return false;
        if (p.getGameMode() == GameMode.CREATIVE) return true;
        set(p, total(p) - amount);
        return true;
    }

    public static boolean canPay(Player p, int amount) {
        return amount >= 0 && (p.getGameMode() == GameMode.CREATIVE || total(p) >= amount);
    }

    public static void set(Player p, int amount) {
        amount = Math.max(0, amount);
        int level = 0;
        while (amount >= toNext(level)) { amount -= toNext(level); level++; }
        p.setLevel(level);
        p.setExp((float) amount / toNext(level));
    }
}
