package tokyo.nakanaka.buildVoxCore.math.region3d;

/**
 * Represents 3-dimensional region, which is the collection of points that are given by 3 double set (x, y, z)
 */
public interface Region3d {
    /**
     * Returns true if this region contains a given point, otherwise false
     * @param x x coordinate of the position of the point
     * @param y y coordinate of the position of the point
     * @param z z coordinate of the position of the point
     * @return true if this region contains (x, y, z), otherwise false
     */
    boolean contains(double x, double y, double z);

}
