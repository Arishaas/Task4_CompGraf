//package com.cgvsu.math.tests.Affine;
//
//import com.cgvsu.math.Matrix4f;
//import com.cgvsu.math.Quaternion;
//import org.junit.jupiter.api.Test;
//
//import static com.cgvsu.render_engine.AffineTransformations.quaternionToMatrix;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class QuaternionTest {
//
//    @Test
//    public void testQuaternionMultiplication() {
//        Quaternion q1 = new Quaternion(1, 0, 0, 0);
//        Quaternion q2 = new Quaternion(0, 1, 0, 0);
//        q1.multiply(q2);
//        assertEquals(0, q1.getW(), 0.0001);
//        assertEquals(1, q1.getX(), 0.0001);
//        assertEquals(0, q1.getY(), 0.0001);
//        assertEquals(0, q1.getZ(), 0.0001);
//    }
//
//    @Test
//    public void testMultiplyByNull() {
//        Quaternion a = new Quaternion(1, 2, 3, 4);
//
//        a.multiply(null);
//
//    }
//
//        @Test
//    public void testQuaternionNormalization() {
//        Quaternion q = new Quaternion(1, 1, 1, 1);
//        q.normalize();
//        float norm = (float) Math.sqrt(q.getX()*q.getX() + q.getY()*q.getY() + q.getZ()*q.getZ() + q.getW()*q.getW());
//        assertEquals(1.0f, norm, 0.0001);
//    }
//
//    @Test
//    public void testQuaternionToMatrix() {
//        Quaternion q = new Quaternion((float)Math.cos(Math.toRadians(45)/2),
//                (float)Math.sin(Math.toRadians(45)/2),
//                0,
//                0); // rotation of 45 degrees around x-axis
//        Matrix4f rotationMatrix = quaternionToMatrix(q);
//        // Expected rotation matrix for 45 degrees around x-axis
//        float[][] expected = {
//                {1, 0, 0, 0},
//                {0, (float)Math.cos(Math.toRadians(45)), (float)Math.sin(Math.toRadians(45)), 0},
//                {0, -(float)Math.sin(Math.toRadians(45)), (float)Math.cos(Math.toRadians(45)), 0},
//                {0, 0, 0, 1}
//        };
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                assertEquals(expected[i][j], rotationMatrix.getElement(i, j), 0.0001);
//            }
//        }
//    }
//
//    @Test
//    public void testQuaternionToMatrixNoRotation() {
//        Quaternion q = new Quaternion(0, 0, 0, 1); // no rotation
//        Matrix4f rotationMatrix = quaternionToMatrix(q);
//        Matrix4f identityMatrix = new Matrix4f(1);
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                assertEquals(identityMatrix.getElement(i, j), rotationMatrix.getElement(i, j), 0.0001);
//            }
//        }
//    }
//
//    @Test
//    public void testQuaternionConstructor() {
//        Quaternion q = new Quaternion(1, 2, 3, 4);
//        assertEquals(1, q.getW(), 0.0001);
//        assertEquals(2, q.getX(), 0.0001);
//        assertEquals(3, q.getY(), 0.0001);
//        assertEquals(4, q.getZ(), 0.0001);
//    }
//
//    @Test
//    public void testQuaternionToMatrix90DegreeXRotation() {
//        double angleRad = Math.toRadians(90);
//        Quaternion q = new Quaternion((float)Math.sin(angleRad/2), 0, 0, (float)Math.cos(angleRad/2));
//        Matrix4f rotationMatrix = quaternionToMatrix(q);
//        float[][] expected = {
//                {0, 1, 0, 0},
//                {-1, 0, 0, 0},
//                {0, 0, 1, 0},
//                {0, 0, 0, 1}
//        };
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                assertEquals(expected[i][j], rotationMatrix.getElement(i, j), 0.0001);
//            }
//        }
//    }
//
//    @Test
//    public void testQuaternionHashcode() {
//        Quaternion q0 = new Quaternion(1, 2, 3, 4);
//        Quaternion q1 = new Quaternion(1, 2, 3, 4);
//        assertEquals(q0.hashCode(), q1.hashCode());
//    }
//}