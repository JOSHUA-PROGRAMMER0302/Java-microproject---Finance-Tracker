package com.financetracker;

import com.financetracker.ui.MainFrame;
import com.financetracker.ui.UITheme;
import javax.swing.SwingUtilities;

/**
 * Entry point for the Finance Tracker application.
 * Initializes the modern UI theme and launches the main frame.
 */
public class App {
    public static void main(String[] args) {

        // Apply the modern FlatLaf-based theme
        UITheme.apply();

        // Launch the main window on the Swing event thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
