package dev.hail.ae_crystal_industries.inits;

import appeng.init.client.InitScreens;
import dev.hail.ae_crystal_industries.blocks.energy_interface.EnergyInterfaceScreen;
import dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator.SunlightResonanceGeneratorScreen;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ACIScreens
{
    public static void initScreens(RegisterMenuScreensEvent event) {
        // The style path is resolved by AE2's StyleManager against "assets/ae2/screens/".
        InitScreens.register(event,
                ACIMenus.SUNLIGHT_RESONANCE_GENERATOR_MENU,
                SunlightResonanceGeneratorScreen::new,
                "/screens/aci_sunlight_resonance_generator.json");
        InitScreens.register(event,
                ACIMenus.ENERGY_INTERFACE_MENU,
                EnergyInterfaceScreen::new,
                "/screens/aci_energy_interface.json");
    }
}
