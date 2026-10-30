package dev.hail.ae_crystal_industries.inits;

import appeng.api.AECapabilities;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import dev.hail.ae_crystal_industries.blocks.energy_interface.EnergyInterfacePart;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Registers AE2 and NeoForge capabilities for this mod's block entities and cable parts.
 */
public final class ACICapabilities {

    private ACICapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        // AE2 only registers this capability for its own block entities. Addons must do it themselves, otherwise
        // AE2 cannot discover the grid node when it scans in from a neighboring cable/controller.
        event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ACIBlockEntities.SUNLIGHT_RESONANCE_GENERATOR.get(),
                (blockEntity, side) -> blockEntity);
        event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ACIBlockEntities.ENERGY_INTERFACE.get(),
                (blockEntity, side) -> blockEntity);

        // The Energy Interface exposes its FE buffer on every side.
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ACIBlockEntities.ENERGY_INTERFACE.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage());
    }

    public static void registerPartCapabilities(RegisterPartCapabilitiesEvent event) {
        event.register(Capabilities.EnergyStorage.BLOCK,
                (part, side) -> part.getEnergyStorage(),
                EnergyInterfacePart.class);
    }
}
