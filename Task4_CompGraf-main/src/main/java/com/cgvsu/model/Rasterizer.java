package com.cgvsu.model;



import com.cgvsu.math.AbstractMatrix;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector4f;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.GraphicConveyor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


//Класс для растеризации
public class Rasterizer {
    private final int width;
    private final int height;
    private final BufferedImage image;
    private float[][] zBuffer;
   // private Camera camera;
    private ArrayList<Vector3f> vertices;
    private final Camera camera;


    public Rasterizer(int width, int height, Camera camera, ArrayList<Vector3f> modelVertices) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.zBuffer = new float[width][height];
        this.camera = camera;
        this.vertices = modelVertices;

    }

    public static float[] getCentroid(Polygon polygon, ArrayList<Vector3f> vertices) {
        float centroidX = 0;
        float centroidY = 0;
        if (polygon.getVertexIndices().isEmpty()){
            return new float[]{0, 0};
        }
        for (int index : polygon.getVertexIndices()) {
            centroidX += vertices.get(index).getX();
            centroidY += vertices.get(index).getY();
        }
        centroidX /= polygon.getVertexIndices().size();
        centroidY /= polygon.getVertexIndices().size();
        return new float[]{centroidX, centroidY};
    }

    private void clearZBuffer() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                zBuffer[i][j] = Float.NEGATIVE_INFINITY;
            }
        }
    }
    public BufferedImage rasterizePolygon(GraphicsContext graphicsContext, Polygon pol) {
        clearZBuffer();
        BufferedImage image1 = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

        List<Integer> indices = pol.getVertexIndices();

        if(indices.size() != 3) {
            System.err.println("This rasterizer needs triangle as a polygon");
            return image;
        }
//        for (int i = 0; i < 10; i++) {
//            System.out.println("modelVertices = : " + this.vertices.get(i));
//
//        }
            float[] polygon = new float[]{
                    this.vertices.get(indices.get(0)).getX(), this.vertices.get(indices.get(0)).getY(), this.vertices.get(indices.get(0)).getZ(),
                    this.vertices.get(indices.get(1)).getX(), this.vertices.get(indices.get(1)).getY(), this.vertices.get(indices.get(1)).getZ(),
                    this.vertices.get(indices.get(2)).getX(), this.vertices.get(indices.get(2)).getY(), this.vertices.get(indices.get(2)).getZ()

            };

        //for (int i = 0; i < polygon.length; i++) {
   //         System.out.println("polygon = : " + Arrays.toString(polygon));

     //   }


        float[] projected = projectTriangle(polygon);

       // for (int i = 0; i < Objects.requireNonNull(projected).length; i++) {
   //         System.out.println("projected = : " + Arrays.toString(projected));

      //  }


        if(projected == null)
            return image;

        float minX = Math.min(projected[0], Math.min(projected[3], projected[6]));
        float maxX = Math.max(projected[0], Math.max(projected[3], projected[6]));
        float minY = Math.min(projected[1], Math.min(projected[4], projected[7]));
        float maxY = Math.max(projected[1], Math.max(projected[4], projected[7]));

        System.out.println("minX = : " + minX);
        System.out.println("maxX = : " + maxX);
        System.out.println("minY = : " + minY);
        System.out.println("maxY = : " + maxY);

        float[] centroid = getCentroid(pol, this.vertices);
        pol.setPosition(centroid[0] + (float) this.width /2, centroid[1] + (float) this.height/2);
        //  pol.setPosition(minX, minY);

//        for (int i = 0; i < projected.length; i += 3) {
//            float x = projected[i];
//            float y = projected[i + 1];
//
//            minX = Math.min(minX, x);
//            maxX = Math.max(maxX, x);
//            minY = Math.min(minY, y);
//            maxY = Math.max(maxY, y);
//        }

     //   System.out.println("До цикла");


        for (int y = (int) Math.floor(minY); y <= (int) Math.floor(maxY); y++) {
      //      System.out.println("В первом цикле");
            for (int x = (int) Math.floor(minX); x <= (int) Math.floor(maxX); x++) {
       //         System.out.println("Во втором цикле");

                if (x < 0 || x >= width || y < 0 || y >= height) {
                    continue;
                }
              //  System.out.println("      170");

                float[] barycentric = calculateBarycentricCoordinates(x, y, projected);
           //     System.out.println("      173 = " + Arrays.toString(barycentric));
//
//                if (barycentric[0] < 0 || barycentric[1] < 0 || barycentric[2] < 0) {
//                    continue;
//                }
              //  System.out.println("      178");

                float z = polygon[2] * barycentric[0] + polygon[5] * barycentric[1] + polygon[8] * barycentric[2];
           //     System.out.println("      181 = " + z);

                if (z > zBuffer[x][y]) {
                    ///System.out.println("      184");

                    zBuffer[x][y] = z;
                    Color col = new Color(1,0,0, 0.5);
                    // int colorValue = convertColorToInt(color);
                    //   image.setRGB(x, y, colorValue);

                    //    System.out.println("image.setRGB(x = : " + x);
                    //   System.out.println("image.setRGB(y = : " + y);

                    int colorValue = convertColorToInt(col);
                    //   System.out.println("colorValue = : " + colorValue);

                    image1.setRGB(x, y, colorValue);
                   // System.out.println("xxxx" + image1);

                }
            }
        }


//        for (int y = (int) Math.floor(minY); y <= (int) Math.floor(maxY); y++) {
//            for (int x = (int) Math.floor(minX); x <= (int) Math.floor(maxX); x++) {
//
//                if (x < 0 || x >= width || y < 0 || y >= height) {
//                    continue;
//                }
//                float[] barycentric = calculateBarycentricCoordinates(x, y, projected);
//                if (barycentric[0] < 0 || barycentric[1] < 0 || barycentric[2] < 0) {
//                    continue;
//                }
//                float z = polygon[2] * barycentric[0] + polygon[5] * barycentric[1] + polygon[8] * barycentric[2];
//                if (z > zBuffer[x][y]) {
//                    zBuffer[x][y] = z;
//                    Color col = new Color(1,0,0, 0.5);
//
//
//                    image.setRGB(x, y, col.hashCode());
//
//                }
//            }
//        }


        System.out.println("1x: " + pol.getPositionX());
        System.out.println("1y: " + pol.getPositionY());

        System.out.println("xxxx" + image1);

        return image1;
    }






    private int convertColorToInt(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        return (red << 16) | (green << 8) | blue;
    }

    private float[] projectTriangle(float[] polygon) {
        if (polygon.length != 9) {
            return null; // Проверка на треугольник
        }

        float[] projected = new float[9];
        // 1. Получение матрицы проекции
        Matrix4f projectionMatrix = camera.getProjectionMatrix();
        // 2. Проекция вершин
        for (int i = 0; i < 3; i++) {
            Vector3f vertex = new Vector3f(polygon[i * 3], polygon[i * 3 + 1], polygon[i * 3 + 2]);
            Vector3f projectedVertex = projectVertex(vertex, projectionMatrix);
            projected[i * 3] = projectedVertex.getX();
            projected[i * 3 + 1] = projectedVertex.getY();
            projected[i * 3 + 2] = projectedVertex.getZ();
        }
        return projected;
    }
    private Vector3f projectVertex(Vector3f vertex, Matrix4f projectionMatrix) {

        // 1. Преобразуем Vector3f в Vector4f (добавляем w = 1.0)
        Vector4f vertex4f = new Vector4f(vertex.getX(), vertex.getY(), vertex.getZ(), 1.0f);
        Matrix4f am = new Matrix4f(projectionMatrix);

        // 2. Умножаем Vector4f на матрицу проекции
        Vector4f transformed4f = am.multiply(vertex4f);

        // 3. Перспективное деление
        float w = transformed4f.getW(); // Получаем w
        if (w != 0) {
            transformed4f.setX(transformed4f.getX() / w);
            transformed4f.setY(transformed4f.getY() / w);
            transformed4f.setZ(transformed4f.getZ() / w);
        }
        // 4. Возвращаем Vector3f после деления и отбрасывания компоненты w
        return new Vector3f(transformed4f.getX(), transformed4f.getY(), transformed4f.getZ());
    }

    private float[] calculateBarycentricCoordinates(float x, float y, float[] projected) {
        float x1 = projected[0];
        float y1 = projected[1];
        float x2 = projected[3];
        float y2 = projected[4];
        float x3 = projected[6];
        float y3 = projected[7];

        float area =  Math.abs( 0.5f * ((x2 - x3) * (y1 - y3) - (x1 - x3) * (y2 - y3)));

     //   System.out.println("area = 0.5 * |(x2 - x3)(y1 - y3) - (x1 - x3)(y2 - y3)|" + Arrays.toString(projected));

        // float area = 0.5f * ((y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3));
     //   System.out.println("     area = " + area);
        if (area == 0) {
            System.err.println("Error: деление на ноль, площадь треугольника равна 0.");
            return new float[]{-1, -1, -1}; //Возвращаем некорректные значения
        }

        float w1 = 0.5f * ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / area;
        float w2 = 0.5f * ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / area;
        float w3 = 1 - w1 - w2;
     //   System.out.println("      w1 = " + w1);
      //  System.out.println("      w2 = " + w2);
      //  System.out.println("      w3 = " + w3);
        return new float[]{w1, w2, w3};
    }

   // private final TriangleRasterizer triangleRasterizer;

//    public Rasterizer(int width, int height) {
//        this.triangleRasterizer = new TriangleRasterizer(width, height);
//    }

//    public BufferedImage rasterize(GraphicsContext graphicsContext, Polygon polygon) {
//        return rasterizePolygon(graphicsContext, polygon);
//    }

    public List<BufferedImage> rasterize(GraphicsContext graphicsContext, List<Polygon> polygons) {
        List<BufferedImage> images = new ArrayList<>();
        //System.out.println("polygons.size()" +  polygons);
        if (polygons == null || polygons.isEmpty()) {
            return images;
        }
        //int n = 0;
        for (Polygon polygon : polygons) {

            System.out.println("rrrr" + polygon);
            //BufferedImage im = rasterizePolygon(graphicsContext, polygon);
//            int x = rasterizePolygon(graphicsContext, polygon).getMinX();
//            int y = rasterizePolygon(graphicsContext, polygon).getMinY();
//            System.out.println("   2x: " + x );
            //System.out.println("   2y: " + y );
          //  System.out.println("qqqq" + 344);
            images.add(rasterizePolygon(graphicsContext, polygon));
           // System.out.println("zzzz" + images);
         //   images.add(rasterizePolygon(graphicsContext, polygon));
          //  System.out.println("rasterize " + rasterizePolygon(graphicsContext, polygon));
        }

        return images;
    }
    static  public Image convertBufferedImageToImage(BufferedImage bufferedImage) {
        if (bufferedImage == null) return null;
        WritableImage wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                pw.setColor(x, y, convertAwtColorToFxColor(new java.awt.Color(bufferedImage.getRGB(x, y))));
            }
        }
        return wr;
    }
    static  public javafx.scene.paint.Color convertAwtColorToFxColor(java.awt.Color color) {
        return javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 255.0);
    }
}