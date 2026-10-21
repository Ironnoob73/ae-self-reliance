package dev.hail.ae_crystal_industries;

import com.mojang.logging.LogUtils;
import dev.hail.ae_crystal_industries.blocks.ACIBlockEntities;
import dev.hail.ae_crystal_industries.blocks.ACIBlocks;
import dev.hail.ae_crystal_industries.blocks.ACIMenus;
import dev.hail.ae_crystal_industries.items.ASRItems;
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

        // The item registry must be initialized before the block registry: ae2addonlib's
        // BlockRegistry.block() looks up the item deferred-register of this mod id when it
        // registers the block item, and throws if it does not exist yet.
        ASRItems.INSTANCE.register(modEventBus);
        ACIBlocks.INSTANCE.register(modEventBus);
        ACIBlockEntities.INSTANCE.register(modEventBus);
        ACIMenus.INSTANCE.register(modEventBus);
        ACICreativeTab.INSTANCE.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        // Register AE2 capabilities for this mod's block entities (grid node host, ...).
        modEventBus.addListener(ACICapabilities::register);
    }

    public static AeCrystalIndustriesMod instance() {
        return INSTANCE;
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }
}
