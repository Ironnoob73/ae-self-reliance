package dev.hail.ae_crystal_industries.inits;

import appeng.api.parts.PartModels;
import appeng.items.materials.MaterialItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import dev.hail.ae_crystal_industries.blocks.energy_interface.EnergyInterfacePart;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Item registry of this mod.
 */
public final class ACIItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AeCrystalIndustriesMod.MOD_ID);

    public static final DeferredItem<Item> TACTICAL_PANCAKE = ITEMS.register("tactical_pancake",
            () -> new Item(new Item.Properties().food((new FoodProperties.Builder()).nutrition(9).saturationModifier(0.75F)
                    .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 36000, 1), 1.0F)
                    .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 36000, 1), 1.0F).build())));

    public static final DeferredItem<MaterialItem> AMETHYST_FIBER = ITEMS.register("amethyst_fiber",
            () -> new MaterialItem(new Item.Properties()));

    public static final DeferredItem<MaterialItem> INTEGRATED_PROCESSOR_PRESS = ITEMS.register("integrated_processor_press",
            () -> new MaterialItem(new Item.Properties()));

    public static final DeferredItem<MaterialItem> INDUSTRY_PROCESSOR_PRESS = ITEMS.register("industry_processor_press",
            () -> new MaterialItem(new Item.Properties()));

    public static final DeferredItem<MaterialItem> PRINTED_INTEGRATED_PROCESSOR = ITEMS.register("printed_integrated_processor",
            () -> new MaterialItem(new Item.Properties()));

    public static final DeferredItem<MaterialItem> PRINTED_INDUSTRY_PROCESSOR = ITEMS.register("printed_industry_processor",
            () -> new MaterialItem(new Item.Properties()));

    public static final DeferredItem<MaterialItem> INTEGRATED_PROCESSOR = ITEMS.register("integrated_processor",
            () -> new MaterialItem(new Item.Properties()));

    public static final DeferredItem<MaterialItem> INDUSTRY_PROCESSOR = ITEMS.register("industry_processor",
            () -> new MaterialItem(new Item.Properties()));

    public static final DeferredItem<PartItem<EnergyInterfacePart>> ENERGY_INTERFACE_PART = ITEMS.register(
            "energy_interface_part",
            () -> new PartItem<>(new Item.Properties(), EnergyInterfacePart.class, EnergyInterfacePart::new));

    private ACIItems() {
    }

    /**
     * Called during mod construction, before item registration. Registers the cable part models early enough for AE2
     * (the model set is frozen when the client registers additional models).
     */
    public static void init() {
        PartModels.registerModels(PartModelsHelper.createModels(EnergyInterfacePart.class));
    }
}
