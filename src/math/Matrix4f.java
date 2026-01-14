package math;

public class Matrix4f {
    public float[][] m = new float[4][4];

    public Matrix4f() { setIdentity(); }

    public Matrix4f(float[][] values) {
        for (int i=0;i<4;i++)
            System.arraycopy(values[i],0,m[i],0,4);
    }

    public void setIdentity() {
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                m[i][j] = (i==j ? 1 : 0);
    }

    public static Matrix4f identity() { return new Matrix4f(); }

    public Matrix4f mul(Matrix4f o) {
        Matrix4f r = new Matrix4f();
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++){
                r.m[i][j]=0;
                for(int k=0;k<4;k++)
                    r.m[i][j] += m[i][k] * o.m[k][j];
            }
        return r;
    }

    public Vector4f mul(Vector4f v) {
        float[] r = new float[4];
        float[] vec = {v.x, v.y, v.z, v.w};
        for(int i=0;i<4;i++){
            r[i]=0;
            for(int j=0;j<4;j++)
                r[i] += m[i][j] * vec[j];
        }
        return new Vector4f(r[0], r[1], r[2], r[3]);
    }

    public Matrix4f transpose() {
        Matrix4f r = new Matrix4f();
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                r.m[i][j] = m[j][i];
        return r;
    }

    public static Matrix4f translation(float x, float y, float z) {
        Matrix4f r = identity();
        r.m[0][3] = x;
        r.m[1][3] = y;
        r.m[2][3] = z;
        return r;
    }

    public static Matrix4f scale(float x, float y, float z) {
        Matrix4f r = identity();
        r.m[0][0] = x;
        r.m[1][1] = y;
        r.m[2][2] = z;
        return r;
    }

    public static Matrix4f rotationX(float a) {
        Matrix4f r = identity();
        float c=(float)Math.cos(a), s=(float)Math.sin(a);
        r.m[1][1]=c; r.m[1][2]=-s;
        r.m[2][1]=s; r.m[2][2]=c;
        return r;
    }

    public static Matrix4f rotationY(float a) {
        Matrix4f r = identity();
        float c=(float)Math.cos(a), s=(float)Math.sin(a);
        r.m[0][0]=c; r.m[0][2]=s;
        r.m[2][0]=-s; r.m[2][2]=c;
        return r;
    }

    public static Matrix4f rotationZ(float a) {
        Matrix4f r = identity();
        float c=(float)Math.cos(a), s=(float)Math.sin(a);
        r.m[0][0]=c; r.m[0][1]=-s;
        r.m[1][0]=s; r.m[1][1]=c;
        return r;
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f z = eye.sub(target).normalize();
        Vector3f x = up.cross(z).normalize();
        Vector3f y = z.cross(x);

        Matrix4f r = identity();
        r.m[0][0]=x.x; r.m[0][1]=x.y; r.m[0][2]=x.z;
        r.m[1][0]=y.x; r.m[1][1]=y.y; r.m[1][2]=y.z;
        r.m[2][0]=z.x; r.m[2][1]=z.y; r.m[2][2]=z.z;

        r.m[0][3] = -x.dot(eye);
        r.m[1][3] = -y.dot(eye);
        r.m[2][3] = -z.dot(eye);

        return r;
    }

    public static Matrix4f perspective(float fov, float aspect, float near, float far) {
        Matrix4f r = new Matrix4f();
        float f = 1.0f / (float)Math.tan(fov/2.0f);
        r.m[0][0]=f/aspect;
        r.m[1][1]=f;
        r.m[2][2]=(far+near)/(near-far);
        r.m[2][3]=(2*far*near)/(near-far);
        r.m[3][2]=-1;
        r.m[3][3]=0;
        return r;
    }
}
