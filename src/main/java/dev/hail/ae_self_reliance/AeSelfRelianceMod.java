package dev.hail.ae_self_reliance;

import com.mojang.logging.LogUtils;
import dev.hail.ae_self_reliance.blocks.ASRBlocks;
import dev.hail.ae_self_reliance.blocks.ASRMenus;
import dev.hail.ae_self_reliance.items.ASRItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;


@Mod(AeSelfRelianceMod.MOD_ID)
public class AeSelfRelianceMod {
    public static final String MOD_ID = "ae_self_reliance";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AeSelfRelianceMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        ASRConfig.register(modContainer);
        ASRBlocks.BLOCKS.register(modEventBus);
        ASRBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);
        ASRItems.ITEMS.register(modEventBus);
        ASRMenus.MENU_TYPES.register(modEventBus);
        ASRCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
