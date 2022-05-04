package tokyo.nakanaka.buildVoxCore.math.region3d;

/**
 * Represents a pyramid which base is on x-y plane, which base center is the space origin,
 * and which axis is z-axis.
 */
public class Pyramid implements Region3d {
    private double side;
    private double height;

    /**
     * @param side the side length of the base square.
     * @param height the height of the pyramid
     * @throws IllegalArgumentException if side or height is less than 0.
     */
    public Pyramid(double side, double height) {
        if(side < 0 || height < 0) {
            throw new IllegalArgumentException();
        }
        this.side = side;
        this.height = height;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        double l = (side / 2) * (1 - z / height);
        return Math.abs(x) <= l && Math.abs(y) <= l && z >= 0;
    }

}
