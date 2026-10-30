package dev.hail.ae_crystal_industries.blocks.energy_interface;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import dev.hail.ae_crystal_industries.gui.IFormattedTextScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EnergyInterfaceScreen extends UpgradeableScreen<EnergyInterfaceMenu> implements IFormattedTextScreen {

    public EnergyInterfaceScreen(EnergyInterfaceMenu menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);

        widgets.addOpenPriorityButton();

        this.widgets.add("setOutputRate", new SetAmountButton(btn -> menu.openSetValueMenu("outputRate")));
        this.widgets.add("setCapacity", new SetAmountButton(btn -> menu.openSetValueMenu("capacity")));

        this.widgets.addButton("outputRateDown", Component.literal("<"), () -> menu.cycleOutputRate(-1));
        this.widgets.addButton("outputRateUp", Component.literal(">"), () -> menu.cycleOutputRate(1));
        this.widgets.addButton("capacityDown", Component.literal("<"), () -> menu.cycleEnergyCapacity(-1));
        this.widgets.addButton("capacityUp", Component.literal(">"), () -> menu.cycleEnergyCapacity(1));
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

        var font = Minecraft.getInstance().font;

        guiGraphics.drawString(font,
                Component.translatable("gui.ae_crystal_industries.energy_interface.output_rate",
                        IFormattedTextScreen.format(menu.outputRate)),
                8, 21, plainTextColor, false);
        guiGraphics.drawString(font,
                Component.translatable("gui.ae_crystal_industries.energy_interface.capacity",
                        IFormattedTextScreen.format(menu.energyCapacity)),
                8, 41, plainTextColor, false);
        guiGraphics.drawString(font,
                Component.translatable("gui.ae_crystal_industries.energy_interface.stored",
                        IFormattedTextScreen.format(menu.storedEnergy)),
                8, 61, plainTextColor, false);

        // Simple buffer fill bar.
        int barX = 8;
        int barY = 72;
        int barWidth = 160;
        int barHeight = 8;
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF1E1E1E);
        if (menu.energyCapacity > 0 && menu.storedEnergy > 0) {
            int filled = (int) Math.round((double) menu.storedEnergy / menu.energyCapacity * (barWidth - 2));
            filled = Math.clamp(filled, 0, barWidth - 2);
            if (filled > 0) {
                guiGraphics.fill(barX + 1, barY + 1, barX + 1 + filled, barY + barHeight - 1, 0xFF3A8FD8);
            }
        }

        guiGraphics.drawString(font,
                Component.translatable("gui.ae_crystal_industries.energy_interface.conversion", menu.aePerFe),
                8, 86, plainTextColor, false);
        guiGraphics.drawString(font,
                Component.translatable("gui.ae_crystal_industries.energy_interface.energy_cards",
                        menu.energyCards),
                8, 98, plainTextColor, false);
    }
}
