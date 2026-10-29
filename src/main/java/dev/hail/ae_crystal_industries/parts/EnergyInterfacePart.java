package dev.hail.ae_crystal_industries.parts;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import appeng.core.settings.TickRates;
import appeng.items.parts.PartModels;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.automation.UpgradeablePart;
import dev.hail.ae_crystal_industries.AeCrystalIndustriesMod;
import dev.hail.ae_crystal_industries.energy.EnergyInterfaceHost;
import dev.hail.ae_crystal_industries.energy.EnergyInterfaceLogic;
import dev.hail.ae_crystal_industries.inits.ACIMenus;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
        this.getMainNode().setIdlePowerUsage(0.0).setFlags();
        this.logic = new EnergyInterfaceLogic(this.getMainNode(), this.getUpgrades(), this::saveChanges,
                this::onActiveChanged);
        this.getMainNode().addService(IGridTickable.class, this);
    }

    private void saveChanges() {
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
        return new TickingRequest(TickRates.Charger, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var blockEntity = getBlockEntity();
        if (blockEntity != null && blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
            this.logic.tick(blockEntity.getLevel(), blockEntity.getBlockPos(), getSide(), ticksSinceLastCall);
        }
        return TickRateModulation.SAME;
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.logic.loadTag(data);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        this.logic.saveTag(data);
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
    public void cycleEnergyCapacity(int direction) {
        this.logic.cycleEnergyCapacity(direction);
    }

    @Override
    public long getStoredEnergy() {
        return this.logic.getStoredEnergy();
    }

    @Override
    public int getAePerFe() {
        return this.logic.getAePerFe();
    }

    @Override
    public boolean isActive() {
        return this.logic.isActive();
    }
}
