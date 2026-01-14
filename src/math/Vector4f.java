package math;

public class Vector4f {
    public float x, y, z, w;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector3f v, float w) {
        this(v.x, v.y, v.z, w);
    }

    // Преобразование обратно в 3D
    public Vector3f toVector3() {
        return w == 0 ? new Vector3f(x, y, z)
                : new Vector3f(x / w, y / w, z / w);
    }

    // Удобные методы
    public Vector4f add(Vector4f o) { return new Vector4f(x + o.x, y + o.y, z + o.z, w + o.w); }
    public Vector4f mul(float s) { return new Vector4f(x * s, y * s, z * s, w * s); }
}
