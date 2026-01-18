package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import javafx.scene.paint.Color;

public class RenderParameters {

    private boolean enableColorPolygon = false;
    private boolean enableAllColorPolygon = false;
    private boolean enableTrianglePolygon = false;
    private boolean enableTexturePolygon = false;
    private boolean enablePolygonalGrid = false;

    private Color polygonalGridColor = Color.BLACK;
    private Color defaultFillColor = Color.LIGHTGREY;
    private float lightingCoefficient = 1.6f;

    public RenderParameters() {}

    public boolean getEmptyParams() {
        if (!this.enableTrianglePolygon &&
                !this.enableColorPolygon &&
                !this.enableTexturePolygon &&
                !this.enableAllColorPolygon
        ) {
            return true;
        }else{
            return false;
        }
    }
    public void setAllParams(boolean triangle, boolean color, boolean texture, boolean allcolor){
        this.enableTrianglePolygon = triangle;
        this.enableColorPolygon = color;
        this.enableTexturePolygon = texture;
        this.enableAllColorPolygon = allcolor;
    }

    public boolean isEnablePolygonalGrid() {
        return enablePolygonalGrid;
    }

    public void setEnablePolygonalGrid(boolean enablePolygonalGrid) {
        this.enablePolygonalGrid = enablePolygonalGrid;
    }

    public void setTexturePolygon(boolean enableTexturePolygon) {
        this.enableTexturePolygon = enableTexturePolygon;
    }
    public boolean isTexturePolygon() {
        return enableTexturePolygon;
    }

    public void setColorPolygon(boolean enableColorPolygon) {
        this.enableColorPolygon = enableColorPolygon;
    }
    public boolean isColorPolygon() {
        return enableColorPolygon;
    }
    public void setAllColorPolygon(boolean enableAllColorPolygon) {
        this.enableAllColorPolygon = enableAllColorPolygon;
    }
    public boolean isAllColorPolygon() {
        return enableAllColorPolygon;
    }
    public void setTrianglePolygon(boolean enableTrianglePolygon) {
        this.enableTrianglePolygon = enableTrianglePolygon;
    }
    public boolean isTrianglePolygon() {
        return enableTrianglePolygon;
    }
    public Color getPolygonalGridColor() {
        return polygonalGridColor;
    }

    public void setPolygonalGridColor(Color polygonalGridColor) {
        this.polygonalGridColor = polygonalGridColor;
    }

    public float getLightingCoefficient() {
        return lightingCoefficient;
    }

    public void setLightingCoefficient(float lightingCoefficient) {
        this.lightingCoefficient = lightingCoefficient;
    }


    public Color getDefaultFillColor() {
        return defaultFillColor;
    }

    public void setDefaultFillColor(Color defaultFillColor) {
        this.defaultFillColor = defaultFillColor;
    }
}
