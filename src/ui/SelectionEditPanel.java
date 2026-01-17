package ui;

import core.Model3D;
import core.SceneManager;
import ui.dialogs.ErrorDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Панель для отображения выбранных вершин/полигонов и их удаления.
 */
public class SelectionEditPanel extends JPanel {

    private final DefaultTableModel model =
            new DefaultTableModel(new Object[]{"Тип", "Индекс"}, 0);
    private final JTable table = new JTable(model);

    public SelectionEditPanel(SceneManager sceneManager, JLabel statusBar) {
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                "Выбор элементов",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                getFont().deriveFont(Font.BOLD)
        ));
        setLayout(new BorderLayout(0, 6));

        table.setFillsViewportHeight(true);
        table.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton delete = new JButton("Удалить выбранное");
        ButtonStyler.stylePrimary(delete);
        delete.setToolTipText("Удалить выбранные вершины/полигоны модели");

        delete.addActionListener(e -> deleteSelected(sceneManager, statusBar));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        bottom.setOpaque(false);
        bottom.add(delete);

        add(bottom, BorderLayout.SOUTH);
    }

    private void deleteSelected(SceneManager sceneManager, JLabel statusBar) {
        Model3D m = sceneManager.getSingleSelected();
        if (m == null) {
            ErrorDialog.showError(SwingUtilities.getWindowAncestor(this),
                    "Нет активной модели.");
            return;
        }

        int[] rows = table.getSelectedRows();
        if (rows.length == 0) return;

        int res = JOptionPane.showConfirmDialog(
                this,
                "Удалить выбранные элементы?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION
        );
        if (res != JOptionPane.YES_OPTION) return;

        for (int i = rows.length - 1; i >= 0; i--) {
            String type = (String) model.getValueAt(rows[i], 0);
            int index = (int) model.getValueAt(rows[i], 1);
            // здесь должны быть вызовы методов меша другого студента
            // if ("vertex".equals(type)) {
            //     m.getMesh().deleteVertex(index);
            // } else if ("polygon".equals(type)) {
            //     m.getMesh().deletePolygon(index);
            // }
            model.removeRow(rows[i]);
        }
        statusBar.setText("Элементы удалены");
    }

    public void addVertex(int index) {
        model.addRow(new Object[]{"vertex", index});
    }

    public void addPolygon(int index) {
        model.addRow(new Object[]{"polygon", index});
    }

    public void clearAll() {
        model.setRowCount(0);
    }
}
