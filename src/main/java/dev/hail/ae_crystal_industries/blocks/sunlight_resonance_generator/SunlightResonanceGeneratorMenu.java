package dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator;

import appeng.menu.implementations.UpgradeableMenu;
import dev.hail.ae_crystal_industries.inits.ACIMenus;
import net.minecraft.world.entity.player.Inventory;

public class SunlightResonanceGeneratorMenu extends UpgradeableMenu<SunlightResonanceGeneratorBlockEntity> {
    private final SunlightResonanceGeneratorBlockEntity sunlightResonanceGenerator;

    public SunlightResonanceGeneratorMenu(int id, Inventory ip, SunlightResonanceGeneratorBlockEntity host) {
        super(ACIMenus.SUNLIGHT_RESONANCE_GENERATOR_MENU, id, ip, host);
        this.sunlightResonanceGenerator = host;
    }

    public double getEnergyPerTick() {
        return this.sunlightResonanceGenerator.getEnergyPerTick();
    }

    public int getSkylightLevel() {
        return this.sunlightResonanceGenerator.getSkyLightLevel();
    }
}
