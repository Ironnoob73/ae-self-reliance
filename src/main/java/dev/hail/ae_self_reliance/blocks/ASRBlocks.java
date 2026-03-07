package dev.hail.ae_self_reliance.blocks;

import dev.hail.ae_self_reliance.AeSelfRelianceMod;
import dev.hail.ae_self_reliance.items.ASRItems;
import io.github.lounode.ae2cs.common.init.AECSBlockEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ASRBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AeSelfRelianceMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AeSelfRelianceMod.MOD_ID);

    public static final DeferredBlock<SunlightResonanceGeneratorBlock> SUNLIGHT_RESONANCE_GENERATOR = registerWithItem("sunlight_resonance_generator", SunlightResonanceGeneratorBlock::new);
    public static final AECSBlockEntities.DeferredBlockEntityType<SunlightResonanceGeneratorBlockEntity> SUNLIGHT_RESONANCE_GENERATOR_BE = AECSBlockEntities.create(
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
}
