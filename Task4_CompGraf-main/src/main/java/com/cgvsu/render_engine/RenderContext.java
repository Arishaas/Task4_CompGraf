package com.cgvsu.render_engine;


import com.cgvsu.model.Model;
import com.cgvsu.scene.PolygonIndexData;

public class RenderContext {

    private int width;
    private int height;
    private float[] zBuffer;
    private int[] colorBuffer;
    private final PolygonIndexData[][] polygonZBuffer;
    private final PolygonIndexData chosenPolygonIndexData;

    public RenderContext(int width, int height, float[] zBuffer, int[] colorBuffer,
                         PolygonIndexData[][] polygonZBuffer, PolygonIndexData chosenPolygonIndex) {

        this.width = width;
        this.height = height;
        this.zBuffer = zBuffer;
        this.colorBuffer = colorBuffer;
        this.polygonZBuffer = polygonZBuffer;
        this.chosenPolygonIndexData = chosenPolygonIndex;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float[] getZBuffer() {
        return zBuffer;
    }

    public int[] getColorBuffer() {
        return colorBuffer;
    }


    public void updateZBuffer(int x, int y, float z) {
        int index = y * width + x;
        if (z < zBuffer[index]) {
            zBuffer[index] = z;
        }
    }


    public void updateColorBuffer(int x, int y, int color) {
        int index = y * width + x;
        colorBuffer[index] = color;
    }

    public void setzBuffer(float[] zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void setColorBuffer(int[] colorBuffer) {
        this.colorBuffer = colorBuffer;
    }

    public boolean isChosenAnyPolygon() {
        return chosenPolygonIndexData != null;
    }

    public PolygonIndexData getChosenPolygonIndexData() {
        return chosenPolygonIndexData;
    }
    public void updatePolygonZBuffer(int x, int y, int polygonIndex, Model model) {
        polygonZBuffer[y][x] = new PolygonIndexData(polygonIndex, model);
    }
}
