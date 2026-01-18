package com.cgvsu.math.tests.Affine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.AffineTransformations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TranslateTest {
    @Test
    public void test1() {
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        Vector3f vector3f = new Vector3f(x, y, z);
        Matrix4f rotationMatrix = AffineTransformations.translate(vector3f);

        Matrix4f identity = new Matrix4f(1);

        Assertions.assertEquals(identity, rotationMatrix, "Поворот с нулевыми углами должен вернуть единичную матрицу");

    }

    @Test
    public void test2() {
        float x = 2f;
        float y = 3f;
        float z = 4f;

        Matrix4f identity = new Matrix4f(new float[]{
                1, 0, 0, 2,
                0, 1, 0, 3,
                0, 0, 1, 4,
                0, 0, 0, 1
        });

        Vector3f vector3f = new Vector3f(x, y, z);
        Matrix4f rotationMatrix = AffineTransformations.translate(vector3f);

        Assertions.assertEquals(identity, rotationMatrix, "Поворот с нулевыми углами должен вернуть единичную матрицу");
    }
}
