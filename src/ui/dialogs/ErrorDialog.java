package ui.dialogs;

import javax.swing.*;
import java.awt.*;

/**
 * Красивый диалог ошибок.
 */
public class ErrorDialog {

    public static void showError(Window parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
    }
}
