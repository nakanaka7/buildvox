package tokyo.nakanaka.buildVoxCore.math.vector;

import java.util.Objects;

/**
 * Represents 3 dimensional vector which takes 3 int
 */
@SuppressWarnings("unused")
public class Vector3i {
    /** a vector (0, 0, 0) */
    public static final Vector3i ZERO = new Vector3i(0, 0, 0);
    /** a vector (1, 0, 0) */
    public static final Vector3i PLUS_I = new Vector3i(1, 0, 0);
    /** a vector (0, 1, 0) */
    public static final Vector3i PLUS_J = new Vector3i(0, 1, 0);
    /** a vector (0, 0, 1) */
    public static final Vector3i PLUS_K = new Vector3i(0, 0, 1);
    /** a vector (-1, 0, 0) */
    public static final Vector3i MINUS_I = new Vector3i(-1, 0, 0);
    /** a vector (0, -1, 0) */
    public static final Vector3i MINUS_J = new Vector3i(0, -1, 0);
    /** a vector (0, 0, -1) */
    public static final Vector3i MINUS_K = new Vector3i(0, 0, -1);
    private int x;
    private int y;
    private int z;

    /**
     * Constructs a vector from 3 int.
     * @param x x-coordinate of the vector
     * @param y y-coordinate of the vector
     * @param z z-coordinate of the vector
     */
    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get the x component of the vector
     * @return the x component of the vector
     */
    public int x() {
        return x;
    }

    /**
     * Get the y component of the vector
     * @return the y component of the vector
     */
    public int y() {
        return y;
    }

    /**
     * Get the z component of the vector
     * @return the z component of the vector
     */
    public int z() {
        return z;
    }

    /**
     * Get a vector3d which is equivalent to this instance.
     * @return a vector3d
     */
    public Vector3d toVector3d() {
        return new Vector3d(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3i vector3i = (Vector3i) o;
        return x == vector3i.x && y == vector3i.y && z == vector3i.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

}
