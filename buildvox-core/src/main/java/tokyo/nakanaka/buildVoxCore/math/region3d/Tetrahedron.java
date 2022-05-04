package tokyo.nakanaka.buildVoxCore.math.region3d;

import tokyo.nakanaka.buildVoxCore.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildVoxCore.math.transformation.Matrix3x3d;
import tokyo.nakanaka.buildVoxCore.math.transformation.SingularException;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;

/**
 * Represents a tetrahedron
 */
public class Tetrahedron implements Region3d {
    private static BaseTetrahedron baseTetra = new BaseTetrahedron();
    private boolean empty;
    private AffineTransformation3d invTrans;

    /**
     * Constructs a tetrahedron made by 4 points
     * @param x1 the x-coordinate of position 1
     * @param y1 the y-coordinate of position 1
     * @param z1 the z-coordinate of position 1
     * @param x2 the x-coordinate of position 2
     * @param y2 the y-coordinate of position 2
     * @param z2 the z-coordinate of position 2
     * @param x3 the x-coordinate of position 3
     * @param y3 the y-coordinate of position 3
     * @param z3 the z-coordinate of position 3
     * @param x4 the x-coordinate of position 4
     * @param y4 the y-coordinate of position 4
     * @param z4 the z-coordinate of position 4
     */
    public Tetrahedron(double x1, double y1, double z1, double x2, double y2, double z2,
                       double x3, double y3, double z3, double x4, double y4, double z4){
        var vec1 = new Vector3d(x1, y1, z1);
        var vec2 = new Vector3d(x2, y2, z2);
        var vec3 = new Vector3d(x3, y3, z3);
        var vec4 = new Vector3d(x4, y4, z4);
        var vec21 = vec2.subtract(vec1);
        var vec31 = vec3.subtract(vec1);
        var vec41 = vec4.subtract(vec1);
        Matrix3x3d a = new Matrix3x3d(vec21.x(), vec31.x(), vec41.x(),
                vec21.y(), vec31.y(), vec41.y(),
                vec21.z(), vec31.z(), vec41.z());
        var trans = new AffineTransformation3d(a, vec1);
        try {
            this.invTrans = trans.inverse();
        }catch (SingularException e){
            this.empty = true;
        }
    }

    @Override
    public boolean contains(double x, double y, double z) {
        if(this.empty)return false;
        Vector3d pos = new Vector3d(x, y, z);
        pos = invTrans.apply(pos);
        return baseTetra.contains(pos.x(), pos.y(), pos.z());
    }

    private static class BaseTetrahedron implements Region3d {
        @Override
        public boolean contains(double x, double y, double z) {
            return x >= 0 && y >= 0 && z>=0 && x + y + z <= 1;
        }
    }

}
