package dev.hail.ae_self_reliance;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
public class ASRConfig {
    public final ASRConfig.StartUpConfig startUpConfig = new ASRConfig.StartUpConfig();
    public final ASRConfig.CommonConfig commonConfig = new ASRConfig.CommonConfig();

    public static ASRConfig INSTANCE;

    private ASRConfig(ModContainer container)
    {
        container.registerConfig(ModConfig.Type.STARTUP, startUpConfig.spec);
        container.registerConfig(ModConfig.Type.COMMON, commonConfig.spec);
        container.getEventBus().addListener((ModConfigEvent.Loading evt) ->
        {
            if (evt.getConfig().getSpec() == commonConfig.spec)
            {
                commonConfig.onLoaded();
            }
        });
        container.getEventBus().addListener((ModConfigEvent.Reloading evt) ->
        {
            if (evt.getConfig().getSpec() == commonConfig.spec)
            {
                commonConfig.onLoaded();
            }
        });
    }

    public static void register(ModContainer container)
    {
        INSTANCE = new ASRConfig(container);
    }

    public static class StartUpConfig
    {
        public final ModConfigSpec spec;

        public StartUpConfig()
        {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            this.spec = builder.build();
        }
    }

    public static class CommonConfig
    {
        public final ModConfigSpec spec;

        public final ModConfigSpec.DoubleValue sunlightGeneratesEnergyPerTick;

        public CommonConfig()
        {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            sunlightGeneratesEnergyPerTick = builder.defineInRange("pure_crystal_burn_multiplier", 5d, 0d, Double.MAX_VALUE);

            this.spec = builder.build();
        }

        public void onLoaded()
        {
        }
    }
}
