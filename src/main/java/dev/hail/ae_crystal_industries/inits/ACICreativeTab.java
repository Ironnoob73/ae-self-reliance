package dev.hail.ae_crystal_industries.inits;

import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import net.minecraft.network.chat.Component;
import net.pedroksl.ae2addonlib.registry.CreativeTabRegistry;

public class ACICreativeTab extends CreativeTabRegistry {

    public static final ACICreativeTab INSTANCE = new ACICreativeTab();

    ACICreativeTab() {
        super(AeCrystalIndustriesMod.MOD_ID, Component.translatable("tab.ae_crystal_industries"), ACIBlocks.SUNLIGHT_RESONANCE_GENERATOR::stack);
    }
}
