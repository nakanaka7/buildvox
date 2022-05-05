package tokyo.nakanaka.buildvox.core.math.region3d;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Represents a sphere which center is (0, 0, 0)
 */
public class Sphere implements Region3d {
    private double r;

    /**
     * @param r a radius of the sphere
     * @throws IllegalArgumentException if r < 0
     */
    public Sphere(double r) {
        if(r < 0) {
            throw new IllegalArgumentException();
        }
        this.r = r;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        double distance = new Vector3D(x, y, z).getNorm();
        return distance <= this.r;
    }

}
