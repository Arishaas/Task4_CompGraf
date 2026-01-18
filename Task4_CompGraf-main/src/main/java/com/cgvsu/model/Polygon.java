package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Polygon {

    private ArrayList<Integer> vertexIndices;
    private ArrayList<Integer> textureVertexIndices;
    private ArrayList<Integer> normalIndices;
    private Color color;
    private float x;
    private float y;
    private ArrayList<Vector2f> textureCoordinates;
    public Polygon() {
        vertexIndices = new ArrayList<Integer>();
        textureVertexIndices = new ArrayList<Integer>();
        normalIndices = new ArrayList<Integer>();
        textureCoordinates = new ArrayList<>();
        this.x = 0;
        this.y = 0;
    }
    public void setTextureCoordinates(ArrayList<Vector2f> textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }

//    public ArrayList<Vector2f> getTextureCoordinates() {
//        ArrayList<Vector2f> coords = new ArrayList<>();
//        for (Integer index : textureVertexIndices) {
//            coords.add(textureCoordinates.get(index)); // Получаем текстурные координаты по индексу
//        }
//        retuIndex 4 out of bounds for length 4rn coords;
//    }

//    public ArrayList<Vector2f> getTextureCoordinates() {
//        System.out.println("textureCoordinates = "+ textureCoordinates);
//        System.out.println("textureVertexIndices = "+ textureVertexIndices);
//
//
//        ArrayList<Vector2f> coords = new ArrayList<>();
//        for (Integer index : textureVertexIndices) {
//            // Проверяем, что индекс в пределах допустимого диапазона
//
//            System.out.println("index = "+ index);
//            System.out.println("textureCoordinates.size() = "+ textureCoordinates.size());
//
//
//
//            if (index >= 0 && index <= textureCoordinates.size()) {
//                coords.add(textureCoordinates.get(index)); // Получаем текстурные координаты по индексу
//            } else {
//                // Обработка ошибки, если индекс выходит за пределы
//                throw new IndexOutOfBoundsException("Texture index " + index + " out of bounds for length " + textureCoordinates.size());
//            }
//        }
//        return coords;
//    }

    public ArrayList<Vector2f> getTextureCoordinates() {
    //    System.out.println("textureCoordinates = " + textureCoordinates);
    //    System.out.println("textureVertexIndices = " + textureVertexIndices);
        int n = textureCoordinates.size();
        ArrayList<Vector2f> coords = new ArrayList<>();
       // for (Integer index : textureVertexIndices) {
        for (int index = 0; index<n; index++){
            // Проверяем, что индекс в пределах допустимого диапазона
         //   System.out.println("index = " + index);
         //   System.out.println("textureCoordinates.size() = " + textureCoordinates.size());

            if (index >= 0 && index < textureCoordinates.size()) { // Изменено на < вместо <=
                coords.add(textureCoordinates.get(index)); // Получаем текстурные координаты по индексу
            } else {
                // Обработка ошибки, если индекс выходит за пределы
                throw new IndexOutOfBoundsException("Texture index " + index + " out of bounds for length " + textureCoordinates.size());
            }
        }
        return coords;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public float getPositionX() {
        return x;
    }
    public float getPositionY() {
        return y;
    }
    public void setVertexIndices(ArrayList<Integer> vertexIndices) {
        assert vertexIndices.size() >= 3;
        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {
        assert textureVertexIndices.size() >= 3;
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {
        assert normalIndices.size() >= 3;
        this.normalIndices = normalIndices;
    }

    public void setColor(Color color_new) {
        this.color = color_new;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public ArrayList<Integer> getNormalIndices() {
        return normalIndices;
    }
}
