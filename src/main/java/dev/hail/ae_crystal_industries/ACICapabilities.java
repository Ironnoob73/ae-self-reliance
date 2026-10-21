package dev.hail.ae_crystal_industries;

import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import dev.hail.ae_crystal_industries.blocks.ACIBlockEntities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Registers AE2 capabilities for this mod's block entities.
 * <p>
 * AE2 only registers its {@link AECapabilities#IN_WORLD_GRID_NODE_HOST} capability for its own block entity types.
 * Addon block entities must register it themselves, otherwise AE2 cannot discover their grid nodes when scanning from
 * a neighboring block: e.g. placing the generator first and then an AE2 cable / controller / cell next to it would
 * never connect, because the neighbor's node lookup goes through this capability.
 */
public final class ACICapabilities {

    private ACICapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        for (var type : ACIBlockEntities.INSTANCE.getImplementorsOf(IInWorldGridNodeHost.class)) {
            event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, type,
                    (object, context) -> (IInWorldGridNodeHost) object);
        }
    }
}