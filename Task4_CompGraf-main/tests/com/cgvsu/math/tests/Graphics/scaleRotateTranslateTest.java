package com.cgvsu.math.tests.Graphics;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.AffineTransformations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class scaleRotateTranslateTest {
    @Test
    public void test1() {
        Vector3f rotate = new Vector3f(0.0f, 0.0f, 0.0f);    // Без вращения
        Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);     // Без масштабирования
        Vector3f translate = new Vector3f(0.0f, 0.0f, 0.0f); // Без трансляции

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);

        // Ожидаемая единичная матрица
        Matrix4f identity = new Matrix4f(1);

        assertMatrixEquals(resultMatrix, identity);

    }

    @Test
    public void test2() {
        Vector3f rotate = new Vector3f(0.0f, 0.0f, 0.0f);     // Без вращения
        Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);      // Без масштабирования
        Vector3f translate = new Vector3f(5.0f, -3.0f, 2.0f); // смещение

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);

        // Ожидаемая матрица смещения
        Matrix4f translationMatrix = new Matrix4f(new float[]{
                1, 0, 0, 5,
                0, 1, 0, -3,
                0, 0, 1, 2,
                0, 0, 0, 1
        });

        assertMatrixEquals(resultMatrix, translationMatrix);


    }

    private void assertVectorEquals(Vector3f actual, Vector3f expected) {
        // float delta = 1e-5f;
        Assertions.assertTrue(actual.equals(expected),
                "Вектор отличается от ожидаемого. Ожидалось: " + expected + ", Получено: " + actual);
    }

    @Test
    public void test3() {
        Vector3f rotate = new Vector3f(0.0f, 0.0f, 0.0f);    // Без вращения
        Vector3f scale = new Vector3f(2.0f, 3.0f, 4.0f);     // Масштабирование
        Vector3f translate = new Vector3f(0.0f, 0.0f, 0.0f); // Без смещения

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);

        // Ожидаемая матрица масштабирования
        Matrix4f scalingMatrix = new Matrix4f(new float[]{
                2, 0, 0, 0,
                0, 3, 0, 0,
                0, 0, 4, 0,
                0, 0, 0, 1
        });


        assertMatrixEquals(resultMatrix, scalingMatrix);
    }


    private void assertMatrixEquals(Matrix4f actual, Matrix4f expected) {
        float delta = 1e-5f;
        float[][] actualElements = actual.getElements();
        float[][] expectedElements = expected.getElements();

        Assertions.assertEquals(expectedElements.length, actualElements.length, "Размеры матриц не совпадают");

        for (int i = 0; i < expectedElements[0].length; i++) {
            for (int j = 0; j < expectedElements.length; j++) {
                Assertions.assertEquals(expectedElements[i][j], actualElements[i][j], delta,
                        "Элемент [" + (i / 4) + "][" + (j % 4) + "] матрицы отличается. Ожидалось: "
                                + expectedElements[i][j] + ", Получено: " + actualElements[i][j]);
            }
        }
    }

    @Test
            //todo БЛЯТЬ
    public void test4() {
        // Вращение на 90 градусов вокруг каждой оси
        Vector3f rotate = new Vector3f(
                (float) 90,
                (float) 90,
                (float) (90)
        );
        Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);     // Без масштабирования
        Vector3f translate = new Vector3f(0.0f, 0.0f, 0.0f); // Без смещения

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);

        float[] expectedElements = new float[]{
                0, 0, 1, 0,
                0, 1, 0, 0,
                -1, 0, 0, 0,
                0, 0, 0, 1
        };

        Matrix4f expectedMatrix = new Matrix4f(expectedElements);

        assertMatrixEquals(resultMatrix, expectedMatrix);
    }

    @Test
    public void test5() {
        Vector3f rotate = new Vector3f(
                (float) Math.toRadians((float) (45)),
                (float) Math.toRadians((float) (45)),
                (float) Math.toRadians((float) (45))
        );

        Vector3f r2 = new Vector3f(
                (float) (45),
                (float) (45),
                (float) (45)
        );

        Vector3f scale = new Vector3f(2.0f, 3.0f, 4.0f);      // Масштабирование
        Vector3f translate = new Vector3f(5.0f, -3.0f, 2.0f); // Трансляция

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(r2, scale, translate);


        float cosX = (float) Math.cos(rotate.getX());
        float sinX = (float) Math.sin(rotate.getX());
        float cosY = (float) Math.cos(rotate.getY());
        float sinY = (float) Math.sin(rotate.getY());
        float cosZ = (float) Math.cos(rotate.getZ());
        float sinZ = (float) Math.sin(rotate.getZ());

        Matrix4f Rx = new Matrix4f(new float[]{
                1, 0, 0, 0,
                0, cosX, sinX, 0,
                0, -sinX, cosX, 0,
                0, 0, 0, 1
        });

        Matrix4f Ry = new Matrix4f(new float[]{
                cosY, 0, sinY, 0,
                0, 1, 0, 0,
                -sinY, 0, cosY, 0,
                0, 0, 0, 1
        });

        Matrix4f Rz = new Matrix4f(new float[]{
                cosZ, sinZ, 0, 0,
                -sinZ, cosZ, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });

        // Общая матрица вращения: R = Rz * Ry * Rx
        Matrix4f R = new Matrix4f(Rz);
        R.multiply(Ry);
        R.multiply(Rx);

        // Матрица масштабирования
        Matrix4f S = new Matrix4f(new float[]{
                scale.getX(), 0, 0, 0,
                0, scale.getY(), 0, 0,
                0, 0, scale.getZ(), 0,
                0, 0, 0, 1
        });

        // Матрица трансляции
        Matrix4f T = new Matrix4f(new float[]{
                1, 0, 0, translate.getX(),
                0, 1, 0, translate.getY(),
                0, 0, 1, translate.getZ(),
                0, 0, 0, 1
        });

        // Ожидаемая комбинированная матрица: T * R * S
        Matrix4f expectedMatrix = new Matrix4f(T);
        expectedMatrix.multiply(R);
        expectedMatrix.multiply(S);

        assertMatrixEquals(resultMatrix, expectedMatrix);

        Vector3f originalVector = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f transformedVector = GraphicConveyor.multiplyMatrix4ByVector3(resultMatrix, originalVector);

        Vector3f expectedVector = GraphicConveyor.multiplyMatrix4ByVector3(expectedMatrix, originalVector);

        assertVectorEquals(transformedVector, expectedVector);
    }

    @Test
    public void test0() {
        Vector3f rotate = new Vector3f(
                (float) (45),
                (float) (45),
                (float) (45)
        );
        Vector3f scale = new Vector3f(0.0f, 0.0f, 0.0f); // Масштабирование до нуля
        Vector3f translate = new Vector3f(1.0f, 2.0f, 3.0f);

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);

        // Ожидаемая матрица: T * R * S = T * R * 0 = матрица трансляции с верхней левой 3x3 частью заполненной нулями
        // я хз, что я тут должен увидеть, если перемножать S * R * T = 0 * R * T = 0
        Matrix4f expectedMatrix = new Matrix4f(new float[]{
                0, 0, 0, 1,
                0, 0, 0, 2,
                0, 0, 0, 3,
                0, 0, 0, 1
        });

        assertMatrixEquals(resultMatrix, expectedMatrix);
    }

    // масштабирование в минус
    @Test
    public void test6() {
        Vector3f rotate = new Vector3f(0.0f, 0.0f, 0.0f);      // Без вращения
        Vector3f scale = new Vector3f(-1.0f, -2.0f, -3.0f);   // Отрицательное масштабирование
        Vector3f translate = new Vector3f(0.0f, 0.0f, 0.0f);   // Без смещения

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);

        Matrix4f expectedMatrix = new Matrix4f(new float[]{
                -1, 0, 0, 0,
                0, -2, 0, 0,
                0, 0, -3, 0,
                0, 0, 0, 1
        });

        assertMatrixEquals(resultMatrix, expectedMatrix);
    }

    @Test
    public void testTransformOrder() {
        // Параметры преобразований
        Vector3f rotate = new Vector3f(
                (float) (30),
                (float) (45),
                (float) (60)
        );
        Vector3f scale = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f translate = new Vector3f(4.0f, 5.0f, 6.0f);

        // Получаем результирующую матрицу
        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);

        // Проверяем порядок преобразований: T * R * S
        // Проверяем порядок преобразований: S * R * T У меня.... и вот опять, я в душе не ебу, как првыильно

        Matrix4f S = AffineTransformations.scale(scale.getX(), scale.getY(), scale.getZ());
        Matrix4f R = AffineTransformations.rotate(rotate.getX(), rotate.getY(), rotate.getZ());

        // Матрица трансляции
        Matrix4f T = AffineTransformations.translate(translate);

        // Ожидаемая матрица: T * R * S
        // Ожидаемая матрица: S * R * T
        Matrix4f expectedMatrix = new Matrix4f(T);
        expectedMatrix.multiply(R);
        expectedMatrix.multiply(S);

        assertMatrixEquals(resultMatrix, expectedMatrix);

        // Дополнительно, проверим применение матрицы к вектору
        Vector3f originalVector = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f expectedTransformedVector = GraphicConveyor.multiplyMatrix4ByVector3(expectedMatrix, originalVector);
        Vector3f actualTransformedVector = GraphicConveyor.multiplyMatrix4ByVector3(resultMatrix, originalVector);

        assertVectorEquals(actualTransformedVector, expectedTransformedVector);
    }

    @Test
    public void test7() {
        Vector3f rotate = new Vector3f(0.0f, 0.0f, 0.0f);     // Без вращения
        Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);      // Без масштабирования
        Vector3f translate = new Vector3f(0.0f, 0.0f, 0.0f); // Без смещения

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);

        Matrix4f identity = new Matrix4f(1);

        assertMatrixEquals(resultMatrix, identity);
    }

    @Test
    public void testLast() {
        Vector3f rotate = new Vector3f(
                (float) (360),
                (float) (360),
                (float) (360)
        ); // Вращение на 360 градусов вокруг всех осей (должно вернуть единичную матрицу для вращения)
        Vector3f scale = new Vector3f(1000.0f, 1000.0f, 1000.0f); // Большое масштабирование
        Vector3f translate = new Vector3f(1000.0f, 1000.0f, 1000.0f); // большое смещение

        Matrix4f resultMatrix = GraphicConveyor.scaleRotateTranslate(rotate, scale, translate);


        Matrix4f S = AffineTransformations.scale(scale.getX(), scale.getY(), scale.getZ());
        Matrix4f R = AffineTransformations.rotate(rotate.getX(), rotate.getY(), rotate.getZ());

        // Матрица трансляции
        Matrix4f T = AffineTransformations.translate(translate);

        // Ожидаемая матрица: T * R * S
        Matrix4f expectedMatrix = new Matrix4f(T);
        expectedMatrix.multiply(R);
        expectedMatrix.multiply(S);


        assertMatrixEquals(resultMatrix, expectedMatrix);
    }

}
