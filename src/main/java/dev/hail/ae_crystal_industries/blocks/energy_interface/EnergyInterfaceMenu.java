package dev.hail.ae_crystal_industries.blocks.energy_interface;

import appeng.core.definitions.AEItems;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.SetStockAmountMenu;
import appeng.menu.implementations.UpgradeableMenu;
import dev.hail.ae_crystal_industries.energy.EnergyInterfaceHost;
import dev.hail.ae_crystal_industries.energy.EnergyInterfaceLogic;
import dev.hail.ae_crystal_industries.gui.SetValueMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

/**
 * Menu shared by the block and the cable part form of the Energy Interface.
 */
public class EnergyInterfaceMenu extends UpgradeableMenu<EnergyInterfaceHost> {

    public static final String ACTION_SET_OUTPUT_VALUE = "setOutputValue";
    private static final String ACTION_CYCLE_OUTPUT_RATE = "cycleOutputRate";
    private static final String ACTION_CYCLE_CAPACITY = "cycleEnergyCapacity";

    @GuiSync(10)
    public long outputRate = EnergyInterfaceLogic.DEFAULT_OUTPUT_RATE;

    @GuiSync(11)
    public long energyCapacity = EnergyInterfaceLogic.DEFAULT_CAPACITY;

    @GuiSync(12)
    public long storedEnergy;

    @GuiSync(13)
    public int aePerFe = EnergyInterfaceLogic.MAX_AE_PER_FE;

    @GuiSync(14)
    public int energyCards;

    public EnergyInterfaceMenu(MenuType<EnergyInterfaceMenu> menuType, int id, Inventory playerInventory,
            EnergyInterfaceHost host) {
        super(menuType, id, playerInventory, host);
        registerClientAction(ACTION_SET_OUTPUT_VALUE, String.class, this::openSetValueMenu);
        registerClientAction(ACTION_CYCLE_OUTPUT_RATE, Long.class, this::handleCycleOutputRate);
        registerClientAction(ACTION_CYCLE_CAPACITY, Long.class, this::handleCycleCapacity);
    }

    public void cycleOutputRate(long direction) {
        if (isClientSide()) {
            sendClientAction(ACTION_CYCLE_OUTPUT_RATE, direction);
        } else {
            handleCycleOutputRate(direction);
        }
    }

    public void cycleEnergyCapacity(long direction) {
        if (isClientSide()) {
            sendClientAction(ACTION_CYCLE_CAPACITY, direction);
        } else {
            handleCycleCapacity(direction);
        }
    }

    private void handleCycleOutputRate(long direction) {
        getHost().cycleOutputRate(direction);
        syncFromHost();
    }

    private void handleCycleCapacity(long direction) {
        getHost().cycleEnergyCapacity(direction);
        syncFromHost();
    }

    private void syncFromHost() {
        this.outputRate = getHost().getOutputRate();
        this.energyCapacity = getHost().getEnergyCapacity();
        this.storedEnergy = getHost().getStoredEnergy();
        this.aePerFe = getHost().getAePerFe();
        this.energyCards = getHost().getUpgrades().getInstalledUpgrades(AEItems.ENERGY_CARD);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            syncFromHost();
        }
        super.broadcastChanges();
    }

    public void openSetValueMenu(String valueName) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_OUTPUT_VALUE, valueName);
        } else {
            Object value = getHost().getValue(valueName);
            if (value != null) {
                SetValueMenu.open((ServerPlayer) getPlayer(), getLocator(), valueName, (Long) value, Long.MAX_VALUE);
            }
        }
    }
}
