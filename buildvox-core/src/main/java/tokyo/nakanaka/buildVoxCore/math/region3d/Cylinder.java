package tokyo.nakanaka.buildVoxCore.math.region3d;

import tokyo.nakanaka.buildVoxCore.math.vector.Vector2d;

/**
 * Represents a cylinder which base is on x-y plane, which base center is the space origin,
 * and which axis is z-axis.
 */
public class Cylinder implements Region3d {
    private double radius;
    private double height;

    /**
     * @param radius the radius of the base disc
     * @param height the height of the cylinder
     * @throws IllegalArgumentException if radius <= 0 or height <= 0
     */
    public Cylinder(double radius, double height) {
        if(radius <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        this.radius = radius;
        this.height = height;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        if(z < 0 || this.height < z) {
            return false;
        }
        return new Vector2d(x, y).length() <= this.radius;
    }


}
