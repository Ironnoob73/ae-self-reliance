package dev.hail.ae_self_reliance;

import com.mojang.logging.LogUtils;
import dev.hail.ae_self_reliance.blocks.ASRBlocks;
import dev.hail.ae_self_reliance.blocks.ASRMenus;
import dev.hail.ae_self_reliance.blocks.SunlightResonanceGeneratorBlock;
import dev.hail.ae_self_reliance.items.ASRItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;


@Mod(AeSelfRelianceMod.MOD_ID)
public class AeSelfRelianceMod {
    public static final String MOD_ID = "ae_self_reliance";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ASR_TAB = CREATIVE_MODE_TABS.register("ae_self_reliance_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ASRBlocks.SUNLIGHT_RESONANCE_GENERATOR.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ASRBlocks.SUNLIGHT_RESONANCE_GENERATOR.get());
            }).build());

    public AeSelfRelianceMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ASRBlocks.BLOCKS.register(modEventBus);
        ASRBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);
        ASRItems.ITEMS.register(modEventBus);
        ASRMenus.MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
