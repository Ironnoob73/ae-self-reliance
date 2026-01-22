package dev.hail.ae_self_reliance.blocks;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.CommonButtons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SunlightResonanceGeneratorScreen extends UpgradeableScreen<SunlightResonanceGeneratorMenu> {

    // Burn indicator similar to the "flame" in a vanilla furnace
    private static final Blitter BURN_PROGRESS = Blitter.texture("guis/vibchamber.png").src(176, 0, 14, 13);

    // "Progress-bar" that indicates the energy generation rate
    //private final ProgressBar generationRateBar;

    public SunlightResonanceGeneratorScreen(SunlightResonanceGeneratorMenu menu, Inventory playerInventory,
                                  Component title) {

        super(menu, playerInventory, title, StyleManager.loadStyleDoc("/screens/vibration_chamber.json"));
        //super(menu, playerInventory, title, style);

        //this.generationRateBar = new ProgressBar(this.menu, style.getImage("generationRateBar"),
        //        ProgressBar.Direction.VERTICAL);
        //widgets.add("generationRateBar", this.generationRateBar);

        addToLeftToolbar(CommonButtons.togglePowerUnit());
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        //var powerPerTick = this.menu.getPowerPerTick();
        //var efficiency = this.menu.getFuelEfficiency();
        //this.generationRateBar.setFullMsg(
        //        Component.literal(Platform.formatPower(powerPerTick, true) + "\n" + "Eff: " + efficiency + "%"));
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX,
                       int mouseY) {
        // Show the flame "burning down" as we burn through an item of fuel
        /*if (this.menu.getRemainingBurnTime() > 0) {
            int f = this.menu.getRemainingBurnTime() * BURN_PROGRESS.getSrcHeight() / 100;
            BURN_PROGRESS.copy()
                    .src(
                            BURN_PROGRESS.getSrcX(),
                            BURN_PROGRESS.getSrcY() + BURN_PROGRESS.getSrcHeight() - f,
                            BURN_PROGRESS.getSrcWidth(),
                            f)
                    .dest(80, 20 + BURN_PROGRESS.getSrcHeight() - f)
                    .blit(guiGraphics);
        }*/
    }

}