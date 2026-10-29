package dev.hail.ae_crystal_industries.blocks.sunlight_resonance_generator;

import appeng.api.config.Actionable;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
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
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.util.Platform;
import dev.hail.ae_crystal_industries.ACIConfig;
import dev.hail.ae_crystal_industries.inits.ACIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SunlightResonanceGeneratorBlockEntity extends AENetworkedBlockEntity implements IGridTickable, IUpgradeableObject {
    private final IUpgradeInventory upgrades;

    // Synced to the client, drives the ACTIVE block state
    public boolean isOn;

    // AE energy produced per tick per sky light level (configurable)
    private final double baseEnergyRate;

    public SunlightResonanceGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().setIdlePowerUsage(0.0F).setFlags().addService(IGridTickable.class, this);
        this.upgrades = UpgradeInventories.forMachine(ACIBlocks.SUNLIGHT_RESONANCE_GENERATOR, 4, this::saveChanges);
        this.baseEnergyRate = ACIConfig.INSTANCE.commonConfig.sunlightGeneratesEnergyPerTick.getAsDouble();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    /**
     * Measures the current daylight like the vanilla daylight detector: sky brightness minus
     * the sky darkening (night / weather), clamped to {@code [0, 15]}. Returns 0 in dimensions
     * without skylight.
     * <p>
     * The light is measured at the position directly above this block: this machine is a solid,
     * light-opaque block, so the light engine reports 0 at its own position. The vanilla daylight
     * detector gets away with measuring its own position only because that block is transparent.
     */
    public int getSkyLightLevel() {
        if (this.level == null || !this.level.dimensionType().hasSkyLight() || !this.level.canSeeSky(this.worldPosition.above())) {
            return 0;
        }

        int i = this.level.getBrightness(LightLayer.SKY, this.worldPosition.above()) - this.level.getSkyDarken();
        float f = level.getSunAngle(1.0F);
        float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
        f += (f1 - f) * 0.2F;
        i = Math.round((float)i * Mth.cos(f));
        return Mth.clamp(i, 0, 15);
    }

    public boolean canSeeSky() {
        if (this.level != null) {
            return this.level.canSeeSky(this.worldPosition.above());
        }
        return false;
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        boolean changed = super.readFromStream(data);
        boolean wasOn = this.isOn;
        this.isOn = data.readBoolean();
        return changed || wasOn != this.isOn;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isOn);
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (ItemStack upgrade : this.upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.upgrades.clear();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        }

        return super.getSubInventory(id);
    }

    public double getEnergyPerTick(){
        return getSkyLightLevel() * this.baseEnergyRate * (1 + this.upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD) / 2.0f);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        // Never report "sleeping": nothing else would wake this device when the sun rises,
        // so it has to keep sampling the daylight on its own. The actual sampling interval
        // is controlled via TickRateModulation in tickingRequest.
        return new TickingRequest(TickRates.VibrationChamber, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        int skyLight = getSkyLightLevel();

        boolean shouldBeOn = skyLight > 0;
        if (this.isOn != shouldBeOn) {
            this.isOn = shouldBeOn;
            this.markForUpdate();
            if (this.hasLevel()) {
                Platform.notifyBlocksOfNeighbors(this.level, this.worldPosition);
            }
        }

        if (skyLight > 0) {
            double produced = skyLight * this.baseEnergyRate * ticksSinceLastCall;
            IGrid grid = node.getGrid();
            IEnergyService energy = grid.getEnergyService();
            double overflow = energy.injectPower(produced, Actionable.MODULATE);

            // Slow down when the network cannot absorb the power, speed up otherwise.
            return overflow > 0 ? TickRateModulation.SLOWER : TickRateModulation.FASTER;
        }

        // No daylight: sample less often, but keep ticking so dawn is noticed.
        return TickRateModulation.SLOWER;
    }

}
