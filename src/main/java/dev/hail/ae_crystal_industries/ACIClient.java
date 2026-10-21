package dev.hail.ae_crystal_industries;

import dev.hail.ae_crystal_industries.blocks.ACIScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AeCrystalIndustriesMod.MOD_ID, dist = Dist.CLIENT)
public class ACIClient extends AeCrystalIndustriesMod {
    public ACIClient(IEventBus eventBus, ModContainer container) {
        super(eventBus, container);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        eventBus.addListener(ACIScreens::initScreens);
    }
}
