package dev.hail.ae_crystal_industries.blocks.energy_interface;

import appeng.api.config.*;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigManagerBuilder;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.items.parts.PartModels;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.automation.UpgradeablePart;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import dev.hail.ae_crystal_industries.blocks.energy_interface.logic.EnergyInterfaceHost;
import dev.hail.ae_crystal_industries.blocks.energy_interface.logic.EnergyInterfaceLogic;
import dev.hail.ae_crystal_industries.inits.ACIMenus;
import dev.hail.ae_crystal_industries.inits.ACIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.energy.IEnergyStorage;

// Init By Deepseek V4.1 Flash 1017:
public class EnergyInterfacePart extends UpgradeablePart implements EnergyInterfaceHost, IGridTickable {

    @PartModels
    private static final IPartModel MODELS = new PartModel(
            ResourceLocation.fromNamespaceAndPath(AeCrystalIndustriesMod.MOD_ID, "part/energy_interface"));

    private final EnergyInterfaceLogic logic;

    public EnergyInterfacePart(IPartItem<?> partItem) {
        super(partItem);
        // Like the Energy Acceptor, this device does not need a channel.
        // By LD_Anvil: Actually it need...
        this.getMainNode().setIdlePowerUsage(0.0).setFlags(GridFlags.REQUIRE_CHANNEL);
        this.logic = new EnergyInterfaceLogic(this.getMainNode(), this, this.getUpgrades(), this::saveChanges,
                this::onActiveChanged);
        this.getMainNode().addService(IGridTickable.class, this);
    }

    @Override
    protected void registerSettings(IConfigManagerBuilder builder) {
        super.registerSettings(builder);
        builder.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
    }

    @Override
    public RedstoneMode getRSMode() {
        return this.getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
    }

    private boolean isInPulseMode() {
        return this.getUpgrades().isInstalled(AEItems.REDSTONE_CARD)
                && getRSMode() == RedstoneMode.SIGNAL_PULSE;
    }

    public void saveChanges() {
        this.getHost().markForSave();
    }

    private void onActiveChanged() {
        // The static part model does not change with the active state.
    }

    public IEnergyStorage getEnergyStorage() {
        return this.logic.getEnergyStorage();
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(4, 4, 12, 12, 12, 14);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 2;
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS;
    }

    @Override
    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!isClientSide()) {
            MenuOpener.open(ACIMenus.ENERGY_INTERFACE_MENU, player, MenuLocators.forPart(this));
        }
        return true;
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

        this.logic.setRunning(true);

        var blockEntity = getBlockEntity();
        if (blockEntity != null && blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
            // A redstone pulse performs exactly one tick of work, no matter how long the interface slept before it.
            int effectiveTicks = isInPulseMode() && this.logic.pendingPulse ? 1 : ticksSinceLastCall;
            this.logic.tick(blockEntity.getLevel(), blockEntity.getBlockPos(), getSide(), effectiveTicks);
        }
        this.logic.pendingPulse = false;
        return TickRateModulation.SAME;
    }

    @Override
    protected boolean isSleeping() {
        if (isInPulseMode() && this.logic.pendingPulse) {
            return false;
        } else {
            return super.isSleeping();
        }
    }

    @Override
    protected void onSettingChanged(IConfigManager manager, Setting<?> setting) {
        super.onSettingChanged(manager, setting);

        updateRedstoneState();

        if (isInPulseMode()) {
            // Switching into pulse mode must not treat an already powered input as a fresh pulse.
            this.logic.pendingPulse = false;
            this.logic.lastRedstone = getHost().hasRedstone();
        }
    }

    @Override
    public void upgradesChanged() {
        super.upgradesChanged();
        // Adding or removing a redstone card changes whether redstone control applies.
        updateRedstoneState();
    }

    private void updateRedstoneState() {
        if (!this.isInPulseMode()) {
            this.logic.pendingPulse = false;
        }

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

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.logic.loadTag(data);
        logic.pendingPulse = isInPulseMode() && data.getBoolean("pendingPulse");
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        this.logic.saveTag(data);
        if (isInPulseMode() && logic.pendingPulse) {
            data.putBoolean("pendingPulse", true);
        }
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        if (isInPulseMode()) {
            var hostIsPowered = this.getHost().hasRedstone();
            if (this.logic.lastRedstone != hostIsPowered) {
                this.logic.lastRedstone = hostIsPowered;
                if (this.logic.lastRedstone && !this.logic.pendingPulse) {
                    // Perform exactly one tick of work for this pulse.
                    this.logic.pendingPulse = true;
                    this.logic.setRunning(true);
                    getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
                }
            }
        }

        // Also put the part back to sleep on a falling edge (pulse mode) and keep the shared running state in sync.
        updateRedstoneState();
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
    public long getNextOutputRate(int direction) {
        return this.logic.getNextOutputRate(direction);
    }

    @Override
    public void setOutputRate(long value) {
        this.logic.setOutputRate(value);
    }

    @Override
    public long getEnergyCapacity() {
        return this.logic.getEnergyCapacity();
    }

    @Override
    public void cycleEnergyCapacity(int direction) {
        this.logic.cycleEnergyCapacity(direction);
    }

    @Override
    public long getNextCapacity(int direction) {
        return this.logic.getNextCapacity(direction);
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
        return ACIItems.ENERGY_INTERFACE_PART.get().getDefaultInstance();
    }

    @Override
    public int getAePerFe() {
        return this.logic.getAePerFe();
    }

    @Override
    public boolean isActive() {
        return this.logic.isActive();
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.logic.lastRedstone = this.getHost().hasRedstone();
        if (this.logic.pendingPulse) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
        }
    }
}
