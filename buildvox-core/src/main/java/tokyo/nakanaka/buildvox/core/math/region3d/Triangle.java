package tokyo.nakanaka.buildvox.core.math.region3d;

import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3d;
import tokyo.nakanaka.buildvox.core.math.transformation.SingularException;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

/**
 * Represents a triangle
 */
public class Triangle implements Region3d{
    private static BaseTriangle3d baseTriangle = new BaseTriangle3d();
    private boolean empty;
    private AffineTransformation3d invTrans;

    /**
     * @param x1 x-coordinate of the position 1
     * @param y1 y-coordinate of the position 1
     * @param z1 z-coordinate of the position 1
     * @param x2 x-coordinate of the position 2
     * @param y2 y-coordinate of the position 2
     * @param z2 z-coordinate of the position 2
     * @param x3 x-coordinate of the position 3
     * @param y3 y-coordinate of the position 3
     * @param z3 z-coordinate of the position 3
     * @param thickness the thickness of the triangle
     * @throws IllegalArgumentException if thickness < 0.
     */
    public Triangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double thickness) {
        if(thickness < 0){
            throw new IllegalArgumentException();
        }
        Vector3d vec1 = new Vector3d(x1, y1, z1);
        Vector3d vec2 = new Vector3d(x2, y2, z2);
        Vector3d vec3 = new Vector3d(x3, y3, z3);
        Vector3d vec21 = vec2.subtract(vec1);
        Vector3d vec31 = vec3.subtract(vec1);
        Vector3d vec21x31 = vec21.crossProduct(vec31);
        Vector3d vecH;
        try {
            vecH = vec21x31.normalize().scalarMultiply(thickness);
        }catch (ArithmeticException e){
            this.empty = true;
            return;
        }
        Matrix3x3d a = new Matrix3x3d(
                vec21.x(), vec31.x(), vecH.x(),
                vec21.y(), vec31.y(), vecH.y(),
                vec21.z(), vec31.z(), vecH.z());
        AffineTransformation3d trans = new AffineTransformation3d(a, vec1);
        try {
            this.invTrans = trans.inverse();
        }catch (SingularException e){
            this.empty = true;
        }
    }

    @Override
    public boolean contains(double x, double y, double z) {
        if(this.empty)return false;
        Vector3d p = this.invTrans.apply(new Vector3d(x, y, z));
        return baseTriangle.contains(p.x(), p.y(), p.z());
    }

    private static class BaseTriangle3d implements Region3d{
        @Override
        public boolean contains(double x, double y, double z) {
            return x >= 0 && y >= 0 && x + y <= 1 && Math.abs(z) <= 0.5;
        }
    }

}
