package cn.luotiany1.luogamebalance;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class GuiHolder implements InventoryHolder {
    public enum Kind { ANVIL, SMITHING }
    private final Kind kind;
    public GuiHolder(Kind kind) { this.kind = kind; }
    public Kind kind() { return kind; }
    @Override public Inventory getInventory() { return null; }
}
