package dev.hail.ae_self_reliance.blocks;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.settings.TickRates;
import appeng.util.Platform;
import dev.hail.ae_self_reliance.ASRConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SunlightResonanceGeneratorBlockEntity extends AENetworkedBlockEntity implements IGridTickable, IUpgradeableObject {
    private final IUpgradeInventory upgrades;
    public boolean isOn;
    public int skyLight;
    private final double baseEnergyRate;
    private double currentEnergyProducePerTick;

    public SunlightResonanceGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ASRBlockEntities.SUNLIGHT_RESONANCE_GENERATOR.get(), pos, blockState);
        this.getMainNode().setIdlePowerUsage(0.0F).setFlags().addService(IGridTickable.class, this);
        this.upgrades = UpgradeInventories.forMachine(ASRBlocks.SUNLIGHT_RESONANCE_GENERATOR, 4, this::saveChanges);
        this.baseEnergyRate = ASRConfig.INSTANCE.commonConfig.sunlightGeneratesEnergyPerTick.getAsDouble();
        this.currentEnergyProducePerTick = 0;
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
        data.writeBoolean(this.isOn);
    }

    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        //double speed = ((skyLight * this.baseEnergyRate) - this.currentEnergyProducePerTick) / 100D;
        //data.putDouble("speed", speed);
    }

    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
        //this.currentEnergyProducePerTick = ((double)data.getInt("speed") * this.maxFuelTicksPerTick / (double)100.0F);
    }

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
        this.markForUpdate();
        if (this.hasLevel()) {
            Platform.notifyBlocksOfNeighbors(this.level, this.worldPosition);
        }
        return new TickingRequest(TickRates.VibrationChamber, skyLight != 0);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.level != null) {
            int bright = level.getBrightness(LightLayer.SKY, getBlockPos().above());
            int dark = level.getSkyDarken();
            skyLight = bright - dark;
        }
        if (skyLight >= 0){
            this.markForUpdate();
            if (this.hasLevel()) {
                Platform.notifyBlocksOfNeighbors(this.level, this.worldPosition);
            }
            IGrid grid = node.getGrid();
            IEnergyService energy = grid.getEnergyService();
            double speedScalingPerTick = (skyLight * this.baseEnergyRate) - this.currentEnergyProducePerTick / 1000D;
            double speedStep = (double)ticksSinceLastCall * speedScalingPerTick;
            this.currentEnergyProducePerTick = this.currentEnergyProducePerTick + speedStep;
            if (this.currentEnergyProducePerTick >= 0){
                energy.injectPower(this.currentEnergyProducePerTick, Actionable.MODULATE);
            }
            return TickRateModulation.FASTER;
        }
        this.currentEnergyProducePerTick = 0;
        return TickRateModulation.SLOWER;
    }

}
