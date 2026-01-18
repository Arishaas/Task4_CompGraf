package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
//import com.cgvsu.math.Quaternion;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector4f;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class AffineTransformations {

    //По тзшке вектора столбцы -> умножаем справа.
    //прокси метод а то поди кватернионы бахну.
    public static Matrix4f rotate(float alpha, float beta, float gamma) {
        //todo понять, что с кватернионами не так - где-то не так умножение кватернионов... фак
        return rotateIntoMatrix(alpha, beta, gamma);
    }
    /**
     * Создает матрицу поворота объекта вокруг осей X, Y и Z.
     *
     * @param alpha Угол поворота вокруг оси X (в градусах).
     * @param beta  Угол поворота вокруг оси Y (в градусах).
     * @param gamma Угол поворота вокруг оси Z (в градусах).
     * @return {@code Matrix4f} - матрица поворота.
     */
    private static Matrix4f rotateIntoMatrix(float alpha, float beta, float gamma) {
        // Крутим вокруг x
        float[] rotateX = new float[]{
                1, 0, 0, 0,
                0, (float) cos(Math.toRadians(alpha)), (float) sin(Math.toRadians(alpha)), 0,
                0, (float) -sin(Math.toRadians(alpha)), (float) cos(Math.toRadians(alpha)), 0,
                0, 0, 0, 1
        };
        // Крутим вокруг y
        float[] rotateY = new float[]{
                (float) cos(Math.toRadians(beta)), 0, (float) sin(Math.toRadians(beta)), 0,
                0, 1, 0, 0,
                (float) -sin(Math.toRadians(beta)), 0, (float) cos(Math.toRadians(beta)), 0,
                0, 0, 0, 1
        };
        // Крутим вокруг Z
        float[] rotateZ = new float[]{
                (float) cos(Math.toRadians(gamma)), (float) sin(Math.toRadians(gamma)), 0, 0,
                (float) -sin(Math.toRadians(gamma)), (float) cos(Math.toRadians(gamma)), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };

        Matrix4f rotateAroundX = new Matrix4f(rotateX);
        Matrix4f rotateAroundY = new Matrix4f(rotateY);
        Matrix4f rotateAroundZ = new Matrix4f(rotateZ);

        //Крутим X->Y->Z получается умножаем наоборот Z -> Y -> X
        rotateAroundZ.multiply(rotateAroundY); //Z * Y
        rotateAroundZ.multiply(rotateAroundX); //Y * X = Z*Y*X

        return rotateAroundZ;
    }

    /**
     * Прокси метод создающий 4мерный вектор, чтобы передать дальше
     *
     * @param vector3f Вектор переноса по осям X, Y и Z.
     * @return {@code Matrix4f} - матрица переноса.
     */
    public static Matrix4f translate(Vector3f vector3f) {
        return calculateTranslate(new Vector4f(vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1));
    }


    /**
     * Метод для вычисления матрицы переноса.
     *
     * @param vector Вектор переноса (4-мерный).
     * @return {@code Matrix4f} - матрица переноса.
     */
    private static Matrix4f calculateTranslate(Vector4f vector) {
        Matrix4f res = new Matrix4f(1);
        for (int i = 0; i < res.getElements().length - 1; i++) {
            res.setElement(i, 3, vector.getNum(i));
        }
        return res;
    }


    /**
     * Создает матрицу масштабирования объекта.
     *
     * @param scaleX Коэффициент масштабирования по оси X.
     * @param scaleY Коэффициент масштабирования по оси Y.
     * @param scaleZ Коэффициент масштабирования по оси Z.
     * @return {@code Matrix4f} - матрица масштабирования.
     */
    public static Matrix4f scale(float scaleX, float scaleY, float scaleZ) {
        float[] scale = new float[]{scaleX, scaleY, scaleZ};
        return calculateScale(scale);
    }

    /**
     * Вспомогательный метод для вычисления матрицы масштабирования.
     *
     * @param scale Массив коэффициентов масштабирования по осям X, Y и Z.
     * @return {@code Matrix4f} - матрица масштабирования.
     */
    private static Matrix4f calculateScale(float[] scale) {
        Matrix4f result = new Matrix4f(1);
        for (int i = 0; i < scale.length; i++) {
            result.setElement(i, i, scale[i]);
        }
        return result;
    }
}