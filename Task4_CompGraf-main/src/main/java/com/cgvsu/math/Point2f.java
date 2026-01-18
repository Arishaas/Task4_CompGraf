package com.cgvsu.math;

import javafx.beans.NamedArg;

public class Point2f {
    private float x;
    private float y;
    private float u;
    private float v;

    public Point2f(@NamedArg("x") float x, @NamedArg("y") float y) {
        this.x = x;
        this.y = y;
    }
    public void Point2f1(@NamedArg("u") float u, @NamedArg("v") float v) {
        this.u = u;
        this.v = v;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
    public float getU() {
        return u;
    }

    public void setU(float u) {
        this.u = u;
    }
    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }


    public double distance(double x, double y) {
        double distanceForX = this.getX() - x;
        double distanceForY = this.getY() - y;
        return Math.sqrt(distanceForX * distanceForX + distanceForY * distanceForY);
    }
}
