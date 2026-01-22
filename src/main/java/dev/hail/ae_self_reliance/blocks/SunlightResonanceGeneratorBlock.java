package dev.hail.ae_self_reliance.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class SunlightResonanceGeneratorBlock extends AEBaseEntityBlock<SunlightResonanceGeneratorBlockEntity>{
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public SunlightResonanceGeneratorBlock() {
        super(metalProps().strength(4.2F));
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SunlightResonanceGeneratorBlockEntity(pos, state);
    }
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, SunlightResonanceGeneratorBlockEntity be) {
        return currentState.setValue(ACTIVE, be.isOn);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity var7 = level.getBlockEntity(pos);
        if (var7 instanceof SunlightResonanceGeneratorBlockEntity be) {
            if (!level.isClientSide) {
                MenuOpener.open(ASRMenus.SUNLIGHT_RESONANCE_GENERATOR_MENU.get(), player, MenuLocators.forBlockEntity(be));
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
    }

}
