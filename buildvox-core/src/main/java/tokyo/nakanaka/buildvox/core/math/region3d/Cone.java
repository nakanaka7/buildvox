package tokyo.nakanaka.buildvox.core.math.region3d;

import tokyo.nakanaka.buildvox.core.math.vector.Vector2d;

/**
 * Represents a cone which base is on x-y plane, which base center is the space origin,
 * and which extends to positive z.
 */
public class Cone implements Region3d{
    private double radius;
    private double height;

    /**
     * @param radius a radius of the base disc
     * @param height a height of the cone
     * @throws IllegalArgumentException if radius < 0 or height < 0
     */
    public Cone(double radius, double height) {
        if(radius < 0 || height < 0) {
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
        Vector2d v = new Vector2d(x, y);
        return v.length() <= this.radius * (1 - z / this.height);
    }

}
