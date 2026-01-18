package com.cgvsu.model;

import com.cgvsu.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class NormalCalculator {

    public static ArrayList<Vector3f> calculateNormals(Model model) {
        ArrayList<Vector3f> normals = new ArrayList<>();
        ArrayList<Vector3f> vertices = model.getVertices();
        ArrayList<Polygon> polygons = model.getPolygons();

        for (int i = 0; i < vertices.size(); i++) {
            normals.add(new Vector3f(0, 0, 0));
        }

        for (Polygon polygon : polygons) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            Vector3f v0 = vertices.get(vertexIndices.get(0));
            Vector3f v1 = vertices.get(vertexIndices.get(1));
            Vector3f v2 = vertices.get(vertexIndices.get(2));

            Vector3f edge1 = v1.sub(v0);
            Vector3f edge2 = v2.sub(v0);
            Vector3f faceNormal = edge1.cross(edge2);
            faceNormal.normalize();

            for (int index : vertexIndices) {
                Vector3f currentNormal = normals.get(index);
                normals.set(index, currentNormal.add(faceNormal));
            }
        }

        for (int i = 0; i < normals.size(); i++) {
            Vector3f normal = normals.get(i);
            normal.normalize();
            normals.set(i, normal);
        }

        return normals;
    }
}
