package dev.hail.ae_crystal_industries.inits;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.BlockDefinition;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import dev.hail.ae_crystal_industries.blocks.SunlightResonanceGeneratorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.pedroksl.ae2addonlib.registry.BlockEntityRegistry;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ACIBlockEntities extends BlockEntityRegistry {

    public static final ACIBlockEntities INSTANCE = new ACIBlockEntities();

    ACIBlockEntities() {
        super(AeCrystalIndustriesMod.MOD_ID);
    }

    public static final Supplier<BlockEntityType<SunlightResonanceGeneratorBlockEntity>> SUNLIGHT_RESONANCE_GENERATOR = create(
            "sunlight_resonance_generator_entity",
            SunlightResonanceGeneratorBlockEntity.class,
            SunlightResonanceGeneratorBlockEntity::new,
            ACIBlocks.SUNLIGHT_RESONANCE_GENERATOR);

    @SafeVarargs
    private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefs) {
        return create(AeCrystalIndustriesMod.MOD_ID, id, entityClass, factory, blockDefs);
    }
}
