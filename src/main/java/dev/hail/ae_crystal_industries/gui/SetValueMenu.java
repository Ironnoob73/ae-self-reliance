package dev.hail.ae_crystal_industries.gui;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.guisync.GuiSync;
import appeng.menu.locator.MenuHostLocator;
import dev.hail.ae_crystal_industries.energy.EnergyInterfaceHost;
import dev.hail.ae_crystal_industries.inits.ACIMenus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

import java.util.Objects;

public class SetValueMenu extends AEBaseMenu implements ISubMenu {

    public static final String ACTION_SET_VALUE = "setValue";

    @GuiSync(1)
    private String valueName;

    @GuiSync(2)
    private long initialValue = -1;

    @GuiSync(3)
    private long maxValue = -1;

    private final EnergyInterfaceHost host;

    public SetValueMenu(int id, Inventory inventory, EnergyInterfaceHost host) {
        super(ACIMenus.SET_VALUE_MENU, id, inventory, host);
        registerClientAction(ACTION_SET_VALUE, Long.class, this::confirm);
        this.host = host;
    }

    @Override
    public ISubMenuHost getHost() {
        return host;
    }

    public static void open(ServerPlayer player, MenuHostLocator locator, String valueName, long initialValue, long maxValue) {
        MenuOpener.open(ACIMenus.SET_VALUE_MENU, player, locator);

        if (player.containerMenu instanceof SetValueMenu menu) {
            menu.setValueToChange(valueName, initialValue, maxValue);
            menu.broadcastChanges();
        }
    }

    private void setValueToChange(String valueName, long initialValue, long maxValue) {
        this.valueName = valueName;
        this.initialValue = initialValue;
        this.maxValue = maxValue;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public long getInitialValue() {
        return initialValue;
    }

    public void confirm(long value) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_VALUE, value);
            return;
        }

        if (!Objects.equals(host.getValue(valueName), value)) {
            host.returnToMainMenu(getPlayer(), this);
            return;
        }

        host.setValue(valueName, value);
        host.returnToMainMenu(getPlayer(), this);
    }
}
