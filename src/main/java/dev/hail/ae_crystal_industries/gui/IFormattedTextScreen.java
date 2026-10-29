package dev.hail.ae_crystal_industries.gui;

import java.util.Locale;

public interface IFormattedTextScreen {
    int textColor = 0x404040;

    static String format(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }

    static String format(double value) {
        return String.format(Locale.ROOT, "%s", value);
    }
}
