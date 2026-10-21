package dev.hail.ae_crystal_industries.blocks;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.CommonButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.LightLayer;

public class SunlightResonanceGeneratorScreen extends UpgradeableScreen<SunlightResonanceGeneratorMenu> {

    private static final Blitter LIGHT_LEVEL = Blitter.texture("guis/ae_self_reliance/sunlight_resonance_generator.png").src(176, 0, 20, 21);

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
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        BlockPos pos = menu.getBlockEntity() != null ? menu.getBlockEntity().getBlockPos() : null;
        int skyLight = 0;
        if (pos != null && level.dimensionType().hasSkyLight()) {
            // Same measurement as the server-side generator: the block is opaque, so read the
            // sky light of the block above it (mirrors the vanilla daylight detector formula).
            skyLight = Math.max(0,
                    Math.min(15, level.getBrightness(LightLayer.SKY, pos.above()) - level.getSkyDarken()));
        }

        // Height of the drawn bar, proportional to the current sky light level (0..15)
        int f = LIGHT_LEVEL.getSrcHeight() * skyLight / 15;
        if (f <= 0) {
            return;
        }

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
