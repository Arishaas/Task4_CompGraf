package math;

public class Transform {
    public Vector3f position = new Vector3f(0, 0, 0);
    public Vector3f rotation = new Vector3f(0, 0, 0); // в радианах
    public Vector3f scale    = new Vector3f(1, 1, 1);

    public Matrix4f getModelMatrix() {
        return Matrix4f.translation(position.x, position.y, position.z)
                .mul(Matrix4f.rotationZ(rotation.z))
                .mul(Matrix4f.rotationY(rotation.y))
                .mul(Matrix4f.rotationX(rotation.x))
                .mul(Matrix4f.scale(scale.x, scale.y, scale.z));
    }
}
