package dev.hail.ae_self_reliance.blocks;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.AEConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SunlightResonanceGeneratorBlockEntity extends AENetworkedBlockEntity implements IGridTickable, IUpgradeableObject {
    private final IUpgradeInventory upgrades;
    public boolean isOn;
    private final double minEnergyRate;
    private final double baseMaxEnergyRate;
    private final double initialEnergyRate;

    public SunlightResonanceGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ASRBlocks.SUNLIGHT_RESONANCE_GENERATOR_BE.get(), pos, blockState);
        this.upgrades = UpgradeInventories.forMachine(ASRBlocks.SUNLIGHT_RESONANCE_GENERATOR, 3, this::saveChanges);
        this.minEnergyRate = AEConfig.instance().getVibrationChamberMinEnergyPerGameTick();
        this.baseMaxEnergyRate = AEConfig.instance().getVibrationChamberMaxEnergyPerGameTick();
        this.initialEnergyRate = Mth.clamp(AEConfig.instance().getVibrationChamberBaseEnergyPerFuelTick(), this.minEnergyRate, this.baseMaxEnergyRate);
    }

    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        boolean c = super.readFromStream(data);
        boolean wasOn = this.isOn;
        this.isOn = data.readBoolean();
        return wasOn != this.isOn || c;
    }
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        //this.isOn = this.getRemainingFuelTicks() > 0.0;
        data.writeBoolean(this.isOn);
    }

    // Upgrades
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (ItemStack upgrade : this.upgrades) {
            drops.add(upgrade);
        }
    }
    public void clearContent() {
        super.clearContent();
        this.upgrades.clear();
    }
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return null;
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int i) {
        return null;
    }
}
