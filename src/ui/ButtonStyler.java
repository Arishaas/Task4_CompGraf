package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Стилизация кнопок + подсветка при наведении и нажатии.
 */
public class ButtonStyler {

    public static void stylePrimary(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(4, 10, 4, 10));

        Color base = UIManager.getColor("Button.background");
        if (base == null) {
            base = new Color(0x3C4043);
        }
        final Color normal = base;
        final Color hover = normal.brighter();
        final Color pressed = normal.darker();

        Color fg = UIManager.getColor("Button.foreground");
        if (fg == null) fg = Color.WHITE;

        button.setBackground(normal);
        button.setForeground(fg);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.getBounds().contains(e.getPoint())) {
                    button.setBackground(hover);
                } else {
                    button.setBackground(normal);
                }
            }
        });
    }
}
