package ui;

import core.Model3D;
import core.SceneManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Левая панель со списком моделей.
 */
public class ModelListPanel extends JPanel {

    private final DefaultListModel<Model3D> listModel = new DefaultListModel<>();
    private final JList<Model3D> list = new JList<>(listModel);

    public ModelListPanel(SceneManager sceneManager) {
        super(new BorderLayout());
        setOpaque(false);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                "Модели",
                TitledBorder.LEFT,
                TitledBorder.TOP
        );
        border.setTitleColor(Color.WHITE);
        setBorder(border);

        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setBackground(UIManager.getColor("Panel.background"));
        list.setForeground(Color.WHITE); // белый текст
        list.setSelectionBackground(new Color(0x4A90E2));
        list.setSelectionForeground(Color.WHITE);
        list.setFixedCellHeight(24);

        add(new JScrollPane(list), BorderLayout.CENTER);

        sceneManager.addListener(new SceneManager.SceneListener() {
            @Override
            public void sceneChanged() {
                listModel.clear();
                for (Model3D m : sceneManager.getModels()) {
                    listModel.addElement(m);
                }
            }

            @Override
            public void selectionChanged() { }
        });

        list.addListSelectionListener(e ->
                sceneManager.setSelected(list.getSelectedValuesList()));
    }
}
