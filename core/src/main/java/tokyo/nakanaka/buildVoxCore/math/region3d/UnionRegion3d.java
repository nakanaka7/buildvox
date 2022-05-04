package tokyo.nakanaka.buildVoxCore.math.region3d;

/**
 * A region3d which is the union of some region3ds
 */
public class UnionRegion3d implements Region3d{
    private Region3d[] regions;

    /**
     * Constructs a union region
     * @param regions regions to unionise
     * @throws IllegalArgumentException if regions' length is 0
     */
    public UnionRegion3d(Region3d... regions) {
        if(regions.length == 0){
            throw new IllegalArgumentException();
        }
        this.regions = regions;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        boolean contain = false;
        for(Region3d e : regions){
            if(e.contains(x, y, z)){
                contain = true;
                break;
            }
        }
        return contain;
    }

}
