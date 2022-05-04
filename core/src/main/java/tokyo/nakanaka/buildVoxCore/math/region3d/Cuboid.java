package tokyo.nakanaka.buildVoxCore.math.region3d;

import tokyo.nakanaka.buildVoxCore.math.vector.Vector3d;

/**
 * Represents a cuboid which is parallel to each x, y, z-axis.
 */
public class Cuboid implements Region3d {
    private double x1;
    private double y1;
    private double z1;
    private double x2;
    private double y2;
    private double z2;

    /**
     * Construct a cuboid which diagonal corners are (x1, y1, z1) and (x2, y2 ,z1)
     * @param x1 x coordinate of the first position
     * @param y1 y coordinate of the first position
     * @param z1 z coordinate of the first position
     * @param x2 x coordinate of the second position
     * @param y2 y coordinate of the second position
     * @param z2 z coordinate of the second position
     */
    public Cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public Cuboid(Vector3d pos1, Vector3d pos2) {
        this(pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z());
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return ((x1 <= x && x <= x2) || (x2 <= x && x <= x1))
                &&((y1 <= y && y <= y2) || (y2 <= y && y <= y1))
                &&((z1 <= z && z <= z2) || (z2 <= z && z <= z1));
    }

    public double x1(){
        return x1;
    }

    public double y1(){
        return y1;
    }

    public double z1(){
        return z1;
    }

    public double x2(){
        return x2;
    }

    public double y2(){
        return y2;
    }

    public double z2(){
        return z2;
    }

}
