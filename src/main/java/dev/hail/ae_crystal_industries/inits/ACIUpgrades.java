package dev.hail.ae_crystal_industries.inits;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class ACIUpgrades {
    public static void addUpgradesForBlocks(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
            Upgrades.add(AEItems.ENERGY_CARD, ACIBlocks.SUNLIGHT_RESONANCE_GENERATOR.get(), 4);

            // Allow up to four AE2 Energy Cards in both forms. Each card reduces the AE cost per FE by one.
            Upgrades.add(AEItems.ENERGY_CARD, ACIBlocks.ENERGY_INTERFACE.get(), 4);
            Upgrades.add(AEItems.ENERGY_CARD, ACIItems.ENERGY_INTERFACE_PART.get(), 4);
        });
    }
}
