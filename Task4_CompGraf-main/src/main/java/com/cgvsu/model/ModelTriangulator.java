package com.cgvsu.model;

import com.cgvsu.math.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class ModelTriangulator {
    public static ArrayList<Polygon> triangulatePolygon(Polygon poly) {
        int vertexNum = poly.getVertexIndices().size(); // число точек в исходном полигоне
        ArrayList<Polygon> polygons = new ArrayList<Polygon>(); // список треугольных полигонов

        if (poly.getVertexIndices().size() == 3) { // если передан треугольник, то возвращаем его
            polygons.add(poly);
            return polygons;
        }

        for (int i = 2; i < vertexNum - 1; i++) {
            ArrayList<Integer> vertex = new ArrayList<>(); // список точек в новом треугольнике
            vertex.add(poly.getVertexIndices().get(0));
            vertex.add(poly.getVertexIndices().get(i - 1));
            vertex.add(poly.getVertexIndices().get(i));

            Polygon currPoly = new Polygon(); //
            currPoly.setVertexIndices(vertex);
            polygons.add(currPoly);
        }
        if (vertexNum > 3) { // последний треугольник
            ArrayList<Integer> vertex = new ArrayList<>();
            vertex.add(poly.getVertexIndices().get(0));
            vertex.add(poly.getVertexIndices().get(vertexNum - 2));
            vertex.add(poly.getVertexIndices().get(vertexNum - 1));

            Polygon currPoly = new Polygon();
            currPoly.setVertexIndices(vertex);
            polygons.add(currPoly);
        }
        return polygons;
    }
    public static ArrayList<Polygon> triangulatePolygonColor(Polygon poly) {
        int vertexNum1 = poly.getVertexIndices().size(); // число точек в исходном полигоне
        ArrayList<Polygon> polygons = new ArrayList<Polygon>(); // список треугольных полигонов

        if (poly.getVertexIndices().size() == 3) { // если передан треугольник, то возвращаем его
            polygons.add(poly);
            return polygons;
        }

        for (int i = 2; i < vertexNum1- 1; i++) {
            ArrayList<Integer> vertex = new ArrayList<>(); // список точек в новом треугольнике
            vertex.add(poly.getVertexIndices().get(0));
            vertex.add(poly.getVertexIndices().get(i - 1));
            vertex.add(poly.getVertexIndices().get(i));

            Polygon currPoly = new Polygon(); //
            currPoly.setVertexIndices(vertex);
            polygons.add(currPoly);
        }
        if (vertexNum1 > 3) { // последний треугольник
            ArrayList<Integer> vertex = new ArrayList<>();
            vertex.add(poly.getVertexIndices().get(0));
            vertex.add(poly.getVertexIndices().get(vertexNum1 - 2));
            vertex.add(poly.getVertexIndices().get(vertexNum1 - 1));

            Polygon currPoly = new Polygon();
            currPoly.setVertexIndices(vertex);
            polygons.add(currPoly);
        }
        return polygons;
    }

    public static ArrayList<Polygon> triangulateModel(ArrayList<Polygon> polygons) {
        ArrayList<Polygon> newModelPoly1 = new ArrayList<Polygon>();

        for (int i = 0; i < polygons.size(); i++) {
            newModelPoly1.addAll(
                    triangulatePolygon(polygons.get(i))
            );
        }
        return newModelPoly1;
    }
    public static ArrayList<Polygon> triangulateModelColor(ArrayList<Polygon> polygons) {
        ArrayList<Polygon> newModelPoly1 = new ArrayList<Polygon>();

        for (int i = 0; i < polygons.size(); i++) {
            newModelPoly1.addAll(
                    triangulatePolygonColor(polygons.get(i))
            );
        }
        return newModelPoly1;
    }

    public static class Triangle {
        public int v1, v2, v3;

        public Triangle(int v1, int v2, int v3) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }

        @Override
        public String toString() {
            return "Triangle{" +
                    "v1=" + v1 +
                    ", v2=" + v2 +
                    ", v3=" + v3 +
                    '}';
        }
    }

    private List<Polygon> polygons;

    public Vector3f getNormal(Vector3f v1, Vector3f v2, Vector3f v3, float[] barycentric, List<Vector3f> vertices) {
        try {
            int index1 = (int) v1.getZ();
            int index2 = (int) v2.getZ();
            int index3 = (int) v3.getZ();

            if (index1 < 0 || index1 >= vertices.size() || index2 < 0 || index2 >= vertices.size() || index3 < 0 || index3 >= vertices.size()) {
                System.err.println("Error: Invalid vertex index in getNormal: index1=" + index1 + ", index2=" + index2 + ", index3=" + index3 +". Vertices size: " + vertices.size());
                return new Vector3f(0, 0, 0);
            }
            Vector3f normal1 = vertices.get(index1);
            Vector3f normal2 = vertices.get(index2);
            Vector3f normal3 = vertices.get(index3);

            Vector3f normal = new Vector3f(
                    normal1.getX() * barycentric[0] + normal2.getX() * barycentric[1] + normal3.getX() * barycentric[2],
                    normal1.getY() * barycentric[0] + normal2.getY() * barycentric[1] + normal3.getY() * barycentric[2],
                    normal1.getZ() * barycentric[0] + normal2.getZ() * barycentric[1] + normal3.getZ() * barycentric[2]).normalizeV();
            System.out.println("Get normal: " + normal + ", from indices: " + index1 + ", " + index2 + ", " + index3);
            return normal;
        } catch (IndexOutOfBoundsException e) {
            System.err.println("IndexOutOfBoundsException in getNormal: v1=" + v1 + ", v2=" + v2 + ", v3=" + v3);
            return new Vector3f(0, 0, 0);
        }
    }

    // Метод для получения треугольников (после триангуляции)
    public ArrayList<Polygon> getTriangles() {
        Polygon pp = new Polygon();
        ArrayList<Integer> vertexIndices = new ArrayList<>();
        ArrayList<Polygon> triangles = new ArrayList<>();
        for (Polygon polygon : polygons) {
            if (polygon.getVertexIndices().size() == 3){
                // triangles.add(new Triangle(polygon.getVertexIndices().get(0), polygon.getVertexIndices().get(1), polygon.getVertexIndices().get(2)));
                vertexIndices.clear();
                vertexIndices.add(polygon.getVertexIndices().get(0));
                vertexIndices.add(polygon.getVertexIndices().get(1));
                vertexIndices.add(polygon.getVertexIndices().get(2));

                pp.setVertexIndices(vertexIndices);
                triangles.add(pp);
        }
        }
        System.out.println("Returning " + triangles.size() + " triangles.");
        return triangles;
    }

    public static void setModelTriangulatorColor(Model model) {
        System.out.println("ModelTriangulatorColor");
        ArrayList<Polygon> polygons = model.getPolygons();
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            float[] centroid = Rasterizer.getCentroid(polygon, model.getVertices());
            polygon.setPosition(centroid[0], centroid[1]);
        }
        model.setOriginalPolygons(model.getPolygons());
        model.setTriangulatePolygons(ModelTriangulator.triangulateModelColor(polygons));
        model.setPolygons(model.getTriangulatePolygons());
    }

    public static void setModelTriangulator(Model model) {
        System.out.println("ModelTriangulator");
        ArrayList<Polygon> polygons1 = model.getPolygons();
        for (int i = 0; i < polygons1.size(); i++) {
            Polygon polygon = polygons1.get(i);
            float[] centroid = Rasterizer.getCentroid(polygon, model.getVertices());
            polygon.setPosition(centroid[0], centroid[1]);
        }
        model.setOriginalPolygons(model.getPolygons());
        model.setTriangulatePolygons(ModelTriangulator.triangulateModel(polygons1));
        model.setPolygons(model.getTriangulatePolygons());
    }




}


