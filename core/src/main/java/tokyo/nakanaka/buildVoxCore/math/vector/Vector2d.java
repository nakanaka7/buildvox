package tokyo.nakanaka.buildVoxCore.math.vector;

/**
 * Represents 2 dimensional vector which takes 2 double
 */
public class Vector2d {
    private double x;
    private double y;

    /**
     * Constructs a vector from 2 doubles.
     * @param x x-coordinate of the vector
     * @param y y-coordinate of the vector
     */
    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x component of the vector
     * @return the x component of the vector
     */
    public double x() {
        return x;
    }

    /**
     * Get the y component of the vector
     * @return the y component of the vector
     */
    public double y() {
        return y;
    }

    /**
     * Gets dot product with another vector
     * @param v another vector
     * @return dot product with another vector
     */
    public double dotProduct(Vector2d v){
        return x * v.x + y * v.y;
    }

    /**
     * Get the length(L2 norm) of the vector.
     * @return length(L2 norm) of the vector
     */
    public double length(){
        return Math.sqrt(this.dotProduct(this));
    }

}
