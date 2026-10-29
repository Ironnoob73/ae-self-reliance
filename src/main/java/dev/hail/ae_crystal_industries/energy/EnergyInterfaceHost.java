package dev.hail.ae_crystal_industries.energy;

import appeng.api.upgrades.IUpgradeableObject;

/**
 * Common interface implemented by both forms of the Energy Interface (the block entity and the cable part), so that a
 * single menu type can drive either of them.
 */
public interface EnergyInterfaceHost extends IUpgradeableObject {

    long getOutputRate();

    void cycleOutputRate(int direction);

    long getEnergyCapacity();

    void cycleEnergyCapacity(int direction);

    long getStoredEnergy();

    /**
     * @return how much AE is consumed to produce a single FE (1..4, reduced by AE2 Energy Cards).
     */
    int getAePerFe();

    boolean isActive();
}
