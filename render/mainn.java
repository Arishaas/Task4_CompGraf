1) RenderMode.java

package renderer;

public enum RenderMode {
    WIREFRAME,          // только сетка (линии)
    TEXTURE,            // только текстура
    LIGHTING,           // только освещение (по нормалям / ламберт)
    TEXTURE_LIGHTING,   // текстура + освещение
    ALL                 // сетка + текстура + освещение + Z-буфер
}


⸻

        2) Camera.java (несколько камер, lookAt + perspective)

package renderer;

import java.util.Objects;

/**
 * Камера: eye/target/up + параметры перспективы.
 * Свет по ТЗ привязан к камере => направление света берем из камеры.
 */
public class Camera {
    public final Vec3 eye;
    public final Vec3 target;
    public final Vec3 up;

    public final float fovYRadians;
    public final float aspect;
    public final float near;
    public final float far;

    public Camera(Vec3 eye, Vec3 target, Vec3 up,
                  float fovYRadians, float aspect, float near, float far) {
        this.eye = Objects.requireNonNull(eye);
        this.target = Objects.requireNonNull(target);
        this.up = Objects.requireNonNull(up);
        this.fovYRadians = fovYRadians;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
    }

    /** Матрица вида (lookAt), правосторонняя система. */
    public Mat4 viewMatrix() {
        Vec3 z = eye.sub(target).normalized();      // forward
        Vec3 x = up.cross(z).normalized();          // right
        Vec3 y = z.cross(x);                        // true up

        // | x.x x.y x.z -dot(x,eye) |
        // | y.x y.y y.z -dot(y,eye) |
        // | z.x z.y z.z -dot(z,eye) |
        // | 0   0   0        1      |
        return new Mat4(new float[]{
                x.x, x.y, x.z, -x.dot(eye),
                y.x, y.y, y.z, -y.dot(eye),
                z.x, z.y, z.z, -z.dot(eye),
                0,   0,   0,    1
        });
    }

    /** Матрица перспективной проекции. */
    public Mat4 projectionMatrix() {
        float f = (float) (1.0 / Math.tan(fovYRadians * 0.5));
        float nf = 1.0f / (near - far);

        // Стандартная perspective (OpenGL-like NDC z в [-1..1], потом мы ремапнем в [0..1]).
        return new Mat4(new float[]{
                f / aspect, 0, 0,                          0,
                0,          f, 0,                          0,
                0,          0, (far + near) * nf,          (2 * far * near) * nf,
                0,          0, -1,                         0
        });
    }

    /** Направление "луча света" из камеры: ray = normalize(target - eye). */
    public Vec3 lightRayDirection() {
        return target.sub(eye).normalized();
    }
}


⸻

        3) RenderEngine.java (Z-buffer + renderModel + barycentric + texture + Lambert)

package renderer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Полный софтварный рендер: треугольники, Z-буфер, текстуры, ламберт, режимы.
 *
 * ВАЖНО: ты подключаешь свои данные через ModelAdapter (см. интерфейс ниже).
 * Это специально, чтобы код работал в любом твоем проекте/структуре.
 */
public class RenderEngine {

    /** Z-буфер: глубина на пиксель. По ТЗ: if(zBuffer(x,y) > z) then draw */
    private float[] zBuffer;
    private int bufferW;
    private int bufferH;
    textureImage текстура (можно null если режима с текстурой нет)
     */
    public void renderModel(ModelAdapter model,
                            Mat4 modelMatrix,
                            Camera camera,
                            WritableImage target,
                            RenderMode mode,
                            Image textureImage) {

        Objects.requireNonNull(model);
        Objects.requireNonNull(modelMatrix);
        Objects.requireNonNull(camera);
        Objects.requireNonNull(target);
        Objects.requireNonNull(mode);

        final int w = (int) target.getWidth();
        final int h = (int) target.getHeight();
        ensureBuffers(w, h);

        PixelWriter pw = target.getPixelWriter();
        PixelReader texReader = (textureImage != null) ? textureImage.getPixelReader() : null;
        final int texW = (textureImage != null) ? (int) textureImage.getWidth() : 0;
        final int texH = (textureImage != null) ? (int) textureImage.getHeight() : 0;

        // MVP = P * V * M
        Mat4 V = camera.viewMatrix();
        Mat4 P = camera.projectionMatrix();
        Mat4 MVP = P.mul(V).mul(modelMatrix);

        // Свет по ТЗ: l = -n·ray, где ray - направление света (из камеры).
        // ray = normalize(target - eye)
        Vec3 ray = camera.lightRayDirection();

        // 1) Триангуляция всех полигонов (если не треугольники)
        //    Делаем fan-triangulation: (v0, vi, vi+1)
        List<int[]> triangles = triangulateAllFaces(model);

        // 2) Пересчет нормалей для вершин (даже если есть в файле)
        //    Нормали считаем в МИРОВОМ/МОДЕЛЬНОМ пространстве (до проекции).
        //    Используем вершины после modelMatrix (без V/P), чтобы нормали совпадали с геометрией в мире.
        Vec3[] worldVerts = new Vec3[model.vertexCount()];
        for (int i = 0; i < model.vertexCount(); i++) {
            Vec4 v = new Vec4(model.getVertex(i), 1.0f);
            Vec4 vw = modelMatrix.mul(v);
            worldVerts[i] = new Vec3(vw.x, vw.y, vw.z);
        }
        Vec3[] vertexNormals = recomputeVertexNormals(model, triangles, worldVerts);

        // 3) Растеризация треугольников с интерполяцией барицентрическими координатами
        for (int[] tri : triangles) {
            int ia = tri[0], ib = tri[1], ic = tri[2];

            // NDC/Screen vertices
            VertexOut A = transformVertex(MVP, model.getVertex(ia), w, h);
            VertexOut B = transformVertex(MVP, model.getVertex(ib), w, h);
            VertexOut C = transformVertex(MVP, model.getVertex(ic), w, h);

            // если треугольник полностью за клипом/сломанный (w==0 etc) — пропускаем
            if (!A.validffer(x,y) > z) t!C.valid) continue;

            // UV (если есть)
            Vec2 uvA = model.hasTexCoords() ? model.getTexCoordForVertex(ia) : new Vec2(0, 0);
            Vec2 uvB = model.hasTexCoords() ? model.getTexCoordForVertex(ib) : new Vec2(0, 0);
            Vec2 uvC = model.hasTexCoords() ? model.getTexCoordForVertex(ic) : new Vec2(0, 0);

            // Нормали (пересчитанные)
            Vec3 nA = vertexNormals[ia];
            Vec3 nB = vertexNormals[ib];
            Vec3 nC = vertexNormals[ic];

            // 5) Режимы отрисовки
            boolean drawWire = (mode == RenderMode.WIREFRAME || mode == RenderMode.ALL);
            boolean drawTex = (mode == RenderMode.TEXTUREenderMode {
                WIREFRAME,          // тольmode == RenderMode.ALL);
                boolean drawLight = (mode == RenderMode.LIGHTINGра в JavaFX: Z-буфер, барицентрическая интеmode == RenderMode.ALL);

                if (drawWire) {
                    // Сетка НЕ обязана писать Z (обычно поверх). Но если хочешь — можно включить.
                    drawLine(pw, w, h, A.sx, A.sy, B.sx, B.sy, Color.WHITE);
                    drawLine(pw, w, h, B.sx, B.sy, C.sx, C.sy, Color.WHITE);
                    drawLine(pw, w, h, C.sx, C.sy, A.sx, A.sy, Color.WHITE);
                    if (!drawTex && !drawLight) {
                        continue; // только wireframe
                    }
                }

                // если включена текстура, но текстуры нет — будем рисовать серым
                if (drawTex && texReader == null) {
                    // ок, просто fallback
                }

                rasterizeTriangleZTexturedLit(
                        pw, w, h,
                        A, B, C,
                        uvA, uvB, uvC,
                        nA, nB, nC,
                        ray,
                        drawTex, drawLight,
                        texReader, texW, texH
                );
            }
        }

        // ============================================================
        // =============== Rasterization core (barycentric) ============
        // ============================================================

        private void rasterizeTriangleZTexturedLit(PixelWriter pw, int w, int h,
        VertexOut A, VertexOut B, VertexOut C,
                Vec2 uvA, Vec2 uvB, Vec2 uvC,
                Vec3 nA, Vec3 nB, Vec3 nC,
                Vec3 ray,
        boolean useTexture, boolean useLighting,
        PixelReader texReader, int texW, int texH) {

            // Bounding box
            int minX = clampInt((int) Math.floor(Math.min(A.sx, Math.min(B.sx, C.sx))), 0, w - 1);
            int maxX = clampInt((int) Math.ceil (Math.max(A.sx, Math.max(B.sx, C.sx))), 0, w - 1);
            int minY = clampInt((int) Math.floor(Math.min(A.sy, Math.min(B.sy, C.sy))), 0, h - 1);
            int maxY = clampInt((int) Math.ceil (Math.max(A.sy, Math.max(B.sy, C.sy))), 0, h - 1);

            // Edge function helpers (2D)
            float area = edge(A.sx, A.sy, B.sx, B.sy, C.sx, C.sy);
            if (area == 0) return;

            // Для устойчивости: допускаем любой winding
            boolean areaPositive = area > 0;

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {

                    // sample at pixel center
                    float px = x + 0.5f;
                    float py = y + 0.5f;

                    float w0 = edge(B.sx, B.sy, C.sx, C.sy, px, py);
                    float w1 = edge(C.sx, C.sy, A.sx, A.sy, px, py);
                    float w2 = edge(A.sx, A.sy, B.sx, B.sy, px, py);

                    if (areaPositive) {
                        if (w0 < 0              w2 < 0) continue;
                    } else {
                        if (w0 > 0арного рендераw2 > 0) continue;
                    }

                    // barycentric (screen-space)
                    float alpha = w0 / area;
                    float beta  = w1 / area;
                    float gamma = w2 / area;

                    // Perspective-correct interpolation (важно для UV и глубины):
                    // use invW = 1 / clipW, and interpolate attributes * invW
                    float invWA = A.invW;
                    float invWB = B.invW;
                    float invWC = C.invW;

                    float invW = alpha * invWA + beta * invWB + gamma * invWC;
                    if (invW == 0) continue;

                    // z в NDC после деления на w: A.ndcZ already
                    // Но корректнее — интерполировать ndcZ * invW и делить на invW:
                    float zOverW = alpha * (A.ndcZ * invWA) + beta * (B.ndcZ * invWB) + gamma * (C.ndcZ * invWC);
                    float ndcZ = zOverW / invW;

                    // Remap NDC z [-1..1] -> [0..1] (для Z-буфера)
                    float z01 = (ndcZ * 0.5f) + 0.5f;

                    int idx = y * w + x;
                    if (zBuffer[idx] > z01) {
                        // прошли Z
                        zBuffer[idx] = z01;

                        // base color
                        Color base = Color.GRAY;

                        if (useTexture) {
                            Vec2 uv = perspectiveCorrectUV(alpha, beta, gamma, uvA, uvB, uvC, invWA, invWB, invWC, invW);
                            base = sampleTexture(texReader, texW, texH, uv);
                        }

                        if (useLighting) {
                            // Интерполяция нормалей (сглаживание) + нормализация
                            Vec3 n = perspectiveCorrectNormal(alpha, beta, gamma, nA, nB, nC, invWA, invWB, invWC, invW).normalized();

                            // l = -n · ray
                            float l = -n.dot(ray);
                            if (l < 0) {
                                // свет "изнутри" => игнорируем (оставляем base как есть, без подсветки)
                                // По смыслу ТЗ: просто не применять освещение.
                            } else {
                                base = applyLambert(base, l);
                            }
                        }

                        pw.setColor(x, y, base);
                    }
                }
            }
        }

        private Vec2 perspectiveCorrectUV(float a, float b, float c,
        Vec2 uvA, Vec2 uvB, Vec2 uvC,
        float invWA, float invWB, float invWC,
        float invW) {
            float u = (a * (uvA.x * invWA) + b * (uvB.x * invWB) + c * (uvC.x * invWC)) / invW;
            float v = (a * (uvA.y * invWA) + b * (uvB.y * invWB) + c * (uvC.y * invWC)) / invW;
            return new Vec2(u, v);
        }

        private Vec3 perspectiveCorrectNormal(float a, float b, float c,
        Vec3 nA, Vec3 nB, Vec3 nC,
        float invWA, float invWB, float invWC,
        float invW) {
            float nx = (a * (nA.x * invWA) + b * (nB.x * invWB) + c * (nC.x * invWC)) / invW;
            float ny = (a * (nA.y * invWA) + b * (nB.y * invWB) + c * (nC.y * invWC)) / invW;
            float nz = (a * (nA.z * invWA) + b * (nB.z * invWB) + c * (nC.z * invWC)) / invW;
            return new Vec3(nx, ny, nz);
        }

        private Color applyLambert(Color rgb, float l) {
            // rgb' = rgb*(1-k) + rgb*k*l  where k=0.5
            float r = (float) rgb.getRed();
            float g = (float) rgb.getGreen();
            float b = (float) rgb.getBlue();

            float scale = (1.0f - kLambert) + (kLambert * l);
            r = clamp01(r * scale);
            g = clamp01(g * scale);
            b = clamp01(b * scale);

            return new Color(r, g, b, rgb.getOpacity());
        }

        private Color sampleTexture(PixelReader pr, int texW, int texH, Vec2 uv) {
            if (pr == nulle {

                /** Z-буtexH <= 0) return Color.GRAY;

                 // OBJ uv обычно в [0..1], v вверх, а у Image y вниз => переворачиваем v.
                 float u = uv.x - (float) Math.floor(uv.x); // repeat
                 float v = uv.
                 y - (float) Math.floor(uv.y); // repeat
                 int tx = clampInt((int) (u * (texW - 1)), 0, texW - 1);
                 int ty = clampInt((int) ((1.0f - v) * (texH - 1)), 0, texH - 1);

                 return pr.getColor(tx, ty);
                 }

                 private static float edge(float ax, float ay, float bx, float by, float px, float py) {
                 return (px - ax) * (by - ay) - (py - ay) * (bx - ax);
                 }

                 // ============================================================
                 // ====================== Triangulation =======================
                 // ============================================================

                 private List<int[]> triangulateAllFaces(ModelAdapter model) {
                 List<int[]> out = new ArrayList<>();
                 for (int f = 0; f < model.faceCount(); f++) {
                 int[] indices = model.getFaceVertexIndices(f); // любые полигоны
                 if (indices.length < 3) continue;
                 if (indices.length == 3) {
                 out.add(new int[]{indices[0], indices[1], indices[2]});
                 } else {
                 // fan: (0, i, i+1)
                 for (int i = 1; i < indices.length - 1; i++) {
                 out.add(new int[]{indices[0], indices[i], indices[i + 1]});
                 }
                 }
                 }
                 return out;
                 }

                 // ============================================================
                 // ====================== Normals recompute ===================
                 // ============================================================

                 private Vec3[] recomputeVertexNormals(ModelAdapter model, List<int[]> triangles, Vec3[] worldVerts) {
                 Vec3[] n = new Vec3[model.vertexCount()];
                 for (int i = 0; i < n.length; i++) n[i] = new Vec3(0, 0, 0);

                 for (int[] t : triangles) {
                 int ia = t[0], ib = t[1], ic = t[2];
                 Vec3 a = worldVerts[ia];
                 Vec3 b = worldVerts[ib];
                 Vec3 c = worldVerts[ic];

                 Vec3 e1 = b.sub(a);
                 Vec3 e2 = c.sub(a);
                 Vec3 faceN = e1.cross(e2); // не нормализуем — площадь как вес

                 n[ia] = n[ia].add(faceN);
                 n[ib] = n[ib].add(faceN);
                 n[ic] = n[ic].add(faceN);
                 }

                 for (int i = 0; i < n.length; i++) {
                 n[i] = n[i].normalized();
                 }
                 return n;
                 }

                 // ============================================================
                 // ====================== Transform vertex ====================
                 // ============================================================

                 private VertexOut transformVertex(Mat4 MVP, Vec3 v, int w, int h) {
                 Vec4 clip = MVP.mul(new Vec4(v, 1.0f));
                 if (clip.w == 0) return VertexOut.invalid();

                 float invW = 1.0f / clip.w;
                 float ndcX = clip.x * invW;
                 float ndcY = clip.y * invW;
                 float ndcZ = clip.z * invW; // [-1..1]

                 // NDC -> Screen
                 float sx = (ndcX * 0.5f + 0.5f) * (w - 1);
                 float sy = (1.0f - (ndcY * 0.5f + 0.5f)) * (h - 1); // y вниз

                 return new VertexOut(sx, sy, ndcZ, invW, true);
                 }

                 private static class VertexOut {
                 final float sx, sy;
                 final float ndcZ;
                 final float invW;
                 final boolean valid;

                 VertexOut(float sx, float sy, float ndcZ, float invW, boolean valid) {
                 this.sx = sx;
                 this.sy = sy;
                 this.ndcZ = ndcZ;
                 this.invW = invW;
                 this.valid = valid;
                 }

                 static VertexOut invalid() {
                 return new VertexOut(0, 0, 0, 0, false);
                 }
                 }

                 // ============================================================
                 // ========================= Wireframe ========================
                 // ============================================================

                 private void drawLine(PixelWriter pw, int w, int h,
                 float x0f, float y0f, float x1f, float y1f, Color c) {
                 int x0 = Math.round(x0f);
                 int y0 = Math.round(y0f);
                 int x1 = Math.round(x1f);
                 int y1 = Math.round(y1f);

                 int dx = Math.abs(x1 - x0);
                 int dy = Math.abs(y1 - y0);
                 int sx = x0 < x1 ? 1 : -1;
                 int sy = y0 < y1 ? 1 : -1;
                 int err = dx - dy;

                 int x = x0;
                 int y = y0;
                 while (true) {
                 if (x >= 0 && x < w && y >= 0 && y < h) pw.setColor(x, y, c);
                 if (x == x1 && y == y1) break;
                 int e2 = 2 * err;
                 if (e2 > -dy) { err -= dy; x += sx; }
                 if (e2 <  dx) { err += dx; y += sy; }
                 }
                 }

                 // ============================================================
                 // =========================== Utils ==========================
                 // ============================================================

                 private static int clampInt(int v, int lo, int hi) {
                 return Math.max(lo, Math.min(hi, v));
                 }

                 private static float clamp01(float v) {
                 return Math.max(0.0f, Math.min(1.0f, v));
                 }

                 // ============================================================
                 // =================== Model adapter interface =================
                 // ============================================================

                 /**
                 * Адаптер под твою модель из ObjReader.
                 * Реализуй этот интерфейс (обычно 30-50 строк) и всё заработает.
                 *
                 * ВАЖНО:
                 * - Индексация: 0-based.
                 * - getFaceVertexIndices(faceIndex) возвращает массив индексов вершин полигона (3..N).
                 * - getTexCoordForVertex(i) должен вернуть UV для вершины i (если у тебя face-vt привязка — см. ниже примечание).
                 *
                 * Если у тебя в OBJ: vt и v индексируются отдельно (face содержит пары v/vt),
                 * то проще сделать "развёрнутую" модель (unique vertex per (v,vt)),
                 * либо написать адаптер, который хранит соответствие vertexIndex -> uv.
                 */
                public interface ModelAdapter {
                    int vertexCount();
                    Vec3 getVertex(int vertexIndex);

                    int faceCount();
                    int[] getFaceVertexIndices(int faceIndex);

                    boolean hasTexCoords();
                    Vec2 getTexCoordForVertex(int vertexIndex);
                }

                // ============================================================
                // =================== Minimal math types ======================
                // ============================================================
                // Эти типы сделаны, чтобы код был самодостаточным.
                // Если у тебя уже есть Vector3f/Matrix4x4 — можешь:
                // 1) заменить Vec3/Mat4/Vec4 на свои,
                // 2) либо оставить как есть (они не конфликтуют, если в другом package).

                public static final class Vec2 {
                    public final float x, y;
                    public Vec2(float x, float y) { this.x = x; this.y = y; }
                }

                public static final class Vec3 {
                    public final float x, y, z;
                    public Vec3(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }

                    public Vec3 add(Vec3 o) { return new Vec3(x + o.x, y + o.y, z + o.z); }
                    public Vec3 sub(Vec3 o) { return new Vec3(x - o.x, y - o.y, z - o.z); }
                    public Vec3 mul(float s) { return new Vec3(x * s, y * s, z * s); }

                    public float dot(Vec3 o) { return x * o.x + y * o.y + z * o.z; }

                    public Vec3 cross(Vec3 o) {
                        return new Vec3(
                                y * o.z - z * o.y,
                                z * o.x - x * o.z,
                                x * o.y - y * o.x
                        );
                    }

                    public float len() { return (float) Math.sqrt(x * x + y * y + z * z); }

                    public Vec3 normalized() {
                        float l = len();
                        if (l == 0) return new Vec3(0, 0, 0);
                        return new Vec3(x / l, y / l, z / l);
                    }
                }

                public static final class Vec4 {
                    public final float x, y, z, w;

                    public Vec4(float x, float y, float z, float w) {
                        this.x = x; this.y = y; this.z = z; this.w = w;
                    }

                    public Vec4(Vec3 v, float w) {
                        this(v.x, v.y, v.z, w);
                    }
                }

                public static final class Mat4 {
                    // row-major 4x4
                    private final float[] m; // length 16

                    public Mat4(float[] m16) {
                        if (m16 == null || m16.length != 16) throw new IllegalArgumentException("Mat4 needs 16 floats");
                        this.m = m16.clone();
                    }

                    public Mat4 mul(Mat4 b) {
                        float[] a = this.m;
                        float[] c = new float[16];
                        float[] bb = b.m;

                        // c = a * b (row-major)
                        for (int r = 0; r < 4; r++) {
                            for (int col = 0; col < 4; col++) {
                                c[r * 4 + col] =
                                        a[r * 4 + 0] * bb[0 * 4 + col] +
                                                a[r * 4 + 1] * bb[1 * 4 + col] +
                                                a[r * 4 + 2] * bb[2 * 4 + col] +
                                                a[r * 4 + 3] * bb[3 * 4 + col];
                            }
                        }
                        return new Mat4(c);
                    }

                    public Vec4 mul(Vec4 v) {
                        float[] a = this.m;
                        float x = a[0] * v.x + a[1] * v.y + a[2] * v.z + a[3] * v.w;
                        float y = a[4] * v.x + a[5] * v.y + a[6] * v.z + a[7] * v.w;
                        float z = a[8] * v.x + a[9] * v.y + a[10]* v.z + a[11]* v.w;
                        float w = a[12]* v.x + a[13]* v.y + a[14]* v.z + a[15]* v.w;
                        return new Vec4(x, y, z, w);
                    }
                }
            }


⸻

            4) Пример управления камерами: CameraManager.java

package renderer;

import java.util.ArrayList;
import java.util.List;

            public class CameraManager {
                private final List<Camera> cameras = new ArrayList<>();
                private int activeIndex = -1;

                public void addCamera(Camera camera) {
                    cameras.add(camera);
                    if (activeIndex < 0) activeIndex = 0;
                }

                public void removeCamera(int index) {
                    if (index < 0 || index >= cameras.size()) return;
                    cameras.remove(index);
                    if (cameras.isEmpty()) activeIndex = -1;
                    else activeIndex = Math.min(activeIndex, cameras.size() - 1);
                }

                public void setActiveIndex(int index) {
                    if (index < 0 || index >= cameras.size()) return;
                    activeIndex = index;
                }

                public Camera getActive() {
                    if (activeIndex < 0) throw new IllegalStateException("No cameras");
                    return cameras.get(activeIndex);
                }

                public int size() { return cameras.size(); }
                public int getActiveIndex() { return activeIndex; }
                public List<Camera> getAll() { return List.copyOf(cameras); }
            }
