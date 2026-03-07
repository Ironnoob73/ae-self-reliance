package dev.hail.ae_self_reliance.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.BlockDefinition;
import dev.hail.ae_self_reliance.AeSelfRelianceMod;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.pedroksl.ae2addonlib.registry.BlockEntityRegistry;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ASRBlockEntities extends BlockEntityRegistry {

    public static final ASRBlockEntities INSTANCE = new ASRBlockEntities();

    ASRBlockEntities() {
        super(AeSelfRelianceMod.MOD_ID);
    }

    public static final Supplier<BlockEntityType<SunlightResonanceGeneratorBlockEntity>> SUNLIGHT_RESONANCE_GENERATOR = create(
            "sunlight_resonance_generator_entity",
            SunlightResonanceGeneratorBlockEntity.class,
            SunlightResonanceGeneratorBlockEntity::new,
            ASRBlocks.SUNLIGHT_RESONANCE_GENERATOR);

    @SafeVarargs
    private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefs) {
        return create(AeSelfRelianceMod.MOD_ID, id, entityClass, factory, blockDefs);
    }
}
