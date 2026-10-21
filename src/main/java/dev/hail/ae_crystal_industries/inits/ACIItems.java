package dev.hail.ae_crystal_industries.inits;

import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import net.pedroksl.ae2addonlib.registry.ItemRegistry;

/**
 * Item registry of this mod, backed by AE2AddonLib.
 * <p>
 * This class MUST be initialized (its static {@link #INSTANCE} touched) before any block definition of this mod is
 * created, because AE2AddonLib's {@code BlockRegistry.block()} looks up the item deferred-register of the same mod id
 * when it registers the block item.
 */
public final class ACIItems extends ItemRegistry {

    public static final ACIItems INSTANCE = new ACIItems();

    ACIItems() {
        super(AeCrystalIndustriesMod.MOD_ID);
    }
}
