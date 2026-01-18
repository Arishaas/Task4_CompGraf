package com.cgvsu.model;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Point2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.GraphicConveyor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static com.cgvsu.model.BoundingBox.getBoundingBox;
import static com.cgvsu.render_engine.GraphicConveyor.multiplyMatrix4ByVector3;
import static com.cgvsu.render_engine.GraphicConveyor.vertexToPoint;

public class ModelColor {
    static private Model localmodel;
    static private Camera localcamera;
    static private GraphicsContext graphicsContext;

    public static void setModelColor(Model model, Camera camera, GraphicsContext gr) {
        System.out.println("setModelColor");
        localmodel = model;
        localcamera = camera;
        graphicsContext = gr;
        // Сохраняем старые полигоны для отмены
        model.setOriginalPolygons(model.getPolygons());
        // Формируем новые полигоны
        model.setColorsPolygons(ModelColor.colorModel(model.getPolygons()));
        // устанавливаем новые полигоны в текущие
        model.setPolygons(model.getColorsPolygons());
    }

    public static ArrayList<Polygon> colorModel( ArrayList<Polygon> polygons){
        ArrayList<Polygon> newModelPoly = new ArrayList<Polygon>();

        for (int i = 0; i < polygons.size(); i++) {
            newModelPoly.add(
                    newColorPolygon(polygons.get(i)));
        }
        return newModelPoly;
    }

    public static Polygon newColorPolygon( Polygon polygons){

        Matrix4f modelMatrix = GraphicConveyor.scaleRotateTranslate(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), new Vector3f(0, 0, 0));
        Matrix4f viewMatrix = localcamera.getViewMatrix();
        Matrix4f projectionMatrix = localcamera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(projectionMatrix);
        modelViewProjectionMatrix.multiply(viewMatrix);
        modelViewProjectionMatrix.multiply(modelMatrix);



        Polygon newPolygon = new Polygon();

        final int nVerticesInPolygon = polygons.getVertexIndices().size();
        System.out.println("nVerticesInPolygon: " + nVerticesInPolygon);

        ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
        for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
            Vector3f vertex = localmodel.getVertices().get(polygons.getVertexIndices().get(vertexInPolygonInd));
            Vector3f point = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());
            vertices.add(point);
        }

        ArrayList<Point2f> ver = new ArrayList<>();
        ArrayList<Float> zValues = new ArrayList<>();
        ArrayList<Vector3f> vertVec = new ArrayList<>();
        for (int vertexIndex : polygons.getVertexIndices()) {
            Vector3f vertex = localmodel.getVertices().get(vertexIndex);
//
          //  System.out.println("X vertex: " + vertex.getX());
        //    System.out.println("Y vertex: " + vertex.getY());
            Point2f screenPoint = vertexToPoint(vertex, 1600, 860);
            // Теперь screenPoint содержит координаты пикселей на холсте
            // Вы можете использовать screenPoint для рисования на холсте

          //  System.out.println("X screenPoint: " + screenPoint.getX());
          //  System.out.println("Y screenPoint: " + screenPoint.getY());
            ver.add(screenPoint);



            Vector3f vertexVecmath = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());

            Vector3f transformedVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
            zValues.add(transformedVertex.getZ());
            vertVec.add(vertexVecmath);



        }
        // Создание нового полигона
        newPolygon.setVertexIndices(polygons.getVertexIndices());
        BoundingBox boundingBox = getBoundingBox(ver);



        // Пример использования GraphicsContext для закрашивания полигона
       // graphicsContext.setFill(Color.RED);
     //   BoundingBox boundingBox = getBoundingBox(ver);
        Color fillColor = Color.RED;
        rasterizePolygon(graphicsContext, ver, fillColor, boundingBox, zValues);


  //      rasterizePolygon(scene. graphicsContext, ver, zBuffer, fillColor, boundingBox, zValues);

        return newPolygon;
    }


    private static void rasterizePolygon(GraphicsContext graphicsContext,
                                         ArrayList<Point2f> screenPoints,
                                      //   float[] zBuffer,
                                         Color fillColor,
                                         BoundingBox boundingBox,
                                         ArrayList<Float> zValues
                                        ) {
        int x_min = (int)boundingBox.getMinX();
        int x_max = (int)boundingBox.getMaxX();
        int y_min = (int)boundingBox.getMinY();
        int y_max = (int)boundingBox.getMaxY();
//        System.out.println("rasterizePolygon: " + x_min + " = " + x_max);
//        System.out.println("rasterizePolygon: " + y_min + " = " + y_max);
//
//        System.out.println("rasterizePolygon: " + boundingBox.getMinY() + " = "+ boundingBox.getMaxY());
//        System.out.println("rasterizePolygon: " + boundingBox.getMinX() + " = "+ boundingBox.getMaxX());
        //return;


        int index = 0;
        for (int y = y_min; y < y_max; y++) {
            for (int x = x_min; x < x_max; x++) {
                Point2f point = new Point2f(x, y);
                if (isPointInsidePolygon(point, screenPoints)) {

                    //System.out.println("dgyfUFAEUUUUUUUUUUUUUUUUUUUUUUUUU: ");


                    //int index = y * x_max + x;
                    float z = interpolateZ(point, screenPoints, zValues);

                    System.out.println("interpolateZ : " + x);
                    System.out.println("interpolateZ : " + y);


                    // if (z < zBuffer[index]) {


                    //      zBuffer[index] = z;
                   // graphicsContext.getPixelWriter().setColor(x, y, fillColor);
                   // graphicsContext.fillRect(x, y, 1, 1);

                    System.out.println("fillColor : " + fillColor);

                    graphicsContext.getPixelWriter().setColor(x, y, Color.RED);

//                    WritableImage writableImage = new WritableImage(w, h);
//                    PixelWriter pixelWriter = writableImage.getPixelWriter();
//                    pixelWriter.setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), colorBuffer, 0, w);
//                    graphicsContext.drawImage(writableImage, 0, 0);

                    //   }
                }
            }
        }
    }

    private static boolean isPointInsidePolygon(Point2f point, ArrayList<Point2f> polygon) {
    //    System.out.println("isPointInsidePolygon: " + point.getX() + " = "+ point.getY());
     //   System.out.println("    isPointInsidePolygon: " + polygon.size());

        // return true;

        boolean inside = false;
        int n = polygon.size();
        Point2f p1, p2;

        p1 = polygon.get(n - 1); // Начинаем с последней вершины
        for (int i = 0; i < n; i++) {
            p2 = polygon.get(i);
            if (point.getY() > Math.min(p1.getY(), p2.getY())) {
                if (point.getY() <= Math.max(p1.getY(), p2.getY())) {
                    if (point.getX() <= Math.max(p1.getX(), p2.getX())) {
                        if (p1.getY() != p2.getY()) {
                            double xinters = (point.getY() - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()) + p1.getX();
                            if (p1.getX() == p2.getX() || point.getX() <= xinters) {
                                inside = !inside;
                            }
                        }
                    }
                }
            }
            p1 = p2;
        }
        return inside;
    }

    private static float interpolateZ(Point2f point, ArrayList<Point2f> screenPoints, ArrayList<Float> zValues) {
        if (screenPoints.size() != zValues.size()) {
            throw new IllegalArgumentException("The number of screen points and z-values must be the same.");
        }

        float totalWeight = 0;
        float weightedZ = 0;

        for (int i = 0; i < screenPoints.size(); i++) {
            Point2f p1 = screenPoints.get(i);
            Point2f p2 = screenPoints.get((i + 1) % screenPoints.size());
            float z1 = zValues.get(i);
            float z2 = zValues.get((i + 1) % zValues.size());

            float weight1 = 1.0f / (float) Math.hypot(point.getX() - p1.getX(), point.getY() - p1.getY());
            float weight2 = 1.0f / (float) Math.hypot(point.getX() - p2.getX(), point.getY() - p2.getY());

            totalWeight += weight1 + weight2;
            weightedZ += weight1 * z1 + weight2 * z2;

        }
        return weightedZ / totalWeight;

    }
    public ModelColor(){


//        // Создание Z-буфера
//        float[] zBuffer = new float[width * height];
//        for (int i = 0; i < zBuffer.length; i++) {
//            zBuffer[i] = Float.MAX_VALUE;
//        }
//
//
//        // Цвет заливки
//        Color fillColor = Color.RED;
//        System.out.println("models: " + models.size());
//
//        for (Model model : models) {
//            final int nPolygons = model.getPolygons().size();
//            System.out.println("nPolygons: " + nPolygons);
//
//            for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
//                final int nVerticesInPolygon = model.getPolygons().get(polygonInd).getVertexIndices().size();
//                System.out.println("nVerticesInPolygon: " + nVerticesInPolygon);
//
//                ArrayList<Vector3f> vertices = new ArrayList<>();
//                for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
//                    Vector3f vertex = model.getVertices().get(model.getPolygons().get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
//
//                    Vector3f point = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());
//                    vertices.add(point);
//
//                }
//
//                ArrayList<Point2f> ver = new ArrayList<>();
//                ArrayList<Float> zValues = new ArrayList<>();
//
//                for (int vertexIndex : model.getPolygons().get(polygonInd).getVertexIndices()) {
//                    Vector3f vertex = model.getVertices().get(vertexIndex);
////
//                    System.out.println("X vertex: " + vertex.getX());
//                    System.out.println("Y vertex: " + vertex.getY());
//                    Point2f screenPoint = vertexToPoint(vertex, width, height);
//                    // Теперь screenPoint содержит координаты пикселей на холсте
//                    // Вы можете использовать screenPoint для рисования на холсте
//
//                    System.out.println("X screenPoint: " + screenPoint.getX());
//                    System.out.println("Y screenPoint: " + screenPoint.getY());
//                    ver.add(screenPoint);
//
//
//
//                    Vector3f vertexVecmath = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());
//
//                    Vector3f transformedVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
//                    zValues.add(transformedVertex.getZ());
//
//
//                }
//
//
//                BoundingBox boundingBox = getBoundingBox(ver);
//
//
//
//                rasterizePolygon(graphicsContext, ver, zBuffer, fillColor, boundingBox, zValues);
//
//
//            }
//            model.setNewColorPolygons(model.getPolygons());
//
//
//        }

    }
}
