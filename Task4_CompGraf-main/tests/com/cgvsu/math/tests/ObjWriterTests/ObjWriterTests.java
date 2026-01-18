package com.cgvsu.ObjWriterTests;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.objWriter.ObjWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ObjWriterTests {
    private final ObjWriter objWriter = new ObjWriter();
    @Test
    public void testVertexToString() {
        Vector3f vector = new Vector3f(1.0f, -2.0f, 0.5f);
        String result = objWriter.vertexToString(vector);

        Assertions.assertEquals("v 1.0 -2.0 0.5", result, "Vertex string should be formatted correctly");
    }

    @Test
    public void testTextureVertexToString() {
        Vector2f vector = new Vector2f(0.3f, -0.7f);
        String result = objWriter.textureVertexToString(vector);

        Assertions.assertEquals("vt 0.3 -0.7", result, "Texture vertex string should be formatted correctly");
    }

    @Test
    public void testNormalToString() {
        Vector3f vector = new Vector3f(0.0f, 1.0f, -1.0f);
        String result = objWriter.normalToString(vector);

        Assertions.assertEquals("vn 0.0 1.0 -1.0", result, "Normal string should be formatted correctly");
    }

    @Test
    public void testPolygonToStringWithVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));

        String result = objWriter.polygonToString(polygon);

        Assertions.assertEquals("f 1 2 3", result, "Polygon string should contain only vertex indices");
    }

    @Test
    public void testPolygonToStringWithTextureIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 5)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(3, 5, 4, 2)));

        String result = objWriter.polygonToString(polygon);

        Assertions.assertEquals("f 1/4 2/6 3/5 6/3", result, "Polygon string should include texture indices");
    }

    @Test
    public void testWrite() throws IOException {
        Model model = new Model();
        model.setVertices(new ArrayList<>(List.of(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1.2f, 3.6f),
                new Vector3f(-2, -4.45f, 7f),
                new Vector3f(-1.5f, -4.45f, 6.5f),
                new Vector3f(10f, 11f, 0)
        )));

        Polygon polygon1 = new Polygon();
        Polygon polygon2 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(List.of(0, 1, 3)));
        polygon2.setVertexIndices(new ArrayList<>(List.of(2, 4, 3)));

        model.setPolygons(new ArrayList<>(List.of(polygon1, polygon2)));

        String testFilename = "test.obj";
        objWriter.write(model, testFilename, true);

        Path path = Path.of(testFilename);
        String expectedContent = String.join(System.lineSeparator(),
                "v 0.0 0.0 0.0",
                "v 1.0 1.2 3.6",
                "v -2.0 -4.45 7.0",
                "v -1.5 -4.45 6.5",
                "v 10.0 11.0 0.0",
                "f 1 2 4",
                "f 3 5 4"
        ) + System.lineSeparator();

        String actualContent = Files.readString(path, StandardCharsets.UTF_8);

        Assertions.assertEquals(expectedContent, actualContent, "File content should match expected OBJ structure");

        Files.deleteIfExists(path);
    }
}