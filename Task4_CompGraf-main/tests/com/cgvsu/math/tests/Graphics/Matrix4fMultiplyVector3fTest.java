package com.cgvsu.math.tests.Graphics;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Matrix4fMultiplyVector3fTest {
    @Test
    public void testMultiplyIdentityMatrix() {
        Matrix4f identity = new Matrix4f(new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
        Vector3f vertex = new Vector3f(3.0f, -2.0f, 5.0f);
        Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(identity, vertex);
        Assertions.assertEquals(vertex, result, "Умножение единичной матрицы должно вернуть исходный вектор");
    }


    @Test
    public void testMultiplyZeroMatrix() {
        Matrix4f zeroMatrix = new Matrix4f();
        zeroMatrix = new Matrix4f(new float[]{
                0, 0, 0, 1,
                0, 0, 0, 2,
                0, 0, 0, 3,
                0, 0, 0, 1  // Столбец 3 (смещение)
        });
        Vector3f vertex = new Vector3f(4.0f, -1.0f, 2.0f);

        Vector3f expected = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(zeroMatrix, vertex);
        Assertions.assertEquals(expected, result, "Умножение нулевой матрицы с смещением должно вернуть смещение");
    }

    /**
     * Тестирование умножения произвольной матрицы на вектор.
     */
    @Test
    public void testMultiplyArbitraryMatrix() {
        Matrix4f matrix = new Matrix4f(new float[]{
                2, 0, 0, 0,
                0, 3, 0, 0,
                0, 0, 4, 0,
                1, 2, 3, 1

        });
        Vector3f vertex = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f expected = new Vector3f(2.0f / 7.0f, 3.0f / 7.0f, 4.0f / 7.0f);
        Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(matrix, vertex);
        Assertions.assertEquals(expected, result, "Умножение произвольной матрицы на вектор выполнено неверно");
    }

    /**
     * Тестирование умножения матрицы с w != 1.
     */
    @Test
    public void testMultiplyMatrixWithWNotOne() {
        Matrix4f matrix = new Matrix4f(new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 2
        });
        Vector3f vertex = new Vector3f(2.0f, 4.0f, 6.0f);

        Vector3f expected = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(matrix, vertex);
        Assertions.assertEquals(expected, result, "Умножение матрицы с w != 1 выполнено неверно");
    }


    @Test
    public void testMultiplyMatrixWithFloatingPoints() {
        Matrix4f matrix = new Matrix4f(new float[]{
                1.5f, 0.0f, 0.0f, 2.0f,
                0.0f, 2.5f, 0.0f, -1.0f,
                0.0f, 0.0f, 3.5f, 0.5f,
                0.0f, 0.0f, 0.0f, 1.0f

        });

        matrix.transposition();
        Vector3f vertex = new Vector3f(2.0f, -1.0f, 4.0f);

        Vector3f expected = new Vector3f(0.375f, -0.3125f, 1.75f);
        Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(matrix, vertex);
        Assertions.assertEquals(expected, result, "Умножение матрицы с плавающими точками выполнено неверно");
    }

    /**
     * Тестирование умножения матрицы, представляющей масштабирование, на вектор.
     */
    @Test
    public void testMultiplyScalingMatrix() {
        // Матрица масштабирования: масштабируем x в 2 раза, y в 3 раза, z в 4 раза
        Matrix4f scalingMatrix = new Matrix4f(new float[]{
                2, 0, 0, 0,
                0, 3, 0, 0,
                0, 0, 4, 0,
                0, 0, 0, 1
        });
        Vector3f vertex = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f expected = new Vector3f(2.0f, 3.0f, 4.0f);
        Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(scalingMatrix, vertex);
        float delta = 1e-6f;

        // Проверяем каждую компоненту вектора отдельно
        Assertions.assertEquals(expected.getX(), result.getX(), delta, "Несовпадение компоненты X");
        Assertions.assertEquals(expected.getY(), result.getY(), delta, "Несовпадение компоненты Y");
        Assertions.assertEquals(expected.getZ(), result.getZ(), delta, "Несовпадение компоненты Z");
    }
}