package dev.hail.ae_crystal_industries;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
public class ACIConfig {
    public final ACIConfig.StartUpConfig startUpConfig = new ACIConfig.StartUpConfig();
    public final ACIConfig.CommonConfig commonConfig = new ACIConfig.CommonConfig();

    public static ACIConfig INSTANCE;

    private ACIConfig(ModContainer container)
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
        INSTANCE = new ACIConfig(container);
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

            sunlightGeneratesEnergyPerTick = builder.defineInRange("sunlight_generates_energy_per_tick", 5d, 0d, Double.MAX_VALUE);

            this.spec = builder.build();
        }

        public void onLoaded()
        {
        }
    }
}
