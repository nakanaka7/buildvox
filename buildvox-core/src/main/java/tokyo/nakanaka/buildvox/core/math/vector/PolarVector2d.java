package tokyo.nakanaka.buildvox.core.math.vector;

import org.apache.commons.math3.complex.Complex;

/**
 * Represents 2 dimensional vector of polar coordinate system.
 */
public class PolarVector2d {
    private double radius;
    private double arg;

    /**
     * Constructs a vector
     * @param radius the radius
     * @param arg the argument (by radian)
     */
    public PolarVector2d(double radius, double arg) {
        this.radius = radius;
        this.arg = arg;
    }

    /**
     * Create a new instance from the 2 component in cartesian system of the vector
     * @param x x-coordinate of the vector
     * @param y y-coordinate of the vector
     * @return a new instance
     */
    public static PolarVector2d newInstance(double x, double y) {
        Complex c = new Complex(x, y);
        return new PolarVector2d(c.abs(), c.getArgument());
    }

    /**
     * Get the radius of this vector
     * @return the radius of this vector
     */
    @SuppressWarnings("unused")
    public double radius() {
        return this.radius;
    }

    /**
     * Get the argument of this vector by radian. The value is between -PI (not inclusive) and PI (inclusive).
     * @return the argument of this vector by radian. The value is between -PI (not inclusive) and PI (inclusive)
     */
    public double argument() {
        return this.arg;
    }

}
