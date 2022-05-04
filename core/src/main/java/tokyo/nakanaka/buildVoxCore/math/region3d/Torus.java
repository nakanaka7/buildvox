package tokyo.nakanaka.buildVoxCore.math.region3d;


import tokyo.nakanaka.buildVoxCore.math.vector.PolarVector2d;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;

/**
 * Represents a torus
 */
public class Torus implements Region3d {
    private double majorRadius;
    private double minorRadius;
    /**
     * @param majorRadius the major radius
     * @param minorRadius the minor radius
     * @throws IllegalArgumentException if major or minor radius is less than 0 (not inclusive)
     */
    public Torus(double majorRadius, double minorRadius) {
        if(majorRadius < 0 || minorRadius < 0) {
            throw new IllegalArgumentException();
        }
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        PolarVector2d polar = PolarVector2d.newInstance(x, y);
        double angle = polar.argument();
        Vector3d q = new Vector3d(this.majorRadius * Math.cos(angle), this.majorRadius * Math.sin(angle), 0);
        return new Vector3d(x, y, z).distance(q) <= this.minorRadius;
    }

}
