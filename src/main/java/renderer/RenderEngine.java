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
 */
public class RenderEngine {

    /** Z-буфер: глубина на пиксель. По ТЗ: if(zBuffer(x,y) > z) then draw */
    private float[] zBuffer;
    private int bufferW;
    private int bufferH;
    private float kLambert = 0.5f; // коэффициент освещения

    /**
     * Основной метод рендеринга.
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

        // Свет по ТЗ: l = -n·ray, где ray - направление света (из камеры)
        Vec3 ray = camera.lightRayDirection();

        // 1) Триангуляция всех полигонов (если не треугольники)
        List<int[]> triangles = triangulateAllFaces(model);

        // 2) Пересчет нормалей для вершин
        Vec3[] worldVerts = new Vec3[model.vertexCount()];
        for (int i = 0; i < model.vertexCount(); i++) {
            Vec4 v = new Vec4(model.getVertex(i), 1.0f);
            Vec4 vw = modelMatrix.mul(v);
            worldVerts[i] = new Vec3(vw.x, vw.y, vw.z);
        }
        Vec3[] vertexNormals = recomputeVertexNormals(model, triangles, worldVerts);

        // 3) Растеризация треугольников
        for (int[] tri : triangles) {
            int ia = tri[0], ib = tri[1], ic = tri[2];

            // Преобразование вершин
            VertexOut A = transformVertex(MVP, model.getVertex(ia), w, h);
            VertexOut B = transformVertex(MVP, model.getVertex(ib), w, h);
            VertexOut C = transformVertex(MVP, model.getVertex(ic), w, h);

            if (!A.valid || !B.valid || !C.valid) continue;

            // UV координаты
            Vec2 uvA = model.hasTexCoords() ? model.getTexCoordForVertex(ia) : new Vec2(0, 0);
            Vec2 uvB = model.hasTexCoords() ? model.getTexCoordForVertex(ib) : new Vec2(0, 0);
            Vec2 uvC = model.hasTexCoords() ? model.getTexCoordForVertex(ic) : new Vec2(0, 0);

            // Нормали
            Vec3 nA = vertexNormals[ia];
            Vec3 nB = vertexNormals[ib];
            Vec3 nC = vertexNormals[ic];

            // Определение режимов отрисовки
            boolean drawWire = (mode == RenderMode.WIREFRAME || mode == RenderMode.ALL);
            boolean drawTex = (mode == RenderMode.TEXTURE || mode == RenderMode.TEXTURE_LIGHTING || mode == RenderMode.ALL);
            boolean drawLight = (mode == RenderMode.LIGHTING || mode == RenderMode.TEXTURE_LIGHTING || mode == RenderMode.ALL);

            if (drawWire) {
                drawLine(pw, w, h, A.sx, A.sy, B.sx, B.sy, Color.WHITE);
                drawLine(pw, w, h, B.sx, B.sy, C.sx, C.sy, Color.WHITE);
                drawLine(pw, w, h, C.sx, C.sy, A.sx, A.sy, Color.WHITE);
                if (!drawTex && !drawLight) continue;
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

    /**
     * Растеризация треугольника с Z-буфером, текстурой и освещением.
     */
    private void rasterizeTriangleZTexturedLit(PixelWriter pw, int w, int h,
                                               VertexOut A, VertexOut B, VertexOut C,
                                               Vec2 uvA, Vec2 uvB, Vec2 uvC,
                                               Vec3 nA, Vec3 nB, Vec3 nC,
                                               Vec3 ray,
                                               boolean useTexture, boolean useLighting,
                                               PixelReader texReader, int texW, int texH) {

        // Bounding box
        int minX = clampInt((int) Math.floor(Math.min(A.sx, Math.min(B.sx, C.sx))), 0, w - 1);
        int maxX = clampInt((int) Math.ceil(Math.max(A.sx, Math.max(B.sx, C.sx))), 0, w - 1);
        int minY = clampInt((int) Math.floor(Math.min(A.sy, Math.min(B.sy, C.sy))), 0, h - 1);
        int maxY = clampInt((int) Math.ceil(Math.max(A.sy, Math.max(B.sy, C.sy))), 0, h - 1);

        // Edge function
        float area = edge(A.sx, A.sy, B.sx, B.sy, C.sx, C.sy);
        if (area == 0) return;

        boolean areaPositive = area > 0;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                float px = x + 0.5f;
                float py = y + 0.5f;

                float w0 = edge(B.sx, B.sy, C.sx, C.sy, px, py);
                float w1 = edge(C.sx, C.sy, A.sx, A.sy, px, py);
                float w2 = edge(A.sx, A.sy, B.sx, B.sy, px, py);

                if (areaPositive) {
                    if (w0 < 0 || w1 < 0 || w2 < 0) continue;
                } else {
                    if (w0 > 0 || w1 > 0 || w2 > 0) continue;
                }

                float alpha = w0 / area;
                float beta = w1 / area;
                float gamma = w2 / area;

                // Perspective-correct interpolation
                float invWA = A.invW;
                float invWB = B.invW;
                float invWC = C.invW;
                float invW = alpha * invWA + beta * invWB + gamma * invWC;
                if (invW == 0) continue;

                float zOverW = alpha * (A.ndcZ * invWA) + beta * (B.ndcZ * invWB) + gamma * (C.ndcZ * invWC);
                float ndcZ = zOverW / invW;
                float z01 = (ndcZ * 0.5f) + 0.5f; // remap to [0..1]

                int idx = y * w + x;
                if (zBuffer[idx] > z01) {
                    zBuffer[idx] = z01;

                    Color base = Color.GRAY;

                    if (useTexture && texReader != null) {
                        Vec2 uv = perspectiveCorrectUV(alpha, beta, gamma, uvA, uvB, uvC, invWA, invWB, invWC, invW);
                        base = sampleTexture(texReader, texW, texH, uv);
                    }

                    if (useLighting) {
                        Vec3 n = perspectiveCorrectNormal(alpha, beta, gamma, nA, nB, nC, invWA, invWB, invWC, invW).normalized();
                        float l = -n.dot(ray);
                        if (l > 0) {
                            base = applyLambert(base, l);
                        }
                    }

                    pw.setColor(x, y, base);
                }
            }
        }
    }

    /**
     * Очистка экрана и Z-буфера.
     */
    public void clear(WritableImage image, Color color, boolean clearZ) {
        PixelWriter pw = image.getPixelWriter();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                pw.setColor(x, y, color);
            }
        }
        if (clearZ && zBuffer != null) {
            for (int i = 0; i < zBuffer.length; i++) {
                zBuffer[i] = Float.MAX_VALUE;
            }
        }
    }

    /**
     * Убедиться, что буферы созданы нужного размера.
     */
    private void ensureBuffers(int width, int height) {
        if (zBuffer == null || bufferW != width || bufferH != height) {
            bufferW = width;
            bufferH = height;
            zBuffer = new float[width * height];
        }
    }

    /**
     * Интерполяция UV координат с учетом перспективы.
     */
    private Vec2 perspectiveCorrectUV(float a, float b, float c,
                                      Vec2 uvA, Vec2 uvB, Vec2 uvC,
                                      float invWA, float invWB, float invWC,
                                      float invW) {
        float u = (a * (uvA.x * invWA) + b * (uvB.x * invWB) + c * (uvC.x * invWC)) / invW;
        float v = (a * (uvA.y * invWA) + b * (uvB.y * invWB) + c * (uvC.y * invWC)) / invW;
        return new Vec2(u, v);
    }

    /**
     * Интерполяция нормалей с учетом перспективы.
     */
    private Vec3 perspectiveCorrectNormal(float a, float b, float c,
                                          Vec3 nA, Vec3 nB, Vec3 nC,
                                          float invWA, float invWB, float invWC,
                                          float invW) {
        float nx = (a * (nA.x * invWA) + b * (nB.x * invWB) + c * (nC.x * invWC)) / invW;
        float ny = (a * (nA.y * invWA) + b * (nB.y * invWB) + c * (nC.y * invWC)) / invW;
        float nz = (a * (nA.z * invWA) + b * (nB.z * invWB) + c * (nC.z * invWC)) / invW;
        return new Vec3(nx, ny, nz);
    }

    /**
     * Применение модели освещения Ламберта.
     */
    private Color applyLambert(Color rgb, float l) {
        float r = (float) rgb.getRed();
        float g = (float) rgb.getGreen();
        float b = (float) rgb.getBlue();

        float scale = (1.0f - kLambert) + (kLambert * l);
        r = clamp01(r * scale);
        g = clamp01(g * scale);
        b = clamp01(b * scale);

        return new Color(r, g, b, rgb.getOpacity());
    }

    /**
     * Выборка цвета из текстуры.
     */
    private Color sampleTexture(PixelReader pr, int texW, int texH, Vec2 uv) {
        if (pr == null || texW <= 0 || texH <= 0) return Color.GRAY;

        float u = uv.x - (float) Math.floor(uv.x); // repeat
        float v = uv.y - (float) Math.floor(uv.y); // repeat

        int tx = clampInt((int) (u * (texW - 1)), 0, texW - 1);
        int ty = clampInt((int) ((1.0f - v) * (texH - 1)), 0, texH - 1);

        return pr.getColor(tx, ty);
    }

    /**
     * Функция edge для барицентрических координат.
     */
    private static float edge(float ax, float ay, float bx, float by, float px, float py) {
        return (px - ax) * (by - ay) - (py - ay) * (bx - ax);
    }

    /**
     * Триангуляция всех полигонов.
     */
    private List<int[]> triangulateAllFaces(ModelAdapter model) {
        List<int[]> out = new ArrayList<>();
        for (int f = 0; f < model.faceCount(); f++) {
            int[] indices = model.getFaceVertexIndices(f);
            if (indices.length < 3) continue;
            if (indices.length == 3) {
                out.add(new int[]{indices[0], indices[1], indices[2]});
            } else {
                for (int i = 1; i < indices.length - 1; i++) {
                    out.add(new int[]{indices[0], indices[i], indices[i + 1]});
                }
            }
        }
        return out;
    }

    /**
     * Пересчет нормалей для вершин.
     */
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
            Vec3 faceN = e1.cross(e2);

            n[ia] = n[ia].add(faceN);
            n[ib] = n[ib].add(faceN);
            n[ic] = n[ic].add(faceN);
        }

        for (int i = 0; i < n.length; i++) {
            n[i] = n[i].normalized();
        }
        return n;
    }

    /**
     * Преобразование вершины.
     */
    private VertexOut transformVertex(Mat4 MVP, Vec3 v, int w, int h) {
        Vec4 clip = MVP.mul(new Vec4(v, 1.0f));
        if (clip.w == 0) return VertexOut.invalid();

        float invW = 1.0f / clip.w;
        float ndcX = clip.x * invW;
        float ndcY = clip.y * invW;
        float ndcZ = clip.z * invW;

        float sx = (ndcX * 0.5f + 0.5f) * (w - 1);
        float sy = (1.0f - (ndcY * 0.5f + 0.5f)) * (h - 1);

        return new VertexOut(sx, sy, ndcZ, invW, true);
    }

    /**
     * Отрисовка линии.
     */
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
            if (e2 < dx) { err += dx; y += sy; }
        }
    }

    // ====================== УТИЛИТЫ ======================

    private static int clampInt(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static float clamp01(float v) {
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    // ====================== ВНУТРЕННИЕ КЛАССЫ ======================

    /**
     * Результат преобразования вершины.
     */
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

    /**
     * Интерфейс адаптера модели.
     */
    public interface ModelAdapter {
        int vertexCount();
        Vec3 getVertex(int vertexIndex);

        int faceCount();
        int[] getFaceVertexIndices(int faceIndex);

        boolean hasTexCoords();
        Vec2 getTexCoordForVertex(int vertexIndex);
    }

    // ====================== МАТЕМАТИЧЕСКИЕ ТИПЫ ======================

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
        private final float[] m = new float[16];

        public Mat4(float[] m16) {
            if (m16 == null || m16.length != 16)
                throw new IllegalArgumentException("Mat4 needs 16 floats");
            System.arraycopy(m16, 0, m, 0, 16);
        }

        public Mat4 mul(Mat4 b) {
            float[] a = this.m;
            float[] c = new float[16];
            float[] bb = b.m;

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
            float z = a[8] * v.x + a[9] * v.y + a[10] * v.z + a[11] * v.w;
            float w = a[12] * v.x + a[13] * v.y + a[14] * v.z + a[15] * v.w;
            return new Vec4(x, y, z, w);
        }
    }
}