package dev.hail.ae_crystal_industries.blocks.energy_interface;

import appeng.api.config.*;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.IPriorityHost;
import dev.hail.ae_crystal_industries.blocks.energy_interface.logic.EnergyInterfaceHost;
import dev.hail.ae_crystal_industries.blocks.energy_interface.logic.EnergyInterfaceLogic;
import dev.hail.ae_crystal_industries.inits.ACIBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

// Init By Deepseek V4.1 Flash 1017:
public class EnergyInterfaceBlockEntity extends AENetworkedBlockEntity
        implements IPriorityHost, IGridTickable, IUpgradeableObject, EnergyInterfaceHost {

    private final IUpgradeInventory upgrades;
    private final EnergyInterfaceLogic logic;

    private final IConfigManager manager;
    private boolean isActive = false;

    public EnergyInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().setIdlePowerUsage(0.0).setFlags(GridFlags.REQUIRE_CHANNEL);
        this.upgrades = UpgradeInventories.forMachine(ACIBlocks.ENERGY_INTERFACE.get().asItem(), 4,
                this::onUpgradesChanged);
        this.logic = new EnergyInterfaceLogic(this.getMainNode(), this, this.upgrades, this::saveChanges,
                this::onActiveChanged);
        this.manager = IConfigManager.builder(this::onConfigChanged)
                .registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE)
                .build();
        this.getMainNode().addService(IGridTickable.class, this);
    }

    private boolean isInPulseMode() {
        return this.upgrades.isInstalled(AEItems.REDSTONE_CARD)
                && getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED) == RedstoneMode.SIGNAL_PULSE;
    }

    public IEnergyStorage getEnergyStorage() {
        return logic.getEnergyStorage();
    }

    private void onUpgradesChanged() {
        saveChanges();
        // Adding or removing a redstone card changes whether redstone control applies.
        updateTask();
    }

    private void onActiveChanged() {
        this.markForUpdate();
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        boolean ret = super.readFromStream(data);

        final boolean isActive = data.readBoolean();
        ret = isActive != this.isActive || ret;
        this.isActive = isActive;

        return ret;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isActive());
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.manager.writeToNBT(data, registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.logic.saveTag(data);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.manager.readFromNBT(data, registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
        this.logic.loadTag(data);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (ItemStack upgrade : this.upgrades) {
            if (!upgrade.isEmpty()) {
                drops.add(upgrade);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.upgrades.clear();
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.manager;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        final boolean sleeping = isSleeping();
        // The node may have been removed from the grid for a while (chunk reload, network change), so the next
        // tick must not catch up on that idle time.
        this.logic.suppressCatchUp();
        // Keep the shared logic's state in sync when the node is (re)added to a grid.
        this.logic.setRunning(!sleeping);
        return new TickingRequest(TickRates.Charger, sleeping);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (isSleeping()) {
            this.logic.setRunning(false);
            return TickRateModulation.SLEEP;
        }

        // Stay awake while the grid is inactive so the interface resumes on its own once power/channel returns.
        // Sleeping here would require an external wake-up that might never happen.
        if (!this.getMainNode().isActive()) {
            return TickRateModulation.IDLE;
        }

        this.logic.setRunning(true);

        var blockEntity = getBlockEntity();
        if (blockEntity != null && blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
            // A redstone pulse performs exactly one tick of work, no matter how long the interface slept before it.
            int effectiveTicks = isInPulseMode() && this.logic.pendingPulse ? 1 : ticksSinceLastCall;
            this.logic.tick(blockEntity.getLevel(), blockEntity.getBlockPos(), null, effectiveTicks);
        }
        this.logic.pendingPulse = false;
        return TickRateModulation.SAME;
    }

    @Override
    public long getOutputRate() {
        return this.logic.getOutputRate();
    }

    @Override
    public void cycleOutputRate(int direction) {
        this.logic.cycleOutputRate(direction);
    }

    @Override
    public long getEnergyCapacity() {
        return this.logic.getEnergyCapacity();
    }

    @Override
    public void setOutputRate(long value) {
        this.logic.setOutputRate(value);
    }

    @Override
    public void cycleEnergyCapacity(int direction) {
        this.logic.cycleEnergyCapacity(direction);
    }

    @Override
    public void setCapacity(long value) {
        this.logic.setCapacity(value);
    }

    @Override
    public long getStoredEnergy() {
        return this.logic.getStoredEnergy();
    }

    @Override
    public EnergyInterfaceLogic getInterfaceLogic() {
        return this.logic;
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return ACIBlocks.ENERGY_INTERFACE.get().asItem().getDefaultInstance();
    }

    @Override
    public int getAePerFe() {
        return this.logic.getAePerFe();
    }

    private void onConfigChanged() {
        this.logic.pendingPulse = false;
        if (isInPulseMode()) {
            // Remember the current signal level, so that switching into pulse mode does not treat an already
            // powered input as a fresh pulse.
            this.logic.lastRedstone = getRedstoneState();
        }
        updateTask();
    }

    private void updateTask() {
        final boolean sleeping = isSleeping();
        this.logic.setRunning(!sleeping);

        getMainNode().ifPresent((grid, node) -> {
            if (sleeping) {
                grid.getTickManager().sleepDevice(node);
            } else {
                grid.getTickManager().wakeDevice(node);
            }
        });
    }

    public void updateRedstoneState() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        final boolean powered = getRedstoneState();

        if (isInPulseMode()) {
            if (this.logic.lastRedstone != powered) {
                this.logic.lastRedstone = powered;
                if (powered && !this.logic.pendingPulse) {
                    // Perform exactly one tick of work for this pulse.
                    this.logic.pendingPulse = true;
                    this.logic.setRunning(true);
                    getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
                }
            }
        } else {
            this.logic.lastRedstone = powered;
            this.logic.pendingPulse = false;
        }

        updateTask();
    }

    private boolean getRedstoneState() {
        if (this.level == null) {
            return false;
        }
        return this.level.getBestNeighborSignal(this.worldPosition) != 0;
    }

    /**
     * @return true if redstone control currently prevents the interface from running.
     */
    private boolean isSleeping() {
        if (isInPulseMode() && this.logic.pendingPulse) {
            return false;
        }

        if (!this.upgrades.isInstalled(AEItems.REDSTONE_CARD)) {
            return false;
        }

        return switch (this.getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED)) {
            case IGNORE -> false;
            case HIGH_SIGNAL -> !this.getRedstoneState();
            case LOW_SIGNAL -> this.getRedstoneState();
            case SIGNAL_PULSE -> true;
        };
    }

    @Override
    public boolean isActive() {
        if (level != null && !level.isClientSide) {
            return this.getMainNode().isOnline();
        } else {
            return this.isActive;
        }
    }

}
