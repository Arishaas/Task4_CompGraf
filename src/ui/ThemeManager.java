package ui;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

/**
 * Одна тёмная тема для всего приложения.
 */
public class ThemeManager {

    private static final Color DARK_BG = Color.decode("#202124");
    private static final Color DARK_PANEL = Color.decode("#2B2B2B");
    private static final Color DARK_FG = Color.WHITE;
    private static final Color DARK_BORDER = Color.decode("#404040");
    private static final Color DARK_BUTTON = Color.decode("#3C4043");

    public static void applyDarkTheme() {
        UIManager.put("control", DARK_BG);
        UIManager.put("Panel.background", DARK_PANEL);
        UIManager.put("Viewport.background", DARK_PANEL);
        UIManager.put("List.background", DARK_PANEL);
        UIManager.put("List.foreground", DARK_FG);
        UIManager.put("Label.foreground", DARK_FG);
        UIManager.put("Button.background", DARK_BUTTON);
        UIManager.put("Button.foreground", DARK_FG);
        UIManager.put("TextField.background", Color.decode("#303134"));
        UIManager.put("TextField.foreground", DARK_FG);
        UIManager.put("TextField.caretForeground", DARK_FG);
        UIManager.put("TextField.border",
                BorderFactory.createLineBorder(DARK_BORDER));
        UIManager.put("TitledBorder.border",
                BorderFactory.createLineBorder(DARK_BORDER));

        setGlobalFont(new Font("Segoe UI", Font.PLAIN, 13));
        updateUi();
    }

    private static void setGlobalFont(Font font) {
        FontUIResource fui = new FontUIResource(font);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fui);
            }
        }
    }

    private static void updateUi() {
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
        }
    }
}
