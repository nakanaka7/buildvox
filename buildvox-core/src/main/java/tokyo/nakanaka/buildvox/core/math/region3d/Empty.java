package tokyo.nakanaka.buildvox.core.math.region3d;

public class Empty implements Region3d{
    @Override
    public boolean contains(double x, double y, double z) {
        return false;
    }
}
