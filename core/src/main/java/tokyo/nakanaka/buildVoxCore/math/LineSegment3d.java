package tokyo.nakanaka.buildVoxCore.math;

import tokyo.nakanaka.buildVoxCore.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;

/**
 * Represents line segment in 3 dimension which is represented by 2 points
 */
public class LineSegment3d {
    private Vector3d pos1;
    private Vector3d pos2;

    /**
     * Constructs new instance by 2 points
     * @param pos1 position of 1st point
     * @param pos2 position of 2nd point
     */
    public LineSegment3d(Vector3d pos1, Vector3d pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    /**
     * Constructs new instance by 2 points
     * @param x1 x-coordinate of 1st point
     * @param y1 y-coordinate of 1st point
     * @param z1 z-coordinate of 1st point
     * @param x2 x-coordinate of 2nd point
     * @param y2 y-coordinate of 2nd point
     * @param z2 z-coordinate of 2nd point
     */
    public LineSegment3d(double x1, double y1, double z1, double x2, double y2, double z2){
        this.pos1 = new Vector3d(x1, y1, z1);
        this.pos2 = new Vector3d(x2, y2, z2);
    }

    /**
     * Gets the 1st point position
     * @return the 1st point position
     */
    public Vector3d pos1() {
        return pos1;
    }

    /**
     * Gets the 2nd point position
     * @return the 2nd point position
     */
    public Vector3d pos2() {
        return pos2;
    }

    /**
     * Translate this line segment
     * @param dx the displacement along x-axis
     * @param dy the displacement along y-axis
     * @param dz the displacement along z-axis
     * @return a new instance
     */
    public LineSegment3d translate(double dx, double dy, double dz){
        return new LineSegment3d(pos1.add(dx, dy, dz), pos2.add(dx, dy, dz));
    }

    /**
     * Affine transform this line segment
     * @param trans the affine transformation
     * @return a new instance
     */
    public LineSegment3d affineTransform(AffineTransformation3d trans){
        return new LineSegment3d(trans.apply(pos1), trans.apply(pos2));
    }

}
