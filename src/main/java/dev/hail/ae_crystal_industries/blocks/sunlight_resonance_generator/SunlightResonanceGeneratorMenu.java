package dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator;

import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import dev.hail.ae_crystal_industries.inits.ACIMenus;
import net.minecraft.world.entity.player.Inventory;

public class SunlightResonanceGeneratorMenu extends UpgradeableMenu<SunlightResonanceGeneratorBlockEntity> {
    private final SunlightResonanceGeneratorBlockEntity sunlightResonanceGenerator;

    /**
     * By Deepseek V4.1 Flash 1017:
     * Sky light level as measured on the server. The server is the only side that maintains
     * {@link net.minecraft.world.level.Level#getSkyDarken()}, so the value is computed there and
     * synced to the client instead of being recalculated by the client-side screen (where the
     * sky darkening would always be 0).
     */
    @GuiSync(10)
    public int skyLightLevel;

    /**
     * By Deepseek V4.1 Flash 1017:
     * Energy produced per tick, also measured on the server so that the GUI shows the same value
     * that is actually injected into the ME network.
     */
    @GuiSync(11)
    public double energyPerTick;

    @GuiSync(12)
    public boolean canSeeSky;

    public SunlightResonanceGeneratorMenu(int id, Inventory ip, SunlightResonanceGeneratorBlockEntity host) {
        super(ACIMenus.SUNLIGHT_RESONANCE_GENERATOR_MENU, id, ip, host);
        this.sunlightResonanceGenerator = host;
        // By Deepseek V4.1 Flash 1017:
        // Populate the synchronized values before the initial full sync is sent, so the GUI
        // does not show a light level of 0 for the first tick after opening.
        if (isServerSide()) {
            this.skyLightLevel = host.getSkyLightLevel();
            this.energyPerTick = host.getEnergyPerTick();
            this.canSeeSky = host.canSeeSky();
        }
    }

    @Override
    // Added By Deepseek V4.1 Flash 1017:
    public void broadcastChanges() {
        if (isServerSide()) {
            this.skyLightLevel = this.sunlightResonanceGenerator.getSkyLightLevel();
            this.energyPerTick = this.sunlightResonanceGenerator.getEnergyPerTick();
        }
        super.broadcastChanges();
    }

    public double getEnergyPerTick() {
        return this.energyPerTick;
    }

    public int getSkylightLevel() {
        return this.skyLightLevel;
    }
}
