package dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.CommonButtons;
import appeng.util.Platform;
import dev.hail.ae_crystal_industries.gui.IFormattedTextScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SunlightResonanceGeneratorScreen extends UpgradeableScreen<SunlightResonanceGeneratorMenu> implements IFormattedTextScreen {

    private static final Blitter LIGHT_LEVEL = Blitter.texture("guis/ae_crystal_industries/sunlight_resonance_generator.png").src(176, 0, 20, 21);

    public SunlightResonanceGeneratorScreen(SunlightResonanceGeneratorMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        addToLeftToolbar(CommonButtons.togglePowerUnit());
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX,
                       int mouseY) {
        if (!menu.canSeeSky) {
            guiGraphics.drawString(font,
                    Component.translatable("gui.ae_crystal_industries.sunlight_resonance_generator.cant_see_sky"),
                    50, 41, errorTextColor, true);
            return;
        }

        // By Deepseek V4.1 Flash 1017:
        // Both values are measured on the server (see SunlightResonanceGeneratorMenu#broadcastChanges):
        // the client level's getSkyDarken() is not reliable, so the light level must not be
        // recalculated here.

        // Height of the drawn bar, proportional to the current sky light level (0..15)
        int f = LIGHT_LEVEL.getSrcHeight() * menu.getSkylightLevel() / 15;

        LIGHT_LEVEL.copy()
                .src(
                        LIGHT_LEVEL.getSrcX(),
                        LIGHT_LEVEL.getSrcY() + LIGHT_LEVEL.getSrcHeight() - f,
                        LIGHT_LEVEL.getSrcWidth(),
                        f)
                .dest(20, 35 + LIGHT_LEVEL.getSrcHeight() - f)
                .blit(guiGraphics);

        guiGraphics.drawString(font,
                Component.translatable("gui.ae_crystal_industries.sunlight_resonance_generator.skylight",
                        IFormattedTextScreen.format(menu.getSkylightLevel())),
                50, 36, plainTextColor, false);
        guiGraphics.drawString(font, Platform.formatPower(menu.getEnergyPerTick(), true),
                50, 48, plainTextColor, false);
    }
}
