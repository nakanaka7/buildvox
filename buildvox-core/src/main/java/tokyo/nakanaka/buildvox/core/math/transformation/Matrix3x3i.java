package tokyo.nakanaka.buildvox.core.math.transformation;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.Objects;

/**
 * Represents a 3x3 matrix which elements are int.
 */
public class Matrix3x3i {
    /**
     * Represents an identity matrix.
     */
    @SuppressWarnings("unused")
    public static final Matrix3x3i IDENTITY = new Matrix3x3i(1, 0, 0, 0, 1, 0, 0, 0, 1);
    private int e00;
    private int e01;
    private int e02;
    private int e10;
    private int e11;
    private int e12;
    private int e20;
    private int e21;
    private int e22;

    /**
     * Constructs the following 3 x 3 matrix <br>
     * |e00 e01 e02| <br>
     * |e10 e11 e12| <br>
     * |e20 e21 e22| <br>
     */
    public Matrix3x3i(int e00, int e01, int e02, int e10, int e11, int e12, int e20, int e21, int e22) {
        this.e00 = e00;
        this.e01 = e01;
        this.e02 = e02;
        this.e10 = e10;
        this.e11 = e11;
        this.e12 = e12;
        this.e20 = e20;
        this.e21 = e21;
        this.e22 = e22;
    }

    /**
     * Constructs a matrix by the 2 dimensional array. The array must be 3x3.
     * @param matrix the 2 dimensional array which represents the 3x3 matrix.
     */
    @SuppressWarnings("unused")
    public Matrix3x3i(int[][] matrix){
        this.e00 = matrix[0][0];
        this.e01 = matrix[0][1];
        this.e02 = matrix[0][2];
        this.e10 = matrix[1][0];
        this.e11 = matrix[1][1];
        this.e12 = matrix[1][2];
        this.e20 = matrix[2][0];
        this.e21 = matrix[2][1];
        this.e22 = matrix[2][2];
    }

    /**
     * Apply a {@link Vector3i}
     * @param v the applied vector.
     * @return the outcome vector(= (this matrix) * v)
     */
    public Vector3i apply(Vector3i v) {
        int x = v.x();
        int y = v.y();
        int z = v.z();
        int sx = e00 * x + e01 * y + e02 * z;
        int sy = e10 * x + e11 * y + e12 * z;
        int sz = e20 * x + e21 * y + e22 * z;
        return new Vector3i(sx, sy, sz);
    }

    /**
     * Multiplies another matrix.
     * @param m the another matrix.
     * @return the outcome matrix(= (this matrix) * m)
     */
    @SuppressWarnings("unused")
    public Matrix3x3i multiply(Matrix3x3i m) {
        Vector3i a0 = new Vector3i(m.e00, m.e10, m.e20);
        Vector3i a1 = new Vector3i(m.e01, m.e11, m.e21);
        Vector3i a2 = new Vector3i(m.e02, m.e12, m.e22);
        Vector3i b0 = apply(a0);
        Vector3i b1 = apply(a1);
        Vector3i b2 = apply(a2);
        return new Matrix3x3i(b0.x(), b1.x(), b2.x(), b0.y(), b1.y(), b2.y(), b0.z(), b1.z(), b2.z());
    }

    /**
     * Gets the determinant of this matrix.
     * @return the determinant of this matrix.
     */
    public int determinant() {
        return e00 * e11 * e22 + e01 * e12 * e20 + e02 * e10 * e21
                - e00 * e12 * e21 - e01 * e10 * e22 - e02 * e11 * e20;
    }

    /**
     * Converts the instance to {@link Matrix3x3d}.
     * @return the Matrix3x3 which elements are the same as this instance.
     */
    @SuppressWarnings("unused")
    public Matrix3x3d toMatrix3x3d() {
        return new Matrix3x3d(e00, e01, e02, e10, e11, e12, e20, e21, e22);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix3x3i that = (Matrix3x3i) o;
        return e00 == that.e00 && e01 == that.e01 && e02 == that.e02 && e10 == that.e10 && e11 == that.e11 && e12 == that.e12 && e20 == that.e20 && e21 == that.e21 && e22 == that.e22;
    }

    @Override
    public int hashCode() {
        return Objects.hash(e00, e01, e02, e10, e11, e12, e20, e21, e22);
    }

}
