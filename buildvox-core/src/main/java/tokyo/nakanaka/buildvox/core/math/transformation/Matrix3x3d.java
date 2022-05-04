package tokyo.nakanaka.buildvox.core.math.transformation;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

/**
 * Represents a 3x3 matrix which elements are double.
 */
public class Matrix3x3d {
    /**
     * Represents an identity matrix.
     */
    @SuppressWarnings("unused")
    public static final Matrix3x3d IDENTITY = new Matrix3x3d(1, 0, 0, 0, 1, 0, 0, 0, 1);
    private double e00;
    private double e01;
    private double e02;
    private double e10;
    private double e11;
    private double e12;
    private double e20;
    private double e21;
    private double e22;

    /**
     Represents the following 3 x 3 matrix <br>
     |e00 e01 e02| <br>
     |e10 e11 e12| <br>
     |e20 e21 e22| <br>
     */
    public Matrix3x3d(double e00, double e01, double e02, double e10, double e11, double e12, double e20, double e21,
                      double e22) {
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
     * Gets an element of the matrix of the given row and column indexes
     * @param rowIndex the row index
     * @param columnIndex the column index
     * @throws IllegalArgumentException if index < 0, or 2 < index
     * @return an element of the matrix of the given row and column indexes
     */
    public double element(int rowIndex, int columnIndex){
        if(rowIndex == 0 && columnIndex == 0)return e00;
        if(rowIndex == 0 && columnIndex == 1)return e01;
        if(rowIndex == 0 && columnIndex == 2)return e02;
        if(rowIndex == 1 && columnIndex == 0)return e10;
        if(rowIndex == 1 && columnIndex == 1)return e11;
        if(rowIndex == 1 && columnIndex == 2)return e12;
        if(rowIndex == 2 && columnIndex == 0)return e20;
        if(rowIndex == 2 && columnIndex == 1)return e21;
        if(rowIndex == 2 && columnIndex == 2)return e22;
        throw new IllegalArgumentException();
    }

    /**
     * Apply a vector
     * @param v the vector to apply
     * @return a new vector
     */
    public Vector3d apply(Vector3d v) {
        double x = v.x();
        double y = v.y();
        double z = v.z();
        double sx = e00 * x + e01 * y + e02 * z;
        double sy = e10 * x + e11 * y + e12 * z;
        double sz = e20 * x + e21 * y + e22 * z;
        return new Vector3d(sx, sy, sz);
    }

}
