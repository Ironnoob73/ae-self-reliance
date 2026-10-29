package dev.hail.ae_crystal_industries.blocks.energy_interface;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.settings.TickRates;
import dev.hail.ae_crystal_industries.energy.EnergyInterfaceHost;
import dev.hail.ae_crystal_industries.energy.EnergyInterfaceLogic;
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
        implements IGridTickable, IUpgradeableObject, EnergyInterfaceHost {

    private final IUpgradeInventory upgrades;
    private final EnergyInterfaceLogic logic;

    public EnergyInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().setIdlePowerUsage(0.0).setFlags(GridFlags.REQUIRE_CHANNEL);
        this.upgrades = UpgradeInventories.forMachine(ACIBlocks.ENERGY_INTERFACE.get().asItem(), 4,
                this::onUpgradesChanged);
        this.logic = new EnergyInterfaceLogic(this.getMainNode(), this.upgrades, this::saveChanges,
                this::onActiveChanged);
        this.getMainNode().addService(IGridTickable.class, this);
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
        return super.readFromStream(data);
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        this.logic.saveTag(data);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
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
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.Charger, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.level != null) {
            this.logic.tick(this.level, this.worldPosition, null, ticksSinceLastCall);
        }
        return TickRateModulation.SAME;
    }

    @Override
    public long getOutputRate() {
        return this.logic.getOutputRate();
    }

    @Override
    public void cycleOutputRate(long direction) {
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
    public void cycleEnergyCapacity(long direction) {
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
    public ItemStack getMainMenuIcon() {
        return ACIBlocks.ENERGY_INTERFACE.get().asItem().getDefaultInstance();
    }

    @Override
    public int getAePerFe() {
        return this.logic.getAePerFe();
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
