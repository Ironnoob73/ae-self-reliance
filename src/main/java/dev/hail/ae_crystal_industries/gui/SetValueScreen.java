package dev.hail.ae_crystal_industries.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.NumberEntryWidget;
import appeng.core.localization.GuiText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SetValueScreen extends AEBaseScreen<SetValueMenu> {

    private final NumberEntryWidget value;
    private boolean valueInitialized;

    public SetValueScreen(SetValueMenu menu, Inventory playerInventory, Component title,
                                 ScreenStyle style) {
        super(menu, playerInventory, title, style);

        widgets.addButton("save", GuiText.Set.text(), this::confirm);
        AESubScreen.addBackButton(menu, "back", widgets);

        this.value = widgets.addNumberEntryWidget("value", NumberEntryType.ENERGY);
        this.value.setLongValue(1);
        this.value.setTextFieldStyle(style.getWidget("valueSet"));
        this.value.setMinValue(0);
        this.value.setHideValidationIcon(true);
        this.value.setOnConfirm(this::confirm);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        if (!this.valueInitialized) {
            this.value.setLongValue(menu.getInitialValue());
            this.value.setMaxValue(menu.getMaxValue());
            this.valueInitialized = true;
        }
    }

    private void confirm() {
        this.value.getLongValue().ifPresent(menu::confirm);
    }
}
