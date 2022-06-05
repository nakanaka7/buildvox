package tokyo.nakanaka.buildvox.core.edit;

import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockTransformation;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3d;
import tokyo.nakanaka.buildvox.core.math.transformation.Matrix3x3i;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;

import java.util.HashSet;
import java.util.Set;

class BlockTransformApproximator {
    private static int[] approximate(AffineTransformation3d trans){
        trans = trans.linear();
        Vector3d transI = trans.apply(Vector3d.PLUS_I);
        Vector3d transJ = trans.apply(Vector3d.PLUS_J);
        Vector3d transK = trans.apply(Vector3d.PLUS_K);
        Set<Vector3d> candidateSet0 = Set.of(Vector3d.PLUS_I, Vector3d.MINUS_I,
                Vector3d.PLUS_J, Vector3d.MINUS_J,
                Vector3d.PLUS_K, Vector3d.MINUS_K);
        Set<Vector3d> candidateSet = new HashSet<>(candidateSet0);
        Vector3d nk = getNearestVector(transK, candidateSet.toArray(new Vector3d[0]));
        candidateSet.remove(nk);
        candidateSet.remove(nk.scalarMultiply(-1));
        Vector3d ni = getNearestVector(transI, candidateSet.toArray(new Vector3d[0]));
        candidateSet.remove(ni);
        candidateSet.remove(ni.scalarMultiply(-1));
        Vector3d nj = getNearestVector(transJ, candidateSet.toArray(new Vector3d[0]));
        candidateSet.remove(nj);
        candidateSet.remove(nj.scalarMultiply(-1));
        return new int[]{(int)Math.round(ni.x()), (int)Math.round(nj.x()), (int)Math.round(nk.x()),
                (int)Math.round(ni.y()), (int)Math.round(nj.y()), (int)Math.round(nk.y()),
                (int)Math.round(ni.z()), (int)Math.round(nj.z()), (int)Math.round(nk.z())};
    }

    static BlockTransformation approximateToBlockTrans(AffineTransformation3d trans) {
        int[] e = approximate(trans);
        Matrix3x3i matrix = new Matrix3x3i(e[0], e[1], e[2], e[3], e[4], e[5], e[6], e[7], e[8]);
        return new BlockTransformation(matrix);
    }

    private static AffineTransformation3d approximateToTrans(AffineTransformation3d trans){
        int[] a = approximate(trans);
        Matrix3x3d matrix = new Matrix3x3d(a[0], a[1], a[2],
                a[3], a[4], a[5],
                a[6], a[7], a[8]);
        return new AffineTransformation3d(matrix, Vector3d.ZERO);
    }

    private static Vector3d getNearestVector(Vector3d v, Vector3d... candidates){
        if(candidates.length == 0)throw new IllegalArgumentException();
        Vector3d nearest = candidates[0];
        double dis = v.distance(candidates[0]);
        for(Vector3d c : candidates){
            double disVc = v.distance(c);
            if(disVc < dis){
                nearest = c;
                dis = disVc;
            }
        }
        return nearest;
    }

}
