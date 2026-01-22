package dev.hail.ae_self_reliance.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.DeferredBlockEntityType;
import com.google.common.base.Preconditions;
import dev.hail.ae_self_reliance.AeSelfRelianceMod;
import dev.hail.ae_self_reliance.items.ASRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ASRBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AeSelfRelianceMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AeSelfRelianceMod.MOD_ID);

    public static final DeferredBlock<SunlightResonanceGeneratorBlock> SUNLIGHT_RESONANCE_GENERATOR = registerWithItem("sunlight_resonance_generator", SunlightResonanceGeneratorBlock::new);
    public static final DeferredBlockEntityType<SunlightResonanceGeneratorBlockEntity> SUNLIGHT_RESONANCE_GENERATOR_BE = create2(
            "sunlight_resonance_generator_entity",
            SunlightResonanceGeneratorBlockEntity.class,
            SunlightResonanceGeneratorBlockEntity::new,
            SUNLIGHT_RESONANCE_GENERATOR);

    public static DeferredBlock<Block> registerWithItem(String id, BlockBehaviour.Properties block, Item.Properties item) {
        DeferredBlock<Block> object = BLOCKS.register(id, () -> new Block(block));
        ASRItems.ITEMS.registerSimpleBlockItem(object, item);
        return object;
    }
    public static <B extends Block> DeferredBlock<B> registerWithItem(String id, Supplier<B> block) {
        DeferredBlock<B> object = BLOCKS.register(id, block);
        ASRItems.ITEMS.registerSimpleBlockItem(object);
        return object;
    }


    @SafeVarargs
    public static <T extends AEBaseBlockEntity> DeferredBlockEntityType<T> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            Supplier<? extends AEBaseEntityBlock<?>>... blockSuppliers
    )
    {
        Preconditions.checkArgument(blockSuppliers.length > 0, "At least one block is required");

        var deferred = BLOCK_ENTITY_TYPES.register(id, () -> {
            // 把 BlockEntityType 反向塞入 BE 构造用
            AtomicReference<BlockEntityType<T>> typeRef = new AtomicReference<>();

            BlockEntityType.BlockEntitySupplier<T> supplier = (pos, state) ->
                    factory.create(typeRef.get(), pos, state);

            // 解包方块
            AEBaseEntityBlock<?>[] blocks = Arrays.stream(blockSuppliers)
                    .map(Supplier::get)
                    .toArray(AEBaseEntityBlock[]::new);

            // 构造 BE 类型
            Block[] vanillaBlocks = Arrays.stream(blocks).toArray(Block[]::new);

            BlockEntityType<T> type = BlockEntityType.Builder.of(supplier, vanillaBlocks).build(null);
            // 让上面的 supplier 拿得到 type
            typeRef.set(type);

            // 把“物品形态”绑定给这个 BE 类型（AE 用于记忆卡/设置导入导出/tooltip 等）
            try
            {
                var item = blocks[0].asItem();
                AEBaseBlockEntity.registerBlockEntityItem(type, item);
            }
            catch (Throwable ignored)
            {
            }

            // 自动生成 tickers（如果实现了 AE 的标记接口）
            BlockEntityTicker<T> serverTicker = null;
            if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass))
            {
                serverTicker = (lvl, p, st, be) -> ((ServerTickingBlockEntity) be).serverTick();
            }
            BlockEntityTicker<T> clientTicker = null;
            if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass))
            {
                clientTicker = (lvl, p, st, be) -> ((ClientTickingBlockEntity) be).clientTick();
            }

            // 把 “type + tickers + 具体 BE 类” 绑定到每个方块上
            for (var b : blocks)
            {
                @SuppressWarnings("unchecked")
                AEBaseEntityBlock<T> base = (AEBaseEntityBlock<T>) b;
                base.setBlockEntity(entityClass, type, clientTicker, serverTicker);
            }

            return type;
        });

        var wrapped = new DeferredBlockEntityType<>(entityClass, deferred);
        //ALL.add(wrapped);
        return wrapped;
    }
    @SafeVarargs
    public static <T extends AEBaseBlockEntity> DeferredBlockEntityType<T> create2(
            String id,
            Class<T> entityClass,
            BiFunction<BlockPos, BlockState, T> ctorPosState,
            Supplier<? extends AEBaseEntityBlock<?>>... blockSuppliers
    )
    {
        return create(id, entityClass, (type, pos, state) -> ctorPosState.apply(pos, state), blockSuppliers);
    }
    public interface BlockEntityFactory<T extends AEBaseBlockEntity>
    {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
}
