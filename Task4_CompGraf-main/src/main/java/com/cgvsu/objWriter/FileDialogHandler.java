package com.cgvsu.objWriter;

import com.cgvsu.model.Model;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Класс FileDialogHandler предназначен для управления диалогами выбора файла и сохранения модели.
 */
public class FileDialogHandler {

    /**
     * Метод для сохранения модели с использованием окна выбора файла.
     *
     * @param model Модель, которую нужно сохранить.
     */
    public static void saveModel(Model model, boolean flag) {
        FileChooser fileChooser = new FileChooser();

        // Устанавливаем фильтр для выбора только файлов с расширением .obj
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("OBJ Files (*.obj)", "*.obj");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Устанавливаем начальную директорию (по желанию)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Сохранить модель");

        // Отображаем диалог выбора файла
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            // Создаем экземпляр ObjWriter и записываем файл
            ObjWriter.write(model, file.getAbsolutePath(), flag);
        }
    }
}
