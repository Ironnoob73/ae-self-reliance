package dev.hail.ae_self_reliance.blocks;

import dev.hail.ae_self_reliance.AeSelfRelianceMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = AeSelfRelianceMod.MOD_ID, value = Dist.CLIENT)
public class ASRScreens
{
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        event.register(ASRMenus.SUNLIGHT_RESONANCE_GENERATOR_MENU.get(), SunlightResonanceGeneratorScreen::new);
    }
}