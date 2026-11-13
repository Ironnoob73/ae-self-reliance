package dev.hail.ae_crystal_industries.inits;

import appeng.block.AEBaseBlockItem;
import appeng.decorative.AEDecorativeBlock;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import dev.hail.ae_crystal_industries.blocks.energy_interface.EnergyInterfaceBlock;
import dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator.SunlightResonanceGeneratorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static appeng.block.AEBaseBlock.stoneProps;

public final class ACIBlocks {

    private static final BlockBehaviour.Properties QUARTZ_PROPERTIES = stoneProps().mapColor(MapColor.COLOR_CYAN)
            .strength(3, 5).requiresCorrectToolForDrops();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AeCrystalIndustriesMod.MOD_ID);

    public static final DeferredBlock<Block> INTACT_BUDDING_QUARTZ = registerBlock(
            "intact_budding_quartz",
            () -> new AEDecorativeBlock(QUARTZ_PROPERTIES),
            AEBaseBlockItem::new);

    public static final DeferredBlock<SunlightResonanceGeneratorBlock> SUNLIGHT_RESONANCE_GENERATOR = registerBlock(
            "sunlight_resonance_generator",
            SunlightResonanceGeneratorBlock::new,
            AEBaseBlockItem::new);

    public static final DeferredBlock<EnergyInterfaceBlock> ENERGY_INTERFACE = registerBlock(
            "energy_interface",
            EnergyInterfaceBlock::new,
            AEBaseBlockItem::new);

    private ACIBlocks() {
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(
            String id,
            Supplier<T> blockSupplier,
            BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        DeferredBlock<T> block = BLOCKS.register(id, blockSupplier);
        ACIItems.ITEMS.register(id, () -> itemFactory.apply(block.get(), new Item.Properties()));
        return block;
    }

    public static void init() {
    }
}
