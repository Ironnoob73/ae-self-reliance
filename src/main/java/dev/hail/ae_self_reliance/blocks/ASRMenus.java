package dev.hail.ae_self_reliance.blocks;

import appeng.menu.implementations.MenuTypeBuilder;
import dev.hail.ae_self_reliance.AeSelfRelianceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ASRMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, AeSelfRelianceMod.MOD_ID);

    public static final Supplier<MenuType<SunlightResonanceGeneratorMenu>> SUNLIGHT_RESONANCE_GENERATOR_MENU = MENU_TYPES.register("sunlight_resonace_generator_menu",
            () -> MenuTypeBuilder.create(SunlightResonanceGeneratorMenu::new, SunlightResonanceGeneratorBlockEntity.class)
                    .build("sunlight_resonace_generator_menu")
    );
}
