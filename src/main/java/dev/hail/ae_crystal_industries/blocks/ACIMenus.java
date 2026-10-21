package dev.hail.ae_crystal_industries.blocks;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.ae2addonlib.registry.MenuRegistry;

import java.util.function.Supplier;

public class ACIMenus extends MenuRegistry {

    public static final ACIMenus INSTANCE = new ACIMenus();

    ACIMenus() {
        super(AeCrystalIndustriesMod.MOD_ID);
    }

    public static final Supplier<MenuType<SunlightResonanceGeneratorMenu>> SUNLIGHT_RESONANCE_GENERATOR_MENU = create(
            "sunlight_resonace_generator_menu",
            SunlightResonanceGeneratorMenu::new, SunlightResonanceGeneratorBlockEntity.class
    );

    private static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return create(AeCrystalIndustriesMod.MOD_ID, id, factory, host);
    }
}
