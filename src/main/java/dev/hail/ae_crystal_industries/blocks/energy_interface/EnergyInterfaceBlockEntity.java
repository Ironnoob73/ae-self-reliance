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
        this.manager = IConfigManager.builder(this::updateTask)
                .registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE)
                .build();
        this.getMainNode().addService(IGridTickable.class, this);
    }

    private boolean isInPulseMode() {
        return getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED) == RedstoneMode.SIGNAL_PULSE;
    }

    public IEnergyStorage getEnergyStorage() {
        return logic.getEnergyStorage();
    }

    private void onUpgradesChanged() {
        saveChanges();
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
        return new TickingRequest(TickRates.Charger, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (!this.isActive()) {
            return TickRateModulation.SLEEP;
        }
        var blockEntity = getBlockEntity();
        if (blockEntity != null && blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
            this.logic.tick(this.level, this.worldPosition, null, ticksSinceLastCall);
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

    private void updateTask() {
        getMainNode().ifPresent((grid, node) -> {
            if (this.isEnabled()) {
                grid.getTickManager().wakeDevice(node);
            } else {
                grid.getTickManager().sleepDevice(node);
            }
        });
    }

    public void updateRedstoneState() {
        if (!this.isInPulseMode()) {
            this.logic.pendingPulse = false;
        }

        getMainNode().ifPresent((grid, node) -> {
            if (!this.isActive()) {
                grid.getTickManager().wakeDevice(node);
            } else {
                grid.getTickManager().sleepDevice(node);
            }
        });

        /*
        final boolean currentState;
        if (this.level != null) {
            currentState = this.level.getBestNeighborSignal(this.worldPosition) != 0;
            if (this.logic.lastRedstone != currentState) {
                this.logic.lastRedstone = currentState;
                this.updateTask();
            }
        }*/
    }

    private boolean getRedstoneState() {
        return this.logic.lastRedstone;
    }

    private boolean isEnabled() {
        if (!upgrades.isInstalled(AEItems.REDSTONE_CARD)) {
            return true;
        }

        final RedstoneMode rs = getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
        if (rs == RedstoneMode.HIGH_SIGNAL) {
            return this.getRedstoneState();
        }
        return !this.getRedstoneState();
    }

    @Override
    public boolean isActive() {
        if (isInPulseMode() && this.logic.pendingPulse) {
            return false;
        } else if (level != null && !level.isClientSide) {
            return this.getMainNode().isOnline();
        } else {
            return this.isActive;
        }
    }

}
