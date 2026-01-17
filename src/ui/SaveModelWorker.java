package ui;

import core.Model3D;
import ui.dialogs.ErrorDialog;
import ui.dialogs.ProgressDialog;

import javax.swing.*;
import java.io.File;

/**
 * Сохраняет выбранную модель в OBJ через ObjWriter.
 */
public class SaveModelWorker extends SwingWorker<Void, Void> {

    private final File file;
    private final Model3D model;
    private final JLabel statusBar;
    private final JFrame owner;
    private ProgressDialog dialog;

    public SaveModelWorker(File file, Model3D model,
                           JLabel statusBar, JFrame owner) {
        this.file = file;
        this.model = model;
        this.statusBar = statusBar;
        this.owner = owner;
    }

    @Override
    protected Void doInBackground() throws Exception {
        SwingUtilities.invokeLater(() -> {
            dialog = new ProgressDialog(owner, "Сохранение модели...");
            dialog.setVisible(true);
        });

        // ЗДЕСЬ ВАШ ObjWriter
        // ObjWriter.saveModel(model, file.getAbsolutePath());

        return null;
    }

    @Override
    protected void done() {
        if (dialog != null) dialog.dispose();
        try {
            get();
            statusBar.setText("Модель сохранена: " + file.getName());
        } catch (Exception ex) {
            ErrorDialog.showError(owner,
                    "Ошибка сохранения:\n" + ex.getMessage());
        }
    }
}
