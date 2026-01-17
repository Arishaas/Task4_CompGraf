package ui;

import core.Model3D;
import core.SceneManager;
import ui.dialogs.ErrorDialog;

import javax.swing.*;
import java.io.File;

/**
 * Меню "Файл" с пунктами открытия/сохранения модели.
 */
public class MenuBuilder {

    public static JMenuBar buildMenu(JFrame owner,
                                     SceneManager sceneManager,
                                     JLabel statusBar) {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("Файл");
        JMenuItem open = new JMenuItem("Открыть модель...");
        JMenuItem save = new JMenuItem("Сохранить как...");

        open.addActionListener(e -> doOpen(owner, sceneManager, statusBar));
        save.addActionListener(e -> doSave(owner, sceneManager, statusBar));

        file.add(open);
        file.add(save);

        bar.add(file);

        return bar; // ВАЖНО: вернуть созданное меню
    }

    private static void doOpen(JFrame owner,
                               SceneManager sceneManager,
                               JLabel statusBar) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            new LoadModelWorker(f, sceneManager, statusBar, owner).execute();
        }
    }

    private static void doSave(JFrame owner,
                               SceneManager sceneManager,
                               JLabel statusBar) {
        Model3D model = sceneManager.getSingleSelected();
        if (model == null) {
            ErrorDialog.showError(owner, "Нет выбранной модели для сохранения.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            new SaveModelWorker(f, model, statusBar, owner).execute();
        }
    }
}
