package dev.hail.ae_crystal_industries.inits;

import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import dev.hail.ae_crystal_industries.blocks.energy_interface.EnergyInterfacePart;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Item registry of this mod.
 */
public final class ACIItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AeCrystalIndustriesMod.MOD_ID);

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
