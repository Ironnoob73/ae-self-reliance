package dev.hail.ae_crystal_industries.energy;

import appeng.api.storage.ISubMenuHost;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.InterfaceMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * By Deepseek V4.1 Flash 1017
 * Common interface implemented by both forms of the Energy Interface (the block entity and the cable part), so that a
 * single menu type can drive either of them.
 */
public interface EnergyInterfaceHost extends IUpgradeableObject, ISubMenuHost {

    long getOutputRate();

    void cycleOutputRate(long direction);

    void setOutputRate(long value);

    long getEnergyCapacity();

    void cycleEnergyCapacity(long direction);

    void setCapacity(long value);

    long getStoredEnergy();

    /**
     * @return how much AE is consumed to produce a single FE (1..4, reduced by AE2 Energy Cards).
     */
    int getAePerFe();

    boolean isActive();

    default Object getValue(String valueName) {
        switch (valueName) {
            case "outputRate" -> {
                return getOutputRate();
            }
            case "capacity" -> {
                return getEnergyCapacity();
            }
        }
        return null;
    }

    default void setValue(String valueName, long value) {
        switch (valueName) {
            case "outputRate" -> setOutputRate(value);
            case "capacity" -> setCapacity(value);
        }
    }

    @Override
    default void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(InterfaceMenu.TYPE, player, subMenu.getLocator());
    }

    @Override
    default ItemStack getMainMenuIcon() {
        return Items.AIR.getDefaultInstance();
    }
}
