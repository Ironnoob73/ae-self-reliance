package dev.hail.ae_self_reliance.blocks;

import appeng.menu.implementations.UpgradeableMenu;
import net.minecraft.world.entity.player.Inventory;

public class SunlightResonanceGeneratorMenu extends UpgradeableMenu<SunlightResonanceGeneratorBlockEntity> {
    public SunlightResonanceGeneratorMenu(int id, Inventory ip, SunlightResonanceGeneratorBlockEntity host) {
        super(ASRMenus.SUNLIGHT_RESONANCE_GENERATOR_MENU.get(), id, ip, host);
    }
}
