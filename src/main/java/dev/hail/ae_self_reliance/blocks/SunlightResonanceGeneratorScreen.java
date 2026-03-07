package dev.hail.ae_self_reliance.blocks;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.CommonButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.LightLayer;

public class SunlightResonanceGeneratorScreen extends UpgradeableScreen<SunlightResonanceGeneratorMenu> {

    private static final Blitter LIGHT_LEVEL = Blitter.texture("guis/ae_self_reliance/sunlight_resonance_generator.png").src(176, 0, 20, 21);

    public SunlightResonanceGeneratorScreen(SunlightResonanceGeneratorMenu menu, Inventory playerInventory,
                                  Component title) {

        super(menu, playerInventory, title, StyleManager.loadStyleDoc("/screens/asr_sunlight_resonance_generator.json"));
        addToLeftToolbar(CommonButtons.togglePowerUnit());
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX,
                       int mouseY) {
        if (Minecraft.getInstance().level != null && menu.getBlockEntity() != null) {
            int bright = Minecraft.getInstance().level.getBrightness(LightLayer.SKY, menu.getBlockEntity().getBlockPos().above());
            int dark = Minecraft.getInstance().level.getSkyDarken();
            int skyLight = bright - dark;
            int f = (LIGHT_LEVEL.getSrcHeight() / 15) * skyLight;
            LIGHT_LEVEL.copy()
                    .src(
                            LIGHT_LEVEL.getSrcX(),
                            LIGHT_LEVEL.getSrcY() + LIGHT_LEVEL.getSrcHeight() - f,
                            LIGHT_LEVEL.getSrcWidth(),
                            f)
                    .dest(78, 35 + LIGHT_LEVEL.getSrcHeight() - f)
                    .blit(guiGraphics);
        }
    }

}