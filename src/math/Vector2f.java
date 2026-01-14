package math;

public class Vector2f {
    public float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Дополнительные методы для удобства
    public Vector2f add(Vector2f o) { return new Vector2f(x + o.x, y + o.y); }
    public Vector2f sub(Vector2f o) { return new Vector2f(x - o.x, y - o.y); }
    public Vector2f mul(float s) { return new Vector2f(x * s, y * s); }
    public float dot(Vector2f o) { return x * o.x + y * o.y; }
    public Vector2f copy() { return new Vector2f(x, y); }
}
