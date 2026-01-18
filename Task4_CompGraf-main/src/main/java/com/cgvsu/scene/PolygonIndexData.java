package com.cgvsu.scene;

import com.cgvsu.model.Model;

// Класс предназначенный для переноса данных о паре {модель}, {индекс полигона в модели}
public class PolygonIndexData {
    private final Model model;
    private final int polygonIndex;

    public PolygonIndexData(int polygonIndex, Model model) {
        this.polygonIndex = polygonIndex;
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public int getPolygonIndex() {
        return polygonIndex;
    }
}