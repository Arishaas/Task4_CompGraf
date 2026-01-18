package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Point2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector4f;

import static java.lang.Math.*;

public class GraphicConveyor {
    public static Matrix4f rotateScaleTranslate() {
        return new Matrix4f(1);
    }
    /**
     * Создает матрицу преобразования модели (масштаб, вращение, перенос). M
     *
     * @param rotate    Вектор поворота модели по осям X, Y и Z.
     * @param scale     Вектор масштабирования модели по осям X, Y и Z.
     * @param translate Вектор переноса модели по осям X, Y и Z.
     * @return {@code Matrix4f} - матрица преобразования из локальной системы координат в мировую.
     *
     * ПОРЯДОК!!! S->R->T
     */
    public static Matrix4f scaleRotateTranslate(Vector3f rotate, Vector3f scale, Vector3f translate) {
        Matrix4f t = AffineTransformations.translate(translate);
        Matrix4f r = AffineTransformations.rotate(rotate.getX(), rotate.getY(), rotate.getZ());
        Matrix4f s = AffineTransformations.scale(scale.getX(), scale.getY(), scale.getZ());

        // Порядок применения трансформаций: сначала масштаб, затем вращение, затем перенос.
        return t.multiplyNew(r.multiplyNew(s));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    /**
     * Создает матрицу вида камеры, которая переводит объекты сцены в систему координат камеры. V
     *
     * @param eye    Позиция камеры в мировых координатах.
     * @param target Точка, на которую направлен объектив камеры.
     * @param up     Вектор "вверх" для ориентации камеры.
     * @return {@code Matrix4f} - матрица вида камеры.
     */
    private static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultZ = new Vector3f();
        resultZ.sub(target, eye); // Направление от камеры к цели.

        Vector3f resultX = up.cross(resultZ); // Вектор "вправо".
        Vector3f resultY = resultZ.cross(resultX); // Вектор "вверх".

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[] matrix = new float[]{
                resultX.getX(), resultY.getX(), resultZ.getX(), 0,
                resultX.getY(), resultY.getY(), resultZ.getY(), 0,
                resultX.getZ(), resultY.getZ(), resultZ.getZ(), 0,
                -resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1
        };

        return new Matrix4f(matrix).transpositionNew();
    }

    /**
     * Создает матрицу перспективной проекции. P
     *
     * @param fov         Угол обзора камеры в радианах.
     * @param aspectRatio Соотношение сторон экрана (ширина / высота).
     * @param nearPlane   Расстояние до ближней плоскости отсечения.
     * @param farPlane    Расстояние до дальней плоскости отсечения.
     * @return {@code Matrix4f} - матрица перспективной проекции.
     */
    public static Matrix4f perspective(float fov, float aspectRatio, float nearPlane, float farPlane) {
        float tangent = (float) (1.0F / tan(fov * 0.5F));
        float[] res = {
                tangent / aspectRatio, 0, 0, 0,
                0, tangent, 0, 0,
                0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 1.0F,
                0, 0, 2 * (nearPlane * farPlane) / (nearPlane - farPlane), 0
        };
        return new Matrix4f(res).transpositionNew();
    }

    /**
     * Преобразует вершину из локальных координат модели в экранные координаты. ЭТО ОКАЗЫВАЕТСЯ КОСТЫЛЬ ЛОЛ
     *
     * @param matrix Матрица преобразования (P*V*M).
     * @param vertex Координаты вершины в локальной системе модели.
     * @return {@code Vector3f} - нормализованные экранные координаты вершины.
     */
    public static Vector3f multiplyMatrix4ByVector3(Matrix4f matrix, Vector3f vertex) {
        Vector4f res = matrix.multiply(new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1));
        float w = res.getW();
        return new Vector3f(res.getX() / w, res.getY() / w, res.getZ() / w);
    }

    /**
     * Вычисляет матрицу модель-вид-проекция (MVP) для заданной модели и камеры.
     *
     * @param camera    Камера с матрицами вида и проекции.
     * @param rotate    Вектор поворота модели.
     * @param scale     Вектор масштабирования модели.
     * @param translate Вектор переноса модели.
     * @return {@code Matrix4f} - итоговая матрица MVP.
     */
    public static Matrix4f calculateModelViewProjectionMatrix(Camera camera, Vector3f rotate, Vector3f scale, Vector3f translate) {
        Matrix4f modelMatrix = scaleRotateTranslate(rotate, scale, translate);
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();



        //добавил от Паши
        modelMatrix.transposition();
        viewMatrix.transposition();
        projectionMatrix.transposition();
        //


        Matrix4f mvpMatrix = new Matrix4f(projectionMatrix);
        mvpMatrix.multiply(viewMatrix);
        mvpMatrix.multiply(modelMatrix);
       // return mvpMatrix;

        return projectionMatrix.multiplyNew(viewMatrix.multiplyNew(modelMatrix));

    }

    /**
     * Преобразует вершину из координат пространства камеры в экранные координаты.
     *
     * @param vertex Вершина в координатах пространства камеры.
     * @param width  Ширина экрана.
     * @param height Высота экрана.
     * @return {@code Point2f} - координаты точки на экране.
     */
    public static Point2f vertexToPoint(Vector3f vertex, int width, int height) {
        float x = (vertex.getX() + 1) * 0.5f * width;
        float y = (1 - (vertex.getY() + 1) * 0.5f) * height;
        return new Point2f(x, y);
    }
    public static Point2f vertexToPoint1(Vector3f vertex, int width, int height, float u, float v) {
        float x = (vertex.getX() + 1) * 0.5f * width;
        float y = (1 - (vertex.getY() + 1) * 0.5f) * height;
        Point2f screenPoint = new Point2f(x, y);
        screenPoint.Point2f1(u, v);
        return screenPoint;
    }
}
