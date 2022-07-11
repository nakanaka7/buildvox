package tokyo.nakanaka.buildvox.core.math.vector;

import java.util.Objects;

/**
 * Represents 3 dimensional vector which takes 3 double
 */
public class Vector3d {
    /** a vector (0, 0, 0) */
    public static final Vector3d ZERO = new Vector3d(0, 0, 0);
    /** a vector (1, 0, 0) */
    public static final Vector3d PLUS_I = new Vector3d(1, 0, 0);
    /** a vector (0, 1, 0) */
    public static final Vector3d PLUS_J = new Vector3d(0, 1, 0);
    /** a vector (0, 0, 1) */
    public static final Vector3d PLUS_K = new Vector3d(0, 0, 1);
    /** a vector (-1, 0, 0) */
    public static final Vector3d MINUS_I = new Vector3d(-1, 0, 0);
    /** a vector (0, -1, 0) */
    public static final Vector3d MINUS_J = new Vector3d(0, -1, 0);
    /** a vector (0, 0, -1) */
    public static final Vector3d MINUS_K = new Vector3d(0, 0, -1);
    private double x;
    private double y;
    private double z;

    /**
     * Constructs a vector from 3 doubles.
     * @param x x-coordinate of the vector
     * @param y y-coordinate of the vector
     * @param z z-coordinate of the vector
     */
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3d vector3d = (Vector3d) o;
        return Double.compare(vector3d.x, x) == 0 && Double.compare(vector3d.y, y) == 0 && Double.compare(vector3d.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
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
     * Get the z component of the vector
     * @return the z component of the vector
     */
    public double z() {
        return z;
    }

    /**
     * Gets the -1 scalar multiplied vector
     * @return the -1 scalar multiplied vector
     */
    public Vector3d negate(){
        return new Vector3d(-x, -y, -z);
    }

    /**
     * Add another vector
     * @param v another vector
     * @return a new vector
     */
    public Vector3d add(Vector3d v){
        return new Vector3d(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Add another vector
     * @param x x-component of another vector
     * @param y y-component of another vector
     * @param z z-component of another vector
     * @return a new vector
     */
    public Vector3d add(double x, double y, double z){
        return this.add(new Vector3d(x, y, z));
    }

    /**
     * Subtract another vector from this vector
     * @param v another vector
     * @return a new vector
     */
    public Vector3d subtract(Vector3d v){
        return new Vector3d(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Subtract another vector from this vector
     * @param x x-component of another vector
     * @param y y-component of another vector
     * @param z z-component of another vector
     * @return a new vector
     */
    public Vector3d subtract(double x, double y, double z) {
        return subtract(new Vector3d(x, y, z));
    }

    /**
     * Multiply this vector by the given scalar
     * @param a scalar
     * @return a new vector
     */
    public Vector3d scalarMultiply(double a){
        return new Vector3d(a * x, a * y, a * z);
    }

    /**
     * Normalize vector
     * @return a new vector
     * @throws ArithmeticException if this vector is zero vector.
     */
    public Vector3d normalize(){
        return this.scalarMultiply(1 / this.length());
    }

    /**
     * Gets dot product with another vector
     * @param v another vector
     * @return dot product with another vector
     */
    public double dotProduct(Vector3d v){
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Gets cross product with another vector
     * @param v another vector
     * @return cross product with another vector
     */
    public Vector3d crossProduct(Vector3d v){
        return  new Vector3d(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    /**
     * Get the length(L2 norm) of the vector.
     * @return length(L2 norm) of the vector
     */
    public double length(){
        return Math.sqrt(this.dotProduct(this));
    }

    /**
     * Get the distance between this and another vector, which equals to the distance between
     * the vectors' "end points" when each vector originates from (0, 0, 0).
     * @param v another vector
     * @return distance between this and another vector
     */
    public double distance(Vector3d v){
        return this.subtract(v).length();
    }

    /**
     * Gets the nearest vector from candidates.
     * @throws IllegalArgumentException if there is no candidates.
     */
    public Vector3d getNearestVector(Vector3d... candidates){
        if(candidates.length == 0)throw new IllegalArgumentException();
        Vector3d nearest = candidates[0];
        double dis = distance(candidates[0]);
        for(Vector3d c : candidates){
            double disVc = distance(c);
            if(disVc < dis){
                nearest = c;
                dis = disVc;
            }
        }
        return nearest;
    }

}
