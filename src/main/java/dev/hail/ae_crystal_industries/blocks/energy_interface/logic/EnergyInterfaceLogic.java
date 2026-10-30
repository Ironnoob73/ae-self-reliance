package dev.hail.ae_crystal_industries.blocks.energy_interface.logic;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * By Deepseek V4.1 Flash 1017
 * Shared AE-to-FE conversion logic of the Energy Interface block and cable part.
 * <p>
 * AE is drained from the grid and converted into FE at a ratio of {@code aePerFe : 1} (4:1 by default, reduced by one
 * for every installed AE2 Energy Card, down to 1:1 with four cards). The produced FE is buffered internally and pushed
 * into adjacent FE receivers at the configured output rate.
 */
public final class EnergyInterfaceLogic {

    /** AE consumed per FE without any Energy Cards. */
    public static final int MAX_AE_PER_FE = 4;
    public static final long DEFAULT_OUTPUT_RATE = 1_000;
    public static final long DEFAULT_CAPACITY = 1_000_000;

    public static final long[] OUTPUT_RATE_STEPS = {
            0, 10, 25, 50, 100, 250, 500, 1_000, 2_500, 5_000, 10_000, 25_000, 50_000, 100_000
    };

    public static final long[] CAPACITY_STEPS = {
            10_000, 50_000, 100_000, 250_000, 500_000, 1_000_000,
            2_500_000, 5_000_000, 10_000_000, 25_000_000, 50_000_000, 100_000_000
    };

    public static final long MAX_OUTPUT_RATE = OUTPUT_RATE_STEPS[OUTPUT_RATE_STEPS.length - 1];
    public static final long MIN_CAPACITY = CAPACITY_STEPS[0];
    public static final long MAX_CAPACITY = CAPACITY_STEPS[CAPACITY_STEPS.length - 1];

    private final EnergyInterfaceHost host;
    private final IManagedGridNode mainNode;
    private final IUpgradeInventory upgrades;
    private final Runnable saveChanges;
    private final Runnable onActiveChanged;

    private long storedEnergy;
    private long outputRate = DEFAULT_OUTPUT_RATE;
    private long capacity = DEFAULT_CAPACITY;
    private boolean active;
    private int priority;
    /**
     * FE that may still leave the buffer during the current tick interval. It is shared between the machine's own
     * push into adjacent receivers and direct extraction through the exposed {@link IEnergyStorage}, so external
     * cables cannot bypass the configured output rate.
     */
    private long outputBudget;

    private final IEnergyStorage energyStorage = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            // This is a generator, not an energy sink.
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (maxExtract <= 0) {
                return 0;
            }
            int amount = (int) Math.min(Math.min(maxExtract, storedEnergy), Math.min(outputBudget,
                    Integer.MAX_VALUE));
            if (amount <= 0) {
                return 0;
            }
            if (!simulate) {
                storedEnergy -= amount;
                outputBudget -= amount;
                saveChanges.run();
            }
            return amount;
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(storedEnergy, Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(capacity, Integer.MAX_VALUE);
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    public EnergyInterfaceLogic(IManagedGridNode mainNode, EnergyInterfaceHost host, IUpgradeInventory upgrades,
            Runnable saveChanges, Runnable onActiveChanged) {
        this.host = host;
        this.mainNode = mainNode;
        this.upgrades = upgrades;
        this.saveChanges = saveChanges;
        this.onActiveChanged = onActiveChanged;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public long getOutputRate() {
        return outputRate;
    }

    public long getEnergyCapacity() {
        return capacity;
    }

    public long getStoredEnergy() {
        return storedEnergy;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * @return AE consumed per FE produced, reduced by one for every installed AE2 Energy Card (to a minimum of 1).
     */
    public int getAePerFe() {
        return Math.max(1, MAX_AE_PER_FE - upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD));
    }

    public void cycleOutputRate(int direction) {
        outputRate = nextStep(OUTPUT_RATE_STEPS, outputRate, direction);
        saveChanges.run();
    }

    public void cycleEnergyCapacity(int direction) {
        capacity = nextStep(CAPACITY_STEPS, capacity, direction);
        if (storedEnergy > capacity) {
            storedEnergy = capacity;
        }
        saveChanges.run();
    }

    public void setOutputRate(long value) {
        long newRate = Math.clamp(value, 0, MAX_OUTPUT_RATE);
        if (newRate == outputRate) {
            return;
        }
        this.outputRate = newRate;
        saveChanges.run();
    }

    public void setCapacity(long value) {
        long newCapacity = Math.clamp(value, MIN_CAPACITY, MAX_CAPACITY);
        if (newCapacity == capacity) {
            return;
        }
        this.capacity = newCapacity;
        if (storedEnergy > capacity) {
            storedEnergy = capacity;
        }
        saveChanges.run();
    }

    /**
     * Converts AE into FE and pushes the buffered FE into adjacent receivers.
     *
     * @param outputSide if non-null, only the block in this direction receives FE (used by the cable part form)
     * @param ticks      how many game ticks passed since the previous call
     */
    public void tick(Level level, BlockPos pos, @Nullable Direction outputSide, int ticks) {
        if (level == null || level.isClientSide() || ticks <= 0) {
            return;
        }

        long before = storedEnergy;
        long perTickBudget = multiplySaturated(outputRate, ticks);
        this.outputBudget = perTickBudget;

        long room = capacity - storedEnergy;
        long targetFe = Math.clamp(room, 0, perTickBudget);
        long produced = 0;

        // Priority function by Deepseek V4.1 Flash 1017
        IGrid grid = mainNode.getGrid();
        // Interfaces with a higher priority are filled first. While any of them still has room in its buffer and is
        // able to charge, this interface must not take AE away from it.
        if (targetFe > 0 && grid != null && !isOutrankedByChargingInterface(grid)) {
            IEnergyService energy = grid.getEnergyService();
            int aePerFe = getAePerFe();
            double neededAe = targetFe * (double) aePerFe;

            // Only extract if at least one whole FE can be produced, so no AE is wasted on partial conversions.
            double availableAe = energy.extractAEPower(neededAe, Actionable.SIMULATE, PowerMultiplier.ONE);
            long feasibleFe = (long) Math.floor(availableAe / aePerFe + 1.0E-9);
            if (feasibleFe > 0) {
                double extractedAe = energy.extractAEPower(feasibleFe * (double) aePerFe,
                        Actionable.MODULATE, PowerMultiplier.ONE);
                produced = (long) Math.floor(extractedAe / aePerFe + 1.0E-9);
                if (produced > 0) {
                    storedEnergy = Math.min(capacity, storedEnergy + produced);
                }
            }
        }

        long pushed = pushEnergy(level, pos, outputSide, perTickBudget);

        boolean nowActive = produced > 0;
        if (nowActive != active) {
            active = nowActive;
            onActiveChanged.run();
        }

        if (storedEnergy != before || pushed > 0) {
            saveChanges.run();
        }
    }

    /**
     * By Deepseek V4.1 Flash 1017:
     * Checks whether another interface on this grid has a higher priority and is still waiting to be charged.
     * <p>
     * The interfaces are ticked independently by AE2's tick manager, so they consult each other instead of relying on a
     * particular tick order. The higher-priority interface may then consume AE as usual, while this interface only
     * pushes the FE it already buffered. Once the higher-priority buffer is full (or the interface is disabled), this
     * interface starts charging as well.
     */
    private boolean isOutrankedByChargingInterface(IGrid grid) {
        for (IGridNode node : grid.getNodes()) {
            if (node.getOwner() instanceof EnergyInterfaceHost otherHost) {
                EnergyInterfaceLogic other = otherHost.getInterfaceLogic();
                if (other != null && other != this
                        && other.priority > priority
                        && other.wantsToCharge()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * By Deepseek V4.1 Flash 1017:
     * @return true if this interface can still consume AE into its buffer during the next tick.
     */
    private boolean wantsToCharge() {
        // An interface with a disabled output rate never produces FE, so it must not starve lower priorities
        // forever. Likewise, an inactive node (no power/channel or an unloaded chunk) cannot consume AE right now.
        return outputRate > 0 && storedEnergy < capacity && mainNode.isActive();
    }

    private long pushEnergy(Level level, BlockPos pos, @Nullable Direction side, long budget) {
        long available = Math.min(Math.min(budget, storedEnergy), outputBudget);
        if (available <= 0) {
            return 0;
        }

        long remaining;
        if (side != null) {
            remaining = pushTo(level, pos.relative(side), side.getOpposite(), available);
        } else {
            remaining = available;
            for (Direction direction : Direction.values()) {
                if (remaining <= 0) {
                    break;
                }
                remaining = pushTo(level, pos.relative(direction), direction.getOpposite(), remaining);
            }
        }

        long pushed = available - remaining;
        if (pushed > 0) {
            storedEnergy -= pushed;
            outputBudget -= pushed;
        }
        return pushed;
    }

    private static long pushTo(Level level, BlockPos targetPos, Direction side, long amount) {
        if (amount <= 0 || !level.isLoaded(targetPos)) {
            return amount;
        }

        IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, side);
        if (storage == null) {
            return amount;
        }

        int requested = (int) Math.min(amount, Integer.MAX_VALUE);
        int accepted = storage.receiveEnergy(requested, true);
        if (accepted <= 0) {
            return amount;
        }

        accepted = storage.receiveEnergy(accepted, false);
        return amount - accepted;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
        this.host.saveChanges();
    }

    public void saveTag(CompoundTag tag) {
        tag.putLong("output_rate", outputRate);
        tag.putLong("capacity", capacity);
        tag.putLong("stored_energy", storedEnergy);
        tag.putInt("priority", this.priority);
    }

    public void loadTag(CompoundTag tag) {
        if (tag.contains("output_rate")) {
            outputRate = Math.clamp(tag.getLong("output_rate"), 0, OUTPUT_RATE_STEPS[OUTPUT_RATE_STEPS.length - 1]);
        }
        if (tag.contains("capacity")) {
            capacity = Math.clamp(tag.getLong("capacity"), CAPACITY_STEPS[0], CAPACITY_STEPS[CAPACITY_STEPS.length - 1]);
        }
        priority = tag.getInt("priority");
        storedEnergy = Math.clamp(tag.getLong("stored_energy"), 0, capacity);
    }

    private static long nextStep(long[] values, long current, long direction) {
        int index = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] <= current) {
                index = i;
            }
        }

        if (direction > 0) {
            index = Math.min(values.length - 1, index + 1);
        } else if (direction < 0) {
            index = Math.max(0, index - 1);
        }

        return values[index];
    }

    private static long multiplySaturated(long value, int multiplier) {
        if (value <= 0 || multiplier <= 0) {
            return 0;
        }
        if (value > Long.MAX_VALUE / multiplier) {
            return Long.MAX_VALUE;
        }
        return value * multiplier;
    }
}
