package tokyo.nakanaka.buildvox.core.math.transformation;

import org.apache.commons.geometry.euclidean.threed.AffineTransformMatrix3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.rotation.QuaternionRotation;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

/**
 * Represents affine transformation for 3 dimensional space (by double).
 */
public class AffineTransformation3d {
    /**
     * Represents identity
     */
    public static final AffineTransformation3d IDENTITY
            = new AffineTransformation3d(AffineTransformMatrix3D.identity());
    private AffineTransformMatrix3D matrix;

    private AffineTransformation3d(AffineTransformMatrix3D matrix) {
        this.matrix = matrix;
    }

    /**
     * Constructs an affine transformation by its linear part and translation part.
     * @param linear a linear part of the affine transformation.
     * @param trans a translation part of the affine transformation.
     */
    public AffineTransformation3d(Matrix3x3d linear, Vector3d trans){
        double a00 = linear.element(0, 0);
        double a01 = linear.element(0, 1);
        double a02 = linear.element(0, 2);
        double a03 = trans.x();
        double a10 = linear.element(1, 0);
        double a11 = linear.element(1, 1);
        double a12 = linear.element(1, 2);
        double a13 = trans.y();
        double a20 = linear.element(2, 0);
        double a21 = linear.element(2, 1);
        double a22 = linear.element(2, 2);
        double a23 = trans.z();
        this.matrix = AffineTransformMatrix3D.of(
                a00, a01, a02, a03,
                a10, a11, a12, a13,
                a20, a21, a22, a23);
    }

    /**
     * Gets the affine transformation that the original transformation is moved to the offset point.
     * @param trans the original affine transformation.
     * @param offsetX the x-coordinate of the offset.
     * @param offsetY the y-coordinate of the offset.
     * @param offsetZ the z-coordinate of the offset.
     * @return the affine transformation that the original transformation is moved to the offset point.
     */
    public static AffineTransformation3d withOffset(AffineTransformation3d trans, double offsetX, double offsetY, double offsetZ){
        return AffineTransformation3d.ofTranslation(offsetX, offsetY, offsetZ)
                .compose(trans)
                .compose(AffineTransformation3d.ofTranslation(-offsetX, -offsetY, -offsetZ));
    }

    /**
     * Gets the affine transformation that the original transformation is moved to the offset point.
     * @param trans the original affine transformation.
     * @param offset offset.
     * @return the affine transformation that the original transformation is moved to the offset point.
     */
    public static AffineTransformation3d withOffset(AffineTransformation3d trans, Vector3d offset) {
        return withOffset(trans, offset.x(), offset.y(), offset.z());
    }

    /**
     * Gets a translation that is given by the displacement.
     * @param dx the x-component of the displacement.
     * @param dy the y-component of the displacement.
     * @param dz the z-component of the displacement.
     * @return a translation that is given by the displacement.
     */
    public static AffineTransformation3d ofTranslation(double dx, double dy, double dz){
        var m = AffineTransformMatrix3D.createTranslation(dx, dy, dz);
        return new AffineTransformation3d(m);
    }

    /**
     * Gets a scale transformation
     * @param factorX a x factor
     * @param factorY a y factor
     * @param factorZ a z factor
     * @return a scale transformation
     */
    public static AffineTransformation3d ofScale(double factorX, double factorY, double factorZ){
        var m = AffineTransformMatrix3D.createScale(factorX, factorY, factorZ);
        return new AffineTransformation3d(m);
    }

    /**
     * Creates the instance which represents the rotation around x-axis.
     * @param angle the angle of rotation by radian. The right-hand rule is applied.
     * @return the instance which represents the rotation around x-axis.
     */
    public static AffineTransformation3d ofRotationX(double angle){
        QuaternionRotation qrot = QuaternionRotation.fromAxisAngle(Vector3D.of(1, 0, 0), angle);
        AffineTransformMatrix3D m = AffineTransformMatrix3D.createRotation(Vector3D.of(0, 0, 0), qrot);
        return new AffineTransformation3d(m);
    }

    /**
     * Creates the instance which represents the rotation around y-axis.
     * @param angle the angle of rotation by radian. The right-hand rule is applied.
     * @return the instance which represents the rotation around y-axis.
     */
    public static AffineTransformation3d ofRotationY(double angle){
        QuaternionRotation qrot = QuaternionRotation.fromAxisAngle(Vector3D.of(0, 1, 0), angle);
        AffineTransformMatrix3D m = AffineTransformMatrix3D.createRotation(Vector3D.of(0, 0, 0), qrot);
        return new AffineTransformation3d(m);
    }

    /**
     * Creates the instance which represents the rotation around z-axis.
     * @param angle the angle of rotation by radian. The right-hand rule is applied.
     * @return the instance which represents the rotation around z-axis.
     */
    public static AffineTransformation3d ofRotationZ(double angle){
        QuaternionRotation qrot = QuaternionRotation.fromAxisAngle(Vector3D.of(0, 0, 1), angle);
        AffineTransformMatrix3D m = AffineTransformMatrix3D.createRotation(Vector3D.of(0, 0, 0), qrot);
        return new AffineTransformation3d(m);
    }

    /**
     * Gets a linear transformation of shear transformation which does not transform x-component of any vector.
     * This method returns a linear transformation which is represented by the following matrix. <br>
     * |   1    0 0| <br>
     * |factorY 1 0| <br>
     * |factorZ 0 1| <br>
     * @param factorY the factorY
     * @param factorZ the factorZ
     * @return a new instance
     */
    public static AffineTransformation3d ofShearX(double factorY, double factorZ) {
        var m = AffineTransformMatrix3D
                .of(1, 0, 0, 0, factorY, 1, 0, 0, factorZ, 0, 1, 0);
        return new AffineTransformation3d(m);
    }

    /**
     * Gets a linear transformation of shear transformation which does not transform y-component of any vector.
     * This method returns a linear transformation which is represented by the following matrix. <br>
     * |1 factorX 0| <br>
     * |0    1    0| <br>
     * |0 factorZ 1| <br>
     * @param factorZ the factorZ
     * @param factorX the factorX
     * @return a new instance
     */
    public static AffineTransformation3d ofShearY(double factorZ, double factorX) {
        var m = AffineTransformMatrix3D
                .of(1, factorX, 0, 0, 0, 1, 0, 0, 0, factorZ, 1, 0);
        return new AffineTransformation3d(m);
    }

    /**
     * Gets a linear transformation of shear transformation which does not transform z-component of any vector.
     * This method returns a linear transformation which is represented by the following matrix. <br>
     * |1 0 factorX| <br>
     * |0 1 factorY| <br>
     * |0 0    1   | <br>
     * @param factorX the factorX
     * @param factorY the factorY
     * @return a new instance
     */
    public static AffineTransformation3d ofShearZ(double factorX, double factorY) {
        var m = AffineTransformMatrix3D
                .of(1, 0, factorX, 0, 0, 1, factorY, 0, 0, 0, 1, 0);
        return new AffineTransformation3d(m);
    }

    /**
     * Apply a vector
     * @param v the vector to apply
     * @return a new vector
     */
    public Vector3d apply(Vector3d v){
        var pt = Vector3D.of(v.x(), v.y(), v.z());
        Vector3D transPt = this.matrix.apply(pt);
        return new Vector3d(transPt.getX(), transPt.getY(), transPt.getZ());
    }

    /**
     * Apply a vector
     * @param x x-coordinate of the vector to apply
     * @param y y-coordinate of the vector to apply
     * @param z z-coordinate of the vector to apply
     * @return a new vector
     */
    public Vector3d apply(double x, double y, double z){
        return this.apply(new Vector3d(x, y, z));
    }

    /**
     * Returns the linear part of this affine transformation.
     * @return the linear part of this affine transformation.
     */
    public AffineTransformation3d linear(){
        AffineTransformMatrix3D m = this.matrix.linear();
        return new AffineTransformation3d(m);
    }

    /**
     * Gets the inverse of this affine transformation
     * @return the inverse of this affine transformation
     * @throws SingularException if this cannot be inverted.
     */
    public AffineTransformation3d inverse(){
        AffineTransformMatrix3D m;
        try {
            m = this.matrix.inverse();
        }catch (IllegalStateException e){
            throw new SingularException();
        }
        return new AffineTransformation3d(m);
    }

    /**
     * Compose another affine transformation
     * @param another another affine transformation
     * @return a new instance
     */
    public AffineTransformation3d compose(AffineTransformation3d another){
        AffineTransformMatrix3D m = this.matrix.multiply(another.matrix);
        return new AffineTransformation3d(m);
    }

}
