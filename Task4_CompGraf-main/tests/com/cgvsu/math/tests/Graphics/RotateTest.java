package com.cgvsu.math.tests.Graphics;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.AffineTransformations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RotateTest {
    @Test
    public void testIdentityRotation() {
        float alpha = 0.0f;
        float beta = 0.0f;
        float gamma = 0.0f;

        Matrix4f rotationMatrix = AffineTransformations.rotate(alpha, beta, gamma);

        Matrix4f identity = new Matrix4f(1);


        Assertions.assertEquals(identity, rotationMatrix, "Поворот с нулевыми углами должен вернуть единичную матрицу");
    }

    @Test
    public void testRotateX() {
        float alpha = (float) 90;
        float beta = 0.0f;
        float gamma = 0.0f;

        Matrix4f rotationMatrix = AffineTransformations.rotate(alpha, beta, gamma);

        Vector3f originalVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f expectedVector = new Vector3f(0.0f, 0.0f, -1.0f);

        Vector3f rotatedVector = GraphicConveyor.multiplyMatrix4ByVector3(rotationMatrix, originalVector);

        float delta = 1e-6f;

        // Проверяем каждую компоненту вектора отдельно
        Assertions.assertEquals(expectedVector.getX(), rotatedVector.getX(), delta, "Несовпадение компоненты X");
        Assertions.assertEquals(expectedVector.getY(), rotatedVector.getY(), delta, "Несовпадение компоненты Y");
        Assertions.assertEquals(expectedVector.getZ(), rotatedVector.getZ(), delta, "Несовпадение компоненты Z");
    }

    @Test
    public void testRotateY() {
        float alpha = 0;
        float beta = (float) 90;
        float gamma = 0.0f;

        Matrix4f rotationMatrix = AffineTransformations.rotate(alpha, beta, gamma);

        Vector3f originalVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f expectedVector = new Vector3f(0.0f, 1.0f, 0.0f);

        Vector3f rotatedVector = GraphicConveyor.multiplyMatrix4ByVector3(rotationMatrix, originalVector);

        float delta = 1e-6f;

        // Проверяем каждую компоненту вектора отдельно
        Assertions.assertEquals(expectedVector.getX(), rotatedVector.getX(), delta, "Несовпадение компоненты X");
        Assertions.assertEquals(expectedVector.getY(), rotatedVector.getY(), delta, "Несовпадение компоненты Y");
        Assertions.assertEquals(expectedVector.getZ(), rotatedVector.getZ(), delta, "Несовпадение компоненты Z");
    }

    @Test
    public void testRotateZ() {
        float alpha = 0;
        float beta = 0;
        float gamma = (float) 90;

        Matrix4f rotationMatrix = AffineTransformations.rotate(alpha, beta, gamma);

        Vector3f originalVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f expectedVector = new Vector3f(1.0f, 0.0f, 0.0f);

        Vector3f rotatedVector = GraphicConveyor.multiplyMatrix4ByVector3(rotationMatrix, originalVector);

        float delta = 1e-6f;

        // Проверяем каждую компоненту вектора отдельно
        Assertions.assertEquals(expectedVector.getX(), rotatedVector.getX(), delta, "Несовпадение компоненты X");
        Assertions.assertEquals(expectedVector.getY(), rotatedVector.getY(), delta, "Несовпадение компоненты Y");
        Assertions.assertEquals(expectedVector.getZ(), rotatedVector.getZ(), delta, "Несовпадение компоненты Z");
    }

    @Test
    public void testRotateAll() {
        float alpha = (float) 90;
        float beta = (float) 90;
        float gamma = (float) 90;

        Matrix4f rotationMatrix = AffineTransformations.rotate(alpha, beta, gamma);

        Vector3f originalVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f expectedVector = new Vector3f(0.0f, 1.0f, 0.0f);

        Vector3f rotatedVector = GraphicConveyor.multiplyMatrix4ByVector3(rotationMatrix, originalVector);

        float delta = 1e-6f;

        // Проверяем каждую компоненту вектора отдельно
        Assertions.assertEquals(expectedVector.getX(), rotatedVector.getX(), delta, "Несовпадение компоненты X");
        Assertions.assertEquals(expectedVector.getY(), rotatedVector.getY(), delta, "Несовпадение компоненты Y");
        Assertions.assertEquals(expectedVector.getZ(), rotatedVector.getZ(), delta, "Несовпадение компоненты Z");
    }

}
