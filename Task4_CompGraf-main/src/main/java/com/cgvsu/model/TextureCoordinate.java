package com.cgvsu.model;

public class TextureCoordinate {
    private float u;
    private float v;

    public TextureCoordinate(float u, float v) {
        this.u = u;
        this.v = v;
    }

    public float getU() {
        return u;
    }

    public float getV() {
        return v;
    }
}