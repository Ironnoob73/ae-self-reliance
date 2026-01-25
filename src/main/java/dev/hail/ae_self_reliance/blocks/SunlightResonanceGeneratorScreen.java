package dev.hail.ae_self_reliance.blocks;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.CommonButtons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SunlightResonanceGeneratorScreen extends UpgradeableScreen<SunlightResonanceGeneratorMenu> {

    public SunlightResonanceGeneratorScreen(SunlightResonanceGeneratorMenu menu, Inventory playerInventory,
                                  Component title) {

        super(menu, playerInventory, title, StyleManager.loadStyleDoc("/screens/sunlight_resonance_generator.json"));
        addToLeftToolbar(CommonButtons.togglePowerUnit());
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX,
                       int mouseY) {
    }

}