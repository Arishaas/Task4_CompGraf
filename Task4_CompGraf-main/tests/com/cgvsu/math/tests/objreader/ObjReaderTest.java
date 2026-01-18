package com.cgvsu.objreader;

import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class ObjReaderTest {

    @Test
    public void testParseVertex01() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        Vector3f result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.03f);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex02() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.01", "1.02", "1.03"));
        Vector3f result = ObjReader.parseVertex(wordsInLineWithoutToken, 5);
        Vector3f expectedResult = new Vector3f(1.01f, 1.02f, 1.10f);
        Assertions.assertFalse(result.equals(expectedResult));
    }

    @Test
    public void testParseVertex03() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("ab", "o", "ba"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Failed to parse float value.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseVertex04() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0"));
        try {
            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Too few vertex arguments.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    //TODO хаха смешно. -_-
//    @Test
//    public void testParseVertex05() {
//        // АГААА! Вот тест, который говорит, что у метода нет проверки на более, чем 3 числа
//        // А такой случай лучше не игнорировать, а сообщать пользователю, что у него что-то не так
//        // ассерт, чтобы не забыть про тест:
//        Assertions.fail();
//
//
//        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0", "2.0", "3.0", "4.0"));
//        try {
//            ObjReader.parseVertex(wordsInLineWithoutToken, 10);
//        } catch (ObjReaderException exception) {
//            String expectedError = "";
//            Assertions.assertEquals(expectedError, exception.getMessage());
//        }
//    }

    @Test
    public void testParseTextureVertex02() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("ab", "o"));
        try {
            ObjReader.parseTextureVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Failed to parse float value.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseTextureVertex03() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("1.0"));
        try {
            ObjReader.parseTextureVertex(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Too few texture vertex arguments.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseNormal01() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("0.0", "1.0", "0.0"));
        Vector3f result = ObjReader.parseNormal(wordsInLineWithoutToken, 5);
        Vector3f expectedResult = new Vector3f(0.0f, 1.0f, 0.0f);
        Assertions.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void testParseNormal02() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("a", "b", "c"));
        try {
            ObjReader.parseNormal(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Failed to parse float value.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }

    @Test
    public void testParseNormal03() {
        ArrayList<String> wordsInLineWithoutToken = new ArrayList<>(Arrays.asList("0.0", "1.0"));
        try {
            ObjReader.parseNormal(wordsInLineWithoutToken, 10);
        } catch (ObjReaderException exception) {
            String expectedError = "Error parsing OBJ file on line: 10. Too few normal arguments.";
            Assertions.assertEquals(expectedError, exception.getMessage());
        }
    }
}