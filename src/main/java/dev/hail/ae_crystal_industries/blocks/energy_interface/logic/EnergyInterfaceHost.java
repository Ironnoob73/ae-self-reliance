package dev.hail.ae_crystal_industries.blocks.energy_interface.logic;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.IPriorityHost;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import dev.hail.ae_crystal_industries.inits.ACIMenus;
import net.minecraft.world.entity.player.Player;

/**
 * By Deepseek V4.1 Flash 1017
 * Common interface implemented by both forms of the Energy Interface (the block entity and the cable part), so that a
 * single menu type can drive either of them.
 */
public interface EnergyInterfaceHost extends IUpgradeableObject, IPriorityHost {

    String VALUE_OUTPUT_RATE = "outputRate";
    String VALUE_CAPACITY = "capacity";

    EnergyInterfaceLogic getInterfaceLogic();

    long getOutputRate();

    void cycleOutputRate(int direction);

    void setOutputRate(long value);

    long getEnergyCapacity();

    void cycleEnergyCapacity(int direction);

    void setCapacity(long value);

    long getStoredEnergy();

    /**
     * @return how much AE is consumed to produce a single FE (1..4, reduced by AE2 Energy Cards).
     */
    int getAePerFe();

    boolean isActive();

    void saveChanges();

    /**
     * @return whether the given identifier names a configurable value of this host.
     */
    default boolean hasValue(String valueName) {
        return VALUE_OUTPUT_RATE.equals(valueName) || VALUE_CAPACITY.equals(valueName);
    }

    default long getValue(String valueName) {
        if (VALUE_OUTPUT_RATE.equals(valueName)) {
            return getOutputRate();
        }
        if (VALUE_CAPACITY.equals(valueName)) {
            return getEnergyCapacity();
        }
        return 0;
    }

    /**
     * @return the highest value the given value may be set to.
     */
    default long getMaxValue(String valueName) {
        if (VALUE_OUTPUT_RATE.equals(valueName)) {
            return EnergyInterfaceLogic.MAX_OUTPUT_RATE;
        }
        if (VALUE_CAPACITY.equals(valueName)) {
            return EnergyInterfaceLogic.MAX_CAPACITY;
        }
        return 0;
    }

    default void setValue(String valueName, long value) {
        if (VALUE_OUTPUT_RATE.equals(valueName)) {
            setOutputRate(value);
        } else if (VALUE_CAPACITY.equals(valueName)) {
            setCapacity(value);
        }
    }

    @Override
    default int getPriority() {
        return getInterfaceLogic().getPriority();
    }

    @Override
    default void setPriority(int newValue) {
        getInterfaceLogic().setPriority(newValue);
    }

    @Override
    default void returnToMainMenu(Player player, ISubMenu subMenu) {
        // The main menu of this host is the Energy Interface menu, not AE2's interface menu.
        MenuOpener.returnTo(ACIMenus.ENERGY_INTERFACE_MENU, player, subMenu.getLocator());
    }
}
