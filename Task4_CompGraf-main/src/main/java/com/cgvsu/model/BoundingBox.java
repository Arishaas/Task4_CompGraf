package com.cgvsu.model;

import com.cgvsu.math.Point2f;
import com.cgvsu.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

// Возвращает прямоугольник в который входит треугольник, по крайним координатам
public class BoundingBox {
        private float minX;
        private float minY;
        private float maxX;
        private float maxY;

        public BoundingBox(float minX, float minY, float maxX, float maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public float getMinX() {
            return minX;
        }

        public float getMinY() {
            return minY;
        }

        public float getMaxX() {
            return maxX;
        }

        public float getMaxY() {
            return maxY;
        }


    public static BoundingBox getBoundingBox(ArrayList<Point2f> vertices) {
        if (vertices == null || vertices.isEmpty()) {
            throw new IllegalArgumentException("Vertices list cannot be null or empty");
        }

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Point2f vertex : vertices) {
            if (vertex.getX() < minX) {
                minX = vertex.getX();
            }
            if (vertex.getX() > maxX) {
                maxX = vertex.getX();
            }
            if (vertex.getY() < minY) {
                minY = vertex.getY();
            }
            if (vertex.getY() > maxY) {
                maxY = vertex.getY();
            }
        }

        return new BoundingBox(minX, minY, maxX, maxY);
    }
}