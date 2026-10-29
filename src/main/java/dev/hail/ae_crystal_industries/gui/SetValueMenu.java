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

public class SetValueMenu extends AEBaseMenu implements ISubMenu {

    public static final String ACTION_SET_VALUE = "setValue";

    /**
     * AE2 synchronizes this field with {@code FriendlyByteBuf#writeUtf}, which does not accept {@code null}. It must
     * therefore never be null, otherwise opening this menu crashes while the initial data is sent to the client
     * ({@code sendAllDataToRemote} is called before {@link #open} can assign the real name).
     */
    @GuiSync(1)
    private String valueName = "";

    /**
     * The value and maximum are sent after the menu has been opened, so their defaults have to be serializable too.
     */
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
        // Keep this field non-null, it is synchronized as a raw UTF string.
        this.valueName = valueName != null ? valueName : "";
        this.initialValue = initialValue;
        this.maxValue = maxValue;
    }

    /**
     * @return true once the server has sent the actual value/maximum to this menu (the client's initial copy only
     *         contains the defaults).
     */
    public boolean hasValue() {
        return !valueName.isEmpty() && maxValue >= 0;
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

        if (host.hasValue(valueName)) {
            // The client is not trusted with the limits, apply them again on the server.
            long max = host.getMaxValue(valueName);
            if (maxValue >= 0) {
                max = Math.min(max, maxValue);
            }
            host.setValue(valueName, Math.clamp(value, 0L, max));
        }

        host.returnToMainMenu(getPlayer(), this);
    }
}
