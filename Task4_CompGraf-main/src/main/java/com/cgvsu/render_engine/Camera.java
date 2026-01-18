package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;

public class Camera {

    private float azimuth = 0.0f;     // Азимут (горизонтальный угол)
    private float elevation = 0.0f;   // Склонение (вертикальный угол)
    private float distance = 100.0f; // Расстояние от камеры до цели

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public void movePosition(final Vector3f translation) {
        this.position.addV(translation);

    }

    public void moveTarget(final Vector3f translation) {
        this.target.addV(target);
    }

    // матрица V
    public Matrix4f getViewMatrix() {
        return GraphicConveyor.lookAt(position, target);
    }

    // P
    public Matrix4f getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    private Vector3f position;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
        updatePositions();
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
        updatePositions();
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
        updatePositions();
    }


    public void updatePosition(){
        updatePositions();
    }

    private void updatePositions() {
        double radAzimuth = Math.toRadians(azimuth);
        double radElevation = Math.toRadians(elevation);

        float x = (float) (distance * Math.cos(radElevation) * Math.sin(radAzimuth));
        float y = (float) (distance * Math.sin(radElevation));
        float z = (float) (distance * Math.cos(radElevation) * Math.cos(radAzimuth));

        position = new Vector3f(x, y, z).sub(target);

    }

    public void setAzimuthAndElevation() {
        Vector3f direction = position.sub(target); // направление - вычисляем как разницу позиции и target
        direction.normalize();

        float azimuth = (float) Math.toDegrees(Math.atan2(direction.getX(), direction.getZ()));
        if (azimuth < 0) {
            azimuth += 360;
        }
        this.azimuth = azimuth;

        this.elevation = (float) Math.toDegrees(Math.asin(direction.getY()));

        // Вычислить расстояние (distance)
        this.distance = position.sub(target).getLength();
    }

    public void cameraReset() {
        this.position = new Vector3f(0, 0, 100);
        this.target = new Vector3f(0, 0, 0);
        this.azimuth = 0;
        this.elevation = 0;
        this.distance = 100;
        updatePosition();
    }
}