package tokyo.nakanaka.buildvox.core.math.region3d;

public class DifferenceRegion3d implements Region3d {
    private Region3d region1;
    private Region3d region2;

    /**
     * Construct a region of {region1} - {region2}
     *
     * @param region1
     * @param region2
     */
    public DifferenceRegion3d(Region3d region1, Region3d region2) {
        this.region1 = region1;
        this.region2 = region2;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return this.region1.contains(x, y, z) && !this.region2.contains(x, y, z);
    }

}
