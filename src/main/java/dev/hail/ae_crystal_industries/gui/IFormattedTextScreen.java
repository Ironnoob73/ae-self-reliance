package dev.hail.ae_crystal_industries.gui;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

import java.awt.*;
import java.util.Locale;

public interface IFormattedTextScreen {
    int plainTextColor = new Color(0x404040).hashCode();

    int errorTextColor = new Color(0xFF3F3F).hashCode();

    static String format(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }

    static String format(double value) {
        return String.format(Locale.ROOT, "%s", value);
    }

    class SetAmountButton extends IconButton {
        public SetAmountButton(OnPress onPress) {
            super(onPress);
            setDisableBackground(true);
        }

        @Override
        protected Icon getIcon() {
            return isHoveredOrFocused() ? Icon.COG : Icon.COG_DISABLED;
        }
    }
}
