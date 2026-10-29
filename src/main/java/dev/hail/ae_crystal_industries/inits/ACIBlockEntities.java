package dev.hail.ae_crystal_industries.inits;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import dev.hail.ae_crystal_industries.blocks.energy_interface.EnergyInterfaceBlockEntity;
import dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator.SunlightResonanceGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public final class ACIBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, AeCrystalIndustriesMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SunlightResonanceGeneratorBlockEntity>> SUNLIGHT_RESONANCE_GENERATOR = create(
            "sunlight_resonance_generator_entity",
            SunlightResonanceGeneratorBlockEntity.class,
            SunlightResonanceGeneratorBlockEntity::new,
            ACIBlocks.SUNLIGHT_RESONANCE_GENERATOR);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyInterfaceBlockEntity>> ENERGY_INTERFACE = create(
            "energy_interface_entity",
            EnergyInterfaceBlockEntity.class,
            EnergyInterfaceBlockEntity::new,
            ACIBlocks.ENERGY_INTERFACE);

    private ACIBlockEntities() {
    }

    public static void init() {
    }

    @FunctionalInterface
    private interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    @SafeVarargs
    private static <T extends AEBaseBlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            DeferredBlock<?>... blocks) {
        return BLOCK_ENTITY_TYPES.register(id, () -> {
            AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
            Block[] registeredBlocks = Arrays.stream(blocks).map(DeferredBlock::get).toArray(Block[]::new);

            BlockEntityType<T> type = BlockEntityType.Builder.of(
                    (pos, state) -> factory.create(typeHolder.get(), pos, state),
                    registeredBlocks).build(null);
            typeHolder.set(type);

            AEBaseBlockEntity.registerBlockEntityItem(type, registeredBlocks[0].asItem());

            for (Block block : registeredBlocks) {
                @SuppressWarnings("unchecked")
                AEBaseEntityBlock<T> baseBlock = (AEBaseEntityBlock<T>) block;
                baseBlock.setBlockEntity(entityClass, type, null, null);
            }

            return type;
        });
    }
}
