package dev.hail.ae_crystal_industries.blocks.energy_interface;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import dev.hail.ae_crystal_industries.inits.ACIMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class EnergyInterfaceBlock extends AEBaseEntityBlock<EnergyInterfaceBlockEntity> {

    public EnergyInterfaceBlock() {
        super(metalProps().strength(4.2F));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EnergyInterfaceBlockEntity be) {
            if (!level.isClientSide()) {
                MenuOpener.open(ACIMenus.ENERGY_INTERFACE_MENU, player, MenuLocators.forBlockEntity(be));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
