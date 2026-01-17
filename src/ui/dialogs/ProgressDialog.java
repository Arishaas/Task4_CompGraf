package ui.dialogs;

import javax.swing.*;
import java.awt.*;

/**
 * Простое модальное окно с индикатором прогресса
 * для операций загрузки/сохранения модели.
 */
public class ProgressDialog extends JDialog {

    private final JProgressBar progressBar = new JProgressBar();

    public ProgressDialog(Window owner, String title) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());
        progressBar.setIndeterminate(true);
        add(progressBar, BorderLayout.CENTER);
        setSize(300, 80);
        setLocationRelativeTo(owner);
    }
}
