package com.cgvsu.math.tests.Model;

import com.cgvsu.model.CalculateNormals;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.cgvsu.math.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Дополнительные тесты для проверки корректности расчета нормалей методом `CalculateNormals`.
 */
class ModelNormalsAdditionalTests {

    /**
     * Тест на плоский квадрат.
     */
    @Test
    void calculateNormalsForFlatSquare() {
        // Создаем модель
        Model model = new Model();

        // Вершины квадрата
        model.getVertices().add(new Vector3f(0.0f, 0.0f, 0.0f)); // Вершина 0
        model.getVertices().add(new Vector3f(1.0f, 0.0f, 0.0f)); // Вершина 1
        model.getVertices().add(new Vector3f(1.0f, 1.0f, 0.0f)); // Вершина 2
        model.getVertices().add(new Vector3f(0.0f, 1.0f, 0.0f)); // Вершина 3

        // Полигон квадрата
        Polygon square = new Polygon();
        square.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2, 3)));

        // Добавляем полигон в модель
        model.getPolygons().add(square);

        // Рассчитываем нормали
        ArrayList<Vector3f> calculatedNormals = CalculateNormals.calculateNormals(model);

        // Ожидаемая нормаль для всех вершин (плоскость OXY, нормаль вдоль оси Z)
        Vector3f expectedNormal = new Vector3f(0.0f, 0.0f, 1.0f);

        // Проверяем, что все вершины имеют одинаковую нормаль
        for (Vector3f normal : calculatedNormals) {
            assertEquals(expectedNormal.getX(), normal.getX(), 0.0001);
            assertEquals(expectedNormal.getY(), normal.getY(), 0.0001);
            assertEquals(expectedNormal.getZ(), normal.getZ(), 0.0001);
        }
    }

    /**
     * Тест на треугольную призму.
     */
    @Test
    void calculateNormalsForTriangularPrism() {
        // Создаем модель
        Model model = new Model();

        // Вершины призмы
        model.getVertices().add(new Vector3f(0.0f, 0.0f, 0.0f)); // 0
        model.getVertices().add(new Vector3f(1.0f, 0.0f, 0.0f)); // 1
        model.getVertices().add(new Vector3f(0.5f, 1.0f, 0.0f)); // 2
        model.getVertices().add(new Vector3f(0.0f, 0.0f, 1.0f)); // 3
        model.getVertices().add(new Vector3f(1.0f, 0.0f, 1.0f)); // 4
        model.getVertices().add(new Vector3f(0.5f, 1.0f, 1.0f)); // 5

        // Полигоны призмы
        ArrayList<Integer> face1 = new ArrayList<>(Arrays.asList(0, 1, 2)); // Нижний треугольник
        ArrayList<Integer> face2 = new ArrayList<>(Arrays.asList(3, 4, 5)); // Верхний треугольник
        ArrayList<Integer> face3 = new ArrayList<>(Arrays.asList(0, 1, 4, 3)); // Боковая грань 1
        ArrayList<Integer> face4 = new ArrayList<>(Arrays.asList(1, 2, 5, 4)); // Боковая грань 2
        ArrayList<Integer> face5 = new ArrayList<>(Arrays.asList(2, 0, 3, 5)); // Боковая грань 3

        // Добавляем полигоны
        model.getPolygons().add(createPolygon(face1));
        model.getPolygons().add(createPolygon(face2));
        model.getPolygons().add(createPolygon(face3));
        model.getPolygons().add(createPolygon(face4));
        model.getPolygons().add(createPolygon(face5));

        // Рассчитываем нормали
        ArrayList<Vector3f> calculatedNormals = CalculateNormals.calculateNormals(model);

        // Проверка: нормали каждой вершины ненулевые и нормализованы
        for (Vector3f normal : calculatedNormals) {
            assertTrue(normal.length() > 0.99f && normal.length() < 1.01f, "Нормаль не нормализована");
        }
    }

    /**
     * Тест на один треугольник.
     */
    @Test
    void calculateNormalsForSingleTriangle() {
        // Создаем модель
        Model model = new Model();

        // Вершины треугольника
        model.getVertices().add(new Vector3f(0.0f, 0.0f, 0.0f)); // 0
        model.getVertices().add(new Vector3f(1.0f, 0.0f, 0.0f)); // 1
        model.getVertices().add(new Vector3f(0.0f, 1.0f, 0.0f)); // 2

        // Полигон треугольника
        Polygon triangle = new Polygon();
        triangle.setVertexIndices(new ArrayList<>(Arrays.asList(0, 1, 2)));

        // Добавляем полигон в модель
        model.getPolygons().add(triangle);

        // Рассчитываем нормали
        ArrayList<Vector3f> calculatedNormals = CalculateNormals.calculateNormals(model);

        // Ожидаемая нормаль для всех вершин
        Vector3f expectedNormal = new Vector3f(0.0f, 0.0f, 1.0f);

        // Проверяем, что все вершины имеют одинаковую нормаль
        for (Vector3f normal : calculatedNormals) {
            assertEquals(expectedNormal.getX(), normal.getX(), 0.0001);
            assertEquals(expectedNormal.getY(), normal.getY(), 0.0001);
            assertEquals(expectedNormal.getZ(), normal.getZ(), 0.0001);
        }
    }

    /**
     * Утилитарный метод для создания полигона.
     */
    private Polygon createPolygon(ArrayList<Integer> vertexIndices) {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(vertexIndices);
        return polygon;
    }
}
