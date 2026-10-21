package dev.hail.ae_crystal_industries.inits;

import appeng.init.client.InitScreens;
import dev.hail.ae_crystal_industries.blocks.SunlightResonanceGeneratorScreen;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ACIScreens
{
    public static void initScreens(RegisterMenuScreensEvent event) {
        // The style path is resolved by AE2's StyleManager against "assets/ae2/screens/".
        InitScreens.register(event,
                ACIMenus.SUNLIGHT_RESONANCE_GENERATOR_MENU.get(),
                SunlightResonanceGeneratorScreen::new,
                "/screens/aci_sunlight_resonance_generator.json");
    }
}
