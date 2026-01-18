package com.cgvsu;

import com.cgvsu.math.Vector3f;

import java.io.*;

public class Main {
    public static void main(String[] args) {


        // Создаем файл для записи вывода
        File outputFile = new File("output.txt");

        try {
            // Создаем PrintStream для записи в файл
            PrintStream fileStream = new PrintStream(outputFile);

            // Перенаправляем стандартный вывод в файл
            System.setOut(fileStream);

            // Теперь все вызовы System.out.println() будут записываться в файл
            System.out.println("Этот текст будет записан в файл.");
            System.out.println("Еще одна строка в файл.");

            // Восстанавливаем стандартный вывод в консоль
                  System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

            // Теперь вывод снова идет в консоль
          System.out.println("Этот текст будет выведен в консоль.");

            Simple3DViewer.main(args);

            Vector3f testVector = new Vector3f(0,1,1);
            Vector3f a = testVector.add(new Vector3f(1,1,1));
            System.out.println(a);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
