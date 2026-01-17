package ui;

import core.SceneManager;

import javax.swing.*;
import java.awt.*;

/**
 * Главное окно: большое поле под модель + узкие панели слева и справа.
 */
public class MainFrame extends JFrame {

    private final SceneManager sceneManager;
    private final JLabel statusBar = new JLabel("Готово");

    public MainFrame(SceneManager sceneManager) {
        super("3D‑редактор");
        this.sceneManager = sceneManager;
        initUi();
    }

    private void initUi() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setJMenuBar(MenuBuilder.buildMenu(this, sceneManager, statusBar));

        ModelListPanel listPanel = new ModelListPanel(sceneManager);
        PropertiesPanel propertiesPanel = new PropertiesPanel(sceneManager, statusBar);
        SelectionEditPanel selectionPanel = new SelectionEditPanel(sceneManager, statusBar);

        JPanel right = new JPanel(new BorderLayout());
        right.add(propertiesPanel, BorderLayout.NORTH);
        right.add(selectionPanel, BorderLayout.CENTER);
        right.setPreferredSize(new Dimension(320, 600));

        listPanel.setPreferredSize(new Dimension(220, 600));

        JPanel center = new JPanel();
        center.setBackground(Color.BLACK);

        add(listPanel, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
    }
}
