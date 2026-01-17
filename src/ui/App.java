package ui;

import core.SceneManager;

import javax.swing.*;

/**
 * Точка входа в приложение.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ThemeManager.applyDarkTheme();        // одна тёмная тема
            SceneManager sceneManager = new SceneManager();
            MainFrame frame = new MainFrame(sceneManager);
            frame.setVisible(true);
        });
    }
}
