package com.cgvsu.math.tests.Affine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.render_engine.AffineTransformations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScaleTest {
    @Test
    public void testScale1() {
        Matrix4f identity = new Matrix4f(1);


        Matrix4f result = AffineTransformations.scale(1, 1, 1);
        Assertions.assertEquals(identity, result, "должны совпасть (грубо говоря, меняем главную диагональ)");
    }


    @Test
    public void testScale2() {
        Matrix4f identity = new Matrix4f(new float[]{
                12, 0, 0, 0,
                0, 54, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });

        Matrix4f result = AffineTransformations.scale(12, 54, 1);
        Assertions.assertEquals(identity, result, "должны совпасть (грубо говоря, меняем главную диагональ)");
    }


}
