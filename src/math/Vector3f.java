package math;

public class Vector3f {
    public float x, y, z;
    private static final float EPS = 1e-6f;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Арифметические операции
    public Vector3f add(Vector3f o) { return new Vector3f(x + o.x, y + o.y, z + o.z); }
    public Vector3f sub(Vector3f o) { return new Vector3f(x - o.x, y - o.y, z - o.z); }
    public Vector3f mul(float s) { return new Vector3f(x * s, y * s, z * s); }

    // Скалярное и векторное произведение
    public float dot(Vector3f o) { return x * o.x + y * o.y + z * o.z; }
    public Vector3f cross(Vector3f o) {
        return new Vector3f(
                y * o.z - z * o.y,
                z * o.x - x * o.z,
                x * o.y - y * o.x
        );
    }

    // Длина и нормализация
    public float length() { return (float) Math.sqrt(dot(this)); }
    public Vector3f normalize() {
        float len = length();
        return len < EPS ? new Vector3f(0,0,0) : mul(1.0f / len);
    }

    // Проверка равенства с учетом точности
    public boolean equals(Vector3f o) {
        return Math.abs(x - o.x) < EPS &&
                Math.abs(y - o.y) < EPS &&
                Math.abs(z - o.z) < EPS;
    }

    // Удобные методы для Math-модуля
    public Vector4f toVector4(float w) { return new Vector4f(this, w); }
    public Vector3f copy() { return new Vector3f(x, y, z); }
}
