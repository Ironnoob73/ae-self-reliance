package dev.hail.ae_crystal_industries.gui;

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
}
