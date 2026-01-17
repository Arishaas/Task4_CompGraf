package core;

import math.Matrix4f;
import math.Transform;

/**
 * Одна 3D‑модель в сцене.
 * Геометрия (mesh) и работа с OBJ находятся в другом модуле/у другого студента.
 */
public class Model3D {

    private final Transform transform = new Transform();
    private Object mesh; // сюда подставь тип mesh из ObjReader

    private String name;

    public Model3D(String name, Object mesh) {
        this.name = name;
        this.mesh = mesh;
    }

    public Transform getTransform() {
        return transform;
    }

    public Matrix4f getModelMatrix() {
        return transform.getModelMatrix();
    }

    // Трансформации удобными методами
    public void setPosition(float x, float y, float z) {
        transform.position.x = x;
        transform.position.y = y;
        transform.position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        transform.rotation.x = x;
        transform.rotation.y = y;
        transform.rotation.z = z;
    }

    public void setScale(float x, float y, float z) {
        transform.scale.x = x;
        transform.scale.y = y;
        transform.scale.z = z;
    }

    public Object getMesh() {
        return mesh;
    }

    public void setMesh(Object mesh) {
        this.mesh = mesh;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    @Override
    public String toString() {
        return name;
    }
}
