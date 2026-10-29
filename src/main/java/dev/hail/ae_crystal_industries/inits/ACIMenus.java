package dev.hail.ae_crystal_industries.inits;

import appeng.menu.implementations.MenuTypeBuilder;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import dev.hail.ae_crystal_industries.blocks.energy_interface.EnergyInterfaceMenu;
import dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator.SunlightResonanceGeneratorBlockEntity;
import dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator.SunlightResonanceGeneratorMenu;
import dev.hail.ae_crystal_industries.energy.EnergyInterfaceHost;
import dev.hail.ae_crystal_industries.gui.SetValueMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public final class ACIMenus {

    public static final MenuType<SunlightResonanceGeneratorMenu> SUNLIGHT_RESONANCE_GENERATOR_MENU = MenuTypeBuilder
            .create(SunlightResonanceGeneratorMenu::new, SunlightResonanceGeneratorBlockEntity.class)
            .build(id("sunlight_resonance_generator"));

    /**
     * The same menu is used by the block and the cable part. AE2 registers menu types built by
     * {@link MenuTypeBuilder#build(ResourceLocation)} through its own registration queue.
     */
    public static final MenuType<EnergyInterfaceMenu> ENERGY_INTERFACE_MENU = MenuTypeBuilder
            .create(
                    EnergyInterfaceMenu::new,
                    EnergyInterfaceHost.class)
            .build(id("energy_interface"));

    public static final MenuType<SetValueMenu> SET_VALUE_MENU = MenuTypeBuilder
            .create(SetValueMenu::new, EnergyInterfaceHost.class)
            .build("set_value");


    private ACIMenus() {
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AeCrystalIndustriesMod.MOD_ID, path);
    }

    public static void init() {
    }
}
