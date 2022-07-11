package tokyo.nakanaka.buildvox.core.block;

import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

import java.util.HashSet;
import java.util.Set;

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

    /**
     * Gets the block transformation approximate of the linear part of the affine transformation.
     * @param trans the affine transformation. Only the linear part affects.
     */
    public static BlockTransformation approximateOf(AffineTransformation3d trans) {
        trans = trans.linear();
        Vector3d transI = trans.apply(Vector3d.PLUS_I);
        Vector3d transJ = trans.apply(Vector3d.PLUS_J);
        Vector3d transK = trans.apply(Vector3d.PLUS_K);
        Set<Vector3d> candidateSet0 = Set.of(Vector3d.PLUS_I, Vector3d.MINUS_I,
                Vector3d.PLUS_J, Vector3d.MINUS_J,
                Vector3d.PLUS_K, Vector3d.MINUS_K);
        Set<Vector3d> candidateSet = new HashSet<>(candidateSet0);
        Vector3d nk = transK.getNearestVector(candidateSet.toArray(new Vector3d[0]));
        candidateSet.remove(nk);
        candidateSet.remove(nk.scalarMultiply(-1));
        Vector3d ni = transI.getNearestVector(candidateSet.toArray(new Vector3d[0]));
        candidateSet.remove(ni);
        candidateSet.remove(ni.scalarMultiply(-1));
        Vector3d nj = transJ.getNearestVector(candidateSet.toArray(new Vector3d[0]));
        candidateSet.remove(nj);
        candidateSet.remove(nj.scalarMultiply(-1));
        int[] e = new int[]{(int) Math.round(ni.x()), (int) Math.round(nj.x()), (int) Math.round(nk.x()),
                (int) Math.round(ni.y()), (int) Math.round(nj.y()), (int) Math.round(nk.y()),
                (int) Math.round(ni.z()), (int) Math.round(nj.z()), (int) Math.round(nk.z())};
        Matrix3x3i matrix = new Matrix3x3i(e[0], e[1], e[2], e[3], e[4], e[5], e[6], e[7], e[8]);
        return new BlockTransformation(matrix);
    }

}
