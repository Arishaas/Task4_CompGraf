package ui;

import core.Model3D;
import core.SceneManager;
import ui.dialogs.ErrorDialog;
import ui.dialogs.ProgressDialog;

import javax.swing.*;
import java.io.File;

/**
 * Читает OBJ в фоне через ObjReader и добавляет модель в сцену.
 * Вместо Object подставь реальный тип mesh.
 */
public class LoadModelWorker extends SwingWorker<Model3D, Void> {

    private final File file;
    private final SceneManager sceneManager;
    private final JLabel statusBar;
    private final JFrame owner;
    private ProgressDialog dialog;

    public LoadModelWorker(File file,
                           SceneManager sceneManager,
                           JLabel statusBar,
                           JFrame owner) {
        this.file = file;
        this.sceneManager = sceneManager;
        this.statusBar = statusBar;
        this.owner = owner;
    }

    @Override
    protected Model3D doInBackground() throws Exception {
        SwingUtilities.invokeLater(() -> {
            dialog = new ProgressDialog(owner, "Загрузка модели...");
            dialog.setVisible(true);
        });

        // ЗДЕСЬ ВАШ ObjReader
        // Object mesh = ObjReader.loadModel(file.getAbsolutePath());
        Object mesh = null;

        String name = file.getName();
        return new Model3D(name, mesh);
    }

    @Override
    protected void done() {
        if (dialog != null) dialog.dispose();
        try {
            Model3D model = get();
            sceneManager.addModel(model);
            statusBar.setText("Загружена модель: " + model.getName());
        } catch (Exception ex) {
            ErrorDialog.showError(owner,
                    "Ошибка загрузки файла:\n" + ex.getMessage());
        }
    }
}
