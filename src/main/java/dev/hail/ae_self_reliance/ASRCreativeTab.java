package dev.hail.ae_self_reliance;

import dev.hail.ae_self_reliance.blocks.ASRBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ASRCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AeSelfRelianceMod.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ASR_TAB = CREATIVE_MODE_TABS.register("ae_self_reliance_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("tab.ae_self_reliance"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ASRBlocks.SUNLIGHT_RESONANCE_GENERATOR.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ASRBlocks.SUNLIGHT_RESONANCE_GENERATOR.get());
            }).build());
}
