package com.financetracker.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * ✨ UITheme — Polished, modern theme for Finance Tracker.
 * Uses FlatLaf with custom branding and future dark mode support.
 */
public class UITheme {

    // 🎨 Brand Palette
    private static final Color PRIMARY_COLOR = new Color(52, 120, 246);      // Bright Blue
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    private static final Color DANGER_COLOR  = new Color(231, 76, 60);       // Red
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);  // Light gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 33, 41);
    private static final Color BORDER_COLOR = new Color(220, 225, 235);

    // Dark mode (optional)
    private static boolean darkMode = false;

    public static void apply() {
        try {
            if (darkMode) {
                FlatDarkLaf.setup();
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                FlatLightLaf.setup();
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception e) {
            System.out.println("⚠️ FlatLaf not found, using default Swing theme");
        }

        // 🪄 Rounded Components
        UIManager.put("Button.arc", 18);
        UIManager.put("Component.arc", 14);
        UIManager.put("ProgressBar.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("PopupMenu.borderCornerRadius", 10);

        // 🖋 Typography
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("defaultFont", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);

        // 🎨 Colors
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("Button.background", PRIMARY_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.hoverBackground", PRIMARY_COLOR.darker());
        UIManager.put("Button.pressedBackground", PRIMARY_COLOR.darker().darker());
        UIManager.put("Table.background", CARD_COLOR);
        UIManager.put("Table.alternateRowColor", new Color(240, 243, 250));
        UIManager.put("Table.selectionBackground", new Color(210, 230, 255));
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", TEXT_COLOR);
        UIManager.put("ScrollBar.thumb", new Color(200, 200, 200));
        UIManager.put("ScrollBar.thumbHover", new Color(180, 180, 180));

        // 🧊 Cards (used in panels)
        Border cardBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
        UIManager.put("Panel.border", cardBorder);

        // 💬 Tooltips
        UIManager.put("ToolTip.background", Color.WHITE);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("ToolTip.foreground", TEXT_COLOR);

        System.out.println("✅ Modern UI theme applied successfully");
    }

    // 🌗 Optional dark mode toggle
    public static void enableDarkMode(boolean enable) {
        darkMode = enable;
        apply();
    }

    // 🎨 Utility color getters
    public static Color primary() { return PRIMARY_COLOR; }
    public static Color success() { return SUCCESS_COLOR; }
    public static Color danger()  { return DANGER_COLOR; }
    public static Color background() { return BACKGROUND_COLOR; }
    public static Color card() { return CARD_COLOR; }
}
