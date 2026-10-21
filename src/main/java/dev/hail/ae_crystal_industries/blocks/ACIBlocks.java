package dev.hail.ae_crystal_industries.blocks;

import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.pedroksl.ae2addonlib.registry.BlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class ACIBlocks extends BlockRegistry {

    public static final ACIBlocks INSTANCE = new ACIBlocks();

    ACIBlocks() {
        super(AeCrystalIndustriesMod.MOD_ID);
    }

    public static final BlockDefinition<SunlightResonanceGeneratorBlock> SUNLIGHT_RESONANCE_GENERATOR = block(
            "Sunlight Resonance Generator",
            "sunlight_resonance_generator",
            SunlightResonanceGeneratorBlock::new, AEBaseBlockItem::new);

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        return block(AeCrystalIndustriesMod.MOD_ID, englishName, id, blockSupplier, itemFactory);
    }

}
