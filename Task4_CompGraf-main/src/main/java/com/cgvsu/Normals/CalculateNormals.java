package com.cgvsu.Normals;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для расчёта нормалей вершин модели.
 *
 * Этот метод позволяет рассчитать нормали вершин на основе нормалей граней модели,
 * создавая плавные переходы между полигонами. Процесс включает три шага:
 *
 * 1. Инициализация нормалей вершин.
 * 2. Вычисление нормалей граней (cross-продукт ребер грани).
 * 3. Суммирование нормалей граней к вершинам, принадлежащим этим граням.
 * 4. Нормализация всех вершинных нормалей для корректного результата.
 */
public class CalculateNormals {

    /**
     * Метод для расчёта нормалей вершин модели.
     *
     * @param model модель, содержащая вершины и полигоны
     * @return список нормалей для каждой вершины
     */
    public static ArrayList<Vector3f> calculateNormals(Model model) {
        // Инициализация нормалей
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Vector3f> vertices = model.getVertices();
        ArrayList<Polygon> polygons = model.getPolygons();

        for (int i = 0; i < vertices.size(); i++) {
            normals.add(new Vector3f(0, 0, 0));
        }

        // Расчёт нормалей граней и добавление их к нормалям вершин
        for (Polygon polygon : polygons) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            Vector3f v0 = vertices.get(vertexIndices.get(0));
            Vector3f v1 = vertices.get(vertexIndices.get(1));
            Vector3f v2 = vertices.get(vertexIndices.get(2));

            // Вычисление нормали грани
            Vector3f edge1 = v1.sub(v0);
            Vector3f edge2 = v2.sub(v0);
            Vector3f faceNormal = edge1.cross(edge2);
            faceNormal.normalize();

            // Добавление нормали грани к вершинам
            for (int index : vertexIndices) {
                Vector3f currentNormal = normals.get(index);
                normals.set(index, currentNormal.add(faceNormal));
            }
        }

        // Нормализация всех нормалей вершин
        for (int i = 0; i < normals.size(); i++) {
            Vector3f normal = normals.get(i);
            normal.normalize();
            normals.set(i, normal);
        }

        return normals;
    }
}
