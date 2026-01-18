package com.cgvsu.render_engine;

import com.cgvsu.math.*;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.model.Texture;
import com.cgvsu.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;

import static com.cgvsu.render_engine.GraphicConveyor.*;

import javafx.scene.image.*;

public class RenderEngine {

    private static Matrix4f modelViewProjectionMatrix;
    private static Texture texture = null;

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Scene scene,
            final int width,
            final int height,
            RenderParameters renderParameters
    ) {
        Matrix4f modelMatrix = GraphicConveyor.scaleRotateTranslate(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), new Vector3f(0, 0, 0));
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        modelViewProjectionMatrix = new Matrix4f(projectionMatrix);
        modelViewProjectionMatrix.multiply(viewMatrix);
        modelViewProjectionMatrix.multiply(modelMatrix);

        if (scene == null || scene.getModels().isEmpty()) {
            return;
        }

        for (Model model : scene.getModels()) {
            renderModel(graphicsContext, modelViewProjectionMatrix, model, width, height, renderParameters);
        }
    }

    private static void renderModel(
            final GraphicsContext graphicsContext,
            final Matrix4f modelViewProjectionMatrix,
            final Model model,
            final int width,
            final int height,
            RenderParameters renderParameters
    ) {
        if (model != null) {
            texture = model.getTexture();
        }

        Color fillColor = Color.RED;

        if (model == null) {
            return;
        }

        final int nPolygons = model.getPolygons().size();

        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = model.getPolygons().get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> screenPoints = new ArrayList<>();
            ArrayList<Float> zValues = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = model.getVertices().get(model.getPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                Vector3f vertexVecmath = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());

                Vector3f transformedVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
                Point2f screenPoint = vertexToPoint(transformedVertex, width, height);
                screenPoints.add(screenPoint);
                zValues.add(transformedVertex.getZ());
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        screenPoints.get(vertexInPolygonInd - 1).getX(),
                        screenPoints.get(vertexInPolygonInd - 1).getY(),
                        screenPoints.get(vertexInPolygonInd).getX(),
                        screenPoints.get(vertexInPolygonInd).getY());
            }

            if (nVerticesInPolygon > 0) {
                graphicsContext.strokeLine(
                        screenPoints.get(nVerticesInPolygon - 1).getX(),
                        screenPoints.get(nVerticesInPolygon - 1).getY(),
                        screenPoints.get(0).getX(),
                        screenPoints.get(0).getY());
            }
        }

        if (!renderParameters.getEmptyParams()) {
            if (renderParameters.isTexturePolygon()) {
                renderPolygonTexture(graphicsContext, model, width, height);
            }
            if (renderParameters.isColorPolygon()) {
                renderPolygonColor(graphicsContext, model, width, height);
            }
            if (renderParameters.isAllColorPolygon()) {
                renderPolygonAllColor(graphicsContext, model, width, height);
            }
        }
    }

    private static void renderPolygonAllColor(
            GraphicsContext graphicsContext,
            Model mesh,
            int width,
            int height) {
        float[][] zBuffer = initializeZBuffer(width, height);

        for (Polygon polygon : mesh.getPolygons()) {
            if (polygon.getVertexIndices().size() == 4) {
                Point2f[] screenPoints = new Point2f[polygon.getVertexIndices().size()];
                float[] zValues = new float[polygon.getVertexIndices().size()];

                for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
                    Vector3f transformedVertex = transformVertex(mesh, polygon, i, modelViewProjectionMatrix);
                    screenPoints[i] = vertexToPoint(transformedVertex, width, height);
                    zValues[i] = transformedVertex.getZ();
                }
                rasterizeTriangle(screenPoints, zValues, zBuffer, graphicsContext);
            }
        }
    }

    private static void renderPolygonColor(
            GraphicsContext graphicsContext,
            Model mesh,
            int width,
            int height
    ) {
        float[][] zBuffer = initializeZBuffer(width, height);

        for (Polygon polygon : mesh.getPolygons()) {
            if (polygon.getVertexIndices().size() == 3) {
                Point2f[] screenPoints = new Point2f[polygon.getVertexIndices().size()];
                float[] zValues = new float[polygon.getVertexIndices().size()];

                for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
                    Vector3f transformedVertex = transformVertex(mesh, polygon, i, modelViewProjectionMatrix);
                    screenPoints[i] = vertexToPoint(transformedVertex, width, height);
                    zValues[i] = transformedVertex.getZ();
                }
                rasterizeTriangleColor(screenPoints, zValues, zBuffer, graphicsContext);
            }
        }
    }

    private static void renderPolygonTexture(
            GraphicsContext graphicsContext,
            Model mesh,
            int width,
            int height
    ) {
        float[][] zBuffer = initializeZBuffer(width, height);

        for (Polygon polygon : mesh.getPolygons()) {
            if (polygon.getVertexIndices().size() == 4) {
                Point2f[] screenPoints = new Point2f[polygon.getVertexIndices().size()];
                float[] zValues = new float[polygon.getVertexIndices().size()];

                ArrayList<Vector2f> texCoords = polygon.getTextureCoordinates();

                for (int i = 0; i < polygon.getVertexIndices().size(); i++) {
                    Vector3f transformedVertex = transformVertex(mesh, polygon, i, modelViewProjectionMatrix);
                    float u = texCoords.get(i).getX();
                    float v = texCoords.get(i).getY();

                    screenPoints[i] = vertexToPoint1(transformedVertex, width, height, u, v);
                    zValues[i] = transformedVertex.getZ();
                }
                rasterizeTexture(screenPoints, zValues, zBuffer, graphicsContext, mesh);
            }
        }
    }

    private static void rasterizeTexture(Point2f[] points, float[] zValues, float[][] zBuffer, GraphicsContext graphicsContext, Model model) {
        Image texture = new Image(Objects.requireNonNull(RenderEngine.class.getResourceAsStream("/images/123.jpg")));

        int minX = (int) Math.max(0, Math.floor(Math.min(
                points[0].getX(),
                Math.min(points[1].getX(), Math.min(points[2].getX(), points[3].getX()))
        )));
        int maxX = (int) Math.min(zBuffer.length - 1, Math.ceil(Math.max(
                points[0].getX(),
                Math.max(points[1].getX(), Math.max(points[2].getX(), points[3].getX()))
        )));
        int minY = (int) Math.max(0, Math.floor(Math.min(
                points[0].getY(),
                Math.min(points[1].getY(), Math.min(points[2].getY(), points[3].getY()))
        )));
        int maxY = (int) Math.min(zBuffer[0].length - 1, Math.ceil(Math.max(
                points[0].getY(),
                Math.max(points[1].getY(), Math.max(points[2].getY(), points[3].getY()))
        )));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                float[] barycentricCoords1 = calculateBarycentricCoordinates(new Point2f[]{points[0], points[1], points[2]}, x, y);
                float[] barycentricCoords2 = calculateBarycentricCoordinates(new Point2f[]{points[0], points[2], points[3]}, x, y);

                boolean insideTriangle1 = isPointInside(barycentricCoords1[0], barycentricCoords1[1], barycentricCoords1[2]);
                boolean insideTriangle2 = isPointInside(barycentricCoords2[0], barycentricCoords2[1], barycentricCoords2[2]);

                if (insideTriangle1 || insideTriangle2) {
                    float depth = insideTriangle1 ? interpolateDepth1(barycentricCoords1, zValues) : interpolateDepth1(barycentricCoords2, zValues);

                    if (depth < zBuffer[x][y]) {
                        zBuffer[x][y] = depth;

                        float u, v;
                        if (insideTriangle1) {
                            u = barycentricCoords1[0] * points[0].getU() +
                                    barycentricCoords1[1] * points[1].getU() +
                                    barycentricCoords1[2] * points[2].getU();
                            v = barycentricCoords1[0] * points[0].getV() +
                                    barycentricCoords1[1] * points[1].getV() +
                                    barycentricCoords1[2] * points[2].getV();
                        } else {
                            u = barycentricCoords2[0] * points[0].getU() +
                                    barycentricCoords2[1] * points[2].getU() +
                                    barycentricCoords2[2] * points[3].getU();
                            v = barycentricCoords2[0] * points[0].getV() +
                                    barycentricCoords2[1] * points[2].getV() +
                                    barycentricCoords2[2] * points[3].getV();
                        }

                        Color textureColor = getColor1(texture, u, v);
                        graphicsContext.getPixelWriter().setColor(x, y, textureColor);
                    }
                }
            }
        }
    }

    public static Color getColor1(Image texture, float u, float v) {
        u = clamp1(u, 0.0f, 1.0f);
        v = clamp1(v, 0.0f, 1.0f);

        int x = (int) Math.min((int) (u * (texture.getWidth() - 1)), texture.getWidth() - 1);
        int y = (int) Math.min((int) ((1 - v) * (texture.getHeight() - 1)), texture.getHeight() - 1);

        PixelReader pixelReader = texture.getPixelReader();
        return pixelReader.getColor(x, y);
    }

    private static float clamp1(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void rasterizeTriangle(Point2f[] points, float[] zValues, float[][] zBuffer, GraphicsContext graphicsContext) {
        Color colorA = Color.RED;
        int minX = (int) Math.max(0, Math.floor(Math.min(
                points[0].getX(),
                Math.min(points[1].getX(), Math.min(points[2].getX(), points[3].getX()))
        )));
        int maxX = (int) Math.min(zBuffer.length - 1, Math.ceil(Math.max(
                points[0].getX(),
                Math.max(points[1].getX(), Math.max(points[2].getX(), points[3].getX()))
        )));
        int minY = (int) Math.max(0, Math.floor(Math.min(
                points[0].getY(),
                Math.min(points[1].getY(), Math.min(points[2].getY(), points[3].getY()))
        )));
        int maxY = (int) Math.min(zBuffer[0].length - 1, Math.ceil(Math.max(
                points[0].getY(),
                Math.max(points[1].getY(), Math.max(points[2].getY(), points[3].getY()))
        )));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                float[] barycentricCoords1 = calculateBarycentricCoordinates(new Point2f[]{points[0], points[1], points[2]}, x, y);
                float[] barycentricCoords2 = calculateBarycentricCoordinates(new Point2f[]{points[0], points[2], points[3]}, x, y);

                boolean insideTriangle1 = isPointInside(barycentricCoords1[0], barycentricCoords1[1], barycentricCoords1[2]);
                boolean insideTriangle2 = isPointInside(barycentricCoords2[0], barycentricCoords2[1], barycentricCoords2[2]);

                if (insideTriangle1 || insideTriangle2) {
                    float depth = interpolateDepth1(barycentricCoords1, zValues);

                    if (depth < zBuffer[x][y]) {
                        zBuffer[x][y] = depth;
                        graphicsContext.getPixelWriter().setColor(x, y, colorA);
                    }
                }
            }
        }
    }

    private static void rasterizeTriangleColor(Point2f[] points, float[] zValues, float[][] zBuffer, GraphicsContext graphicsContext) {
        Color colorA = Color.GREEN;
        Color colorB = Color.BLACK;
        Color colorC = Color.YELLOW;

        int minX = (int) Math.max(0, Math.floor(Math.min(points[0].getX(), Math.min(points[1].getX(), points[2].getX()))));
        int maxX = (int) Math.min(zBuffer.length - 1, Math.ceil(Math.max(points[0].getX(), Math.max(points[1].getX(), points[2].getX()))));
        int minY = (int) Math.max(0, Math.floor(Math.min(points[0].getY(), Math.min(points[1].getY(), points[2].getY()))));
        int maxY = (int) Math.min(zBuffer[0].length - 1, Math.ceil(Math.max(points[0].getY(), Math.max(points[1].getY(), points[2].getY()))));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                float[] barycentricCoords = calculateBarycentricCoordinates(points, x, y);
                float lambda0 = barycentricCoords[0];
                float lambda1 = barycentricCoords[1];
                float lambda2 = barycentricCoords[2];

                if (isPointInsideTriangle(lambda0, lambda1, lambda2)) {
                    float depth = interpolateDepth(lambda0, lambda1, lambda2, zValues);

                    if (depth < zBuffer[x][y]) {
                        zBuffer[x][y] = depth;
                        Color color = interpolateColor(lambda0, lambda1, lambda2, colorA, colorB, colorC);
                        graphicsContext.getPixelWriter().setColor(x, y, color);
                    }
                }
            }
        }
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static Color interpolateColor(float lambda0, float lambda1, float lambda2, Color colorA, Color colorB, Color colorC) {
        double red = lambda0 * colorA.getRed() + lambda1 * colorB.getRed() + lambda2 * colorC.getRed();
        double green = lambda0 * colorA.getGreen() + lambda1 * colorB.getGreen() + lambda2 * colorC.getGreen();
        double blue = lambda0 * colorA.getBlue() + lambda1 * colorB.getBlue() + lambda2 * colorC.getBlue();
        return new Color(clamp(red), clamp(green), clamp(blue), 1.0);
    }

    private static boolean isPointInsideTriangle(float lambda0, float lambda1, float lambda2) {
        return lambda0 >= 0 && lambda1 >= 0 && lambda2 >= 0;
    }

    private static boolean isPointInside(float lambda0, float lambda1, float lambda2) {
        return lambda0 >= 0 && lambda1 >= 0 && lambda2 >= 0;
    }

    private static float interpolateDepth(float lambda0, float lambda1, float lambda2, float[] zValues) {
        return lambda0 * zValues[0] + lambda1 * zValues[1] + lambda2 * zValues[2];
    }

    private static float interpolateDepth1(float[] barycentricCoords, float[] zValues) {
        return barycentricCoords[0] * zValues[0] + barycentricCoords[1] * zValues[1] + barycentricCoords[2] * zValues[2];
    }

    private static float[] calculateBarycentricCoordinates(Point2f[] points, int x, int y) {
        float denominator = ((points[1].getY() - points[2].getY()) * (points[0].getX() - points[2].getX()) +
                (points[2].getX() - points[1].getX()) * (points[0].getY() - points[2].getY()));

        float lambda0 = ((points[1].getY() - points[2].getY()) * (x - points[2].getX()) +
                (points[2].getX() - points[1].getX()) * (y - points[2].getY())) / denominator;

        float lambda1 = ((points[2].getY() - points[0].getY()) * (x - points[2].getX()) +
                (points[0].getX() - points[2].getX()) * (y - points[2].getY())) / denominator;

        float lambda2 = 1.0f - lambda0 - lambda1;

        return new float[]{lambda0, lambda1, lambda2};
    }

    private static Vector3f transformVertex(Model mesh, Polygon polygon, int vertexIndex, Matrix4f transformationMatrix) {
        Vector3f vertex = mesh.getVertices().get(polygon.getVertexIndices().get(vertexIndex));
        Vector3f vertexVecmath = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());
        return multiplyMatrix4ByVector3(transformationMatrix, vertexVecmath);
    }

    private static float[][] initializeZBuffer(int width, int height) {
        float[][] zBuffer = new float[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                zBuffer[x][y] = Float.MAX_VALUE;
            }
        }
        return zBuffer;
    }
}
