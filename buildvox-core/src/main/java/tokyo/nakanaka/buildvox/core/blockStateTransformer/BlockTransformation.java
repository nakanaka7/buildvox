package tokyo.nakanaka.buildvox.core.blockStateTransformer;

import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;

/**
 * Represents a transformation to apply blocks. The transformation is some combinations of the 90 degrees
 * rotations and reflections about x, y, z-axis.
 */
public class BlockTransformation {
    private Matrix3x3i matrix;

    /**
     * Constructs a block transformation from the matrix.
     * @param matrix a matrix which represents the block transformation.
     * @throws IllegalArgumentException if the given matrix does not represent a block transformation.
     */
    public BlockTransformation(Matrix3x3i matrix) {
        this.matrix = matrix;
        if(matrix.determinant() != 1 && matrix.determinant() != -1) {
            throw new IllegalArgumentException("Determinant must be 1 or -1.");
        }
    }

    /**
     * Gets a matrix which represents this block transformation.
     * @return a matrix which represents this block transformation.
     */
    public Matrix3x3i toMatrix3x3i() {
        return matrix;
    }

}
