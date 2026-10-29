package dev.hail.ae_crystal_industries.inits;

import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ACICreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, AeCrystalIndustriesMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("tab.ae_crystal_industries"))
                    .icon(() -> ACIBlocks.ENERGY_INTERFACE.get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ACIBlocks.SUNLIGHT_RESONANCE_GENERATOR.get());
                        output.accept(ACIBlocks.ENERGY_INTERFACE.get());
                        output.accept(ACIItems.ENERGY_INTERFACE_PART.get());
                    })
                    .build());

    private ACICreativeTab() {
    }

    public static void init() {
    }
}
