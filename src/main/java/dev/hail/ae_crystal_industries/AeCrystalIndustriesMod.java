package dev.hail.ae_crystal_industries;

import com.mojang.logging.LogUtils;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import dev.hail.ae_crystal_industries.inits.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;


@Mod(value = AeCrystalIndustriesMod.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class AeCrystalIndustriesMod {
    public static final String MOD_ID = "ae_crystal_industries";
    public static final Logger LOGGER = LogUtils.getLogger();

    static AeCrystalIndustriesMod INSTANCE;

    public AeCrystalIndustriesMod(IEventBus modEventBus, ModContainer modContainer) {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = this;

        ACIConfig.register(modContainer);

        // Register the content. The item registry has to be initialized first since the block registration adds
        // the corresponding block items to it.
        ACIItems.init();
        ACIBlocks.init();
        ACIBlockEntities.init();
        ACIMenus.init();
        ACICreativeTab.init();

        ACIItems.ITEMS.register(modEventBus);
        ACIBlocks.BLOCKS.register(modEventBus);
        ACIBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ACICreativeTab.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        // Register AE2 capabilities for this mod's block entities / parts (grid node host, FE output, ...).
        modEventBus.addListener(ACICapabilities::register);
        modEventBus.addListener(ACICapabilities::registerPartCapabilities);
    }

    public static AeCrystalIndustriesMod instance() {
        return INSTANCE;
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Allow up to four AE2 Energy Cards in both forms. Each card reduces the AE cost per FE by one.
            Upgrades.add(AEItems.ENERGY_CARD, ACIBlocks.ENERGY_INTERFACE.get(), 4);
            Upgrades.add(AEItems.ENERGY_CARD, ACIItems.ENERGY_INTERFACE_PART.get(), 4);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }
}
