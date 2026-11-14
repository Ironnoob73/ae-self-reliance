package dev.hail.ae_crystal_industries.blocks.blasting_simulation_chamber;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlastingSimulationChamberBlock extends AEBaseEntityBlock<BlastingSimulationChamberBlockEntity> {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public BlastingSimulationChamberBlock() {
        super(metalProps().strength(4.2F).explosionResistance(1225));
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, BlastingSimulationChamberBlockEntity be) {
        return currentState.setValue(ACTIVE, be.isOn);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }
}
