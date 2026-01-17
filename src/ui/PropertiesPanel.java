package ui;

import core.Model3D;
import core.SceneManager;
import ui.dialogs.ErrorDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Панель трансформаций выбранной модели:
 * Масштаб, Вращение, Позиция.
 */
public class PropertiesPanel extends JPanel {

    private final JTextField scaleX  = new JTextField("1.0", 6);
    private final JTextField scaleY  = new JTextField("1.0", 6);
    private final JTextField scaleZ  = new JTextField("1.0", 6);

    private final JTextField rotX    = new JTextField("0.0", 6);
    private final JTextField rotY    = new JTextField("0.0", 6);
    private final JTextField rotZ    = new JTextField("0.0", 6);

    private final JTextField posX    = new JTextField("0.0", 6);
    private final JTextField posY    = new JTextField("0.0", 6);
    private final JTextField posZ    = new JTextField("0.0", 6);

    public PropertiesPanel(SceneManager sceneManager, JLabel statusBar) {
        setOpaque(false);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                "Трансформации",
                TitledBorder.LEFT,
                TitledBorder.TOP
        );
        border.setTitleColor(Color.WHITE);
        setBorder(border);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        // --- Масштаб ---
        row = addGroupHeader("Масштаб", row, c);
        row = addTripleRow("X:", scaleX, "Y:", scaleY, "Z:", scaleZ, row, c);

        JButton resetScale = new JButton("Сбросить масштаб");
        ButtonStyler.stylePrimary(resetScale);
        resetScale.addActionListener(e -> {
            scaleX.setText("1.0");
            scaleY.setText("1.0");
            scaleZ.setText("1.0");
            applyTransform(sceneManager, statusBar);
        });
        c.gridx = 0; c.gridy = row++; c.gridwidth = 3;
        add(resetScale, c);

        // --- Вращение ---
        row = addGroupHeader("Вращение (градусы)", row, c);
        row = addTripleRow("X:", rotX, "Y:", rotY, "Z:", rotZ, row, c);

        JButton resetRot = new JButton("Сбросить вращение");
        ButtonStyler.stylePrimary(resetRot);
        resetRot.addActionListener(e -> {
            rotX.setText("0.0");
            rotY.setText("0.0");
            rotZ.setText("0.0");
            applyTransform(sceneManager, statusBar);
        });
        c.gridx = 0; c.gridy = row++; c.gridwidth = 3;
        add(resetRot, c);

        // --- Позиция ---
        row = addGroupHeader("Позиция", row, c);
        row = addTripleRow("X:", posX, "Y:", posY, "Z:", posZ, row, c);

        JButton resetPos = new JButton("Сбросить позицию");
        ButtonStyler.stylePrimary(resetPos);
        resetPos.addActionListener(e -> {
            posX.setText("0.0");
            posY.setText("0.0");
            posZ.setText("0.0");
            applyTransform(sceneManager, statusBar);
        });
        c.gridx = 0; c.gridy = row++; c.gridwidth = 3;
        add(resetPos, c);

        // --- Пересчитать нормали ---
        row = addGroupHeader("Пересчитать нормали", row, c);
        JButton recalcNormals = new JButton("Пересчитать");
        ButtonStyler.stylePrimary(recalcNormals);
        c.gridx = 0; c.gridy = row++; c.gridwidth = 3;
        add(recalcNormals, c);
    }

    private int addGroupHeader(String title, int row, GridBagConstraints c) {
        JLabel label = new JLabel(title);
        label.setFont(getFont().deriveFont(Font.BOLD));
        label.setForeground(Color.WHITE); // белый заголовок
        c.gridx = 0; c.gridy = row++; c.gridwidth = 3;
        add(label, c);
        return row;
    }

    private int addTripleRow(String l1, JTextField f1,
                             String l2, JTextField f2,
                             String l3, JTextField f3,
                             int row, GridBagConstraints c) {
        c.gridwidth = 1;

        JLabel lab1 = new JLabel(l1);
        lab1.setForeground(Color.WHITE);
        JLabel lab2 = new JLabel(l2);
        lab2.setForeground(Color.WHITE);
        JLabel lab3 = new JLabel(l3);
        lab3.setForeground(Color.WHITE);

        c.gridx = 0; c.gridy = row;
        add(lab1, c);
        c.gridx = 1;
        add(f1, c);

        c.gridx = 0; c.gridy = row + 1;
        add(lab2, c);
        c.gridx = 1;
        add(f2, c);

        c.gridx = 0; c.gridy = row + 2;
        add(lab3, c);
        c.gridx = 1;
        add(f3, c);

        return row + 3;
    }

    private void applyTransform(SceneManager sceneManager, JLabel statusBar) {
        Model3D m = sceneManager.getSingleSelected();
        if (m == null) {
            ErrorDialog.showError(SwingUtilities.getWindowAncestor(this),
                    "Сначала выберите модель.");
            return;
        }
        try {
            float sx = Float.parseFloat(scaleX.getText());
            float sy = Float.parseFloat(scaleY.getText());
            float sz = Float.parseFloat(scaleZ.getText());

            float rxDeg = Float.parseFloat(rotX.getText());
            float ryDeg = Float.parseFloat(rotY.getText());
            float rzDeg = Float.parseFloat(rotZ.getText());

            float px = Float.parseFloat(posX.getText());
            float py = Float.parseFloat(posY.getText());
            float pz = Float.parseFloat(posZ.getText());

            float rx = (float) Math.toRadians(rxDeg);
            float ry = (float) Math.toRadians(ryDeg);
            float rz = (float) Math.toRadians(rzDeg);

            m.setScale(sx, sy, sz);
            m.setRotation(rx, ry, rz);
            m.setPosition(px, py, pz);

            statusBar.setText("Трансформации обновлены");
        } catch (NumberFormatException ex) {
            ErrorDialog.showError(SwingUtilities.getWindowAncestor(this),
                    "Неверный формат числа.");
        }
    }
}
