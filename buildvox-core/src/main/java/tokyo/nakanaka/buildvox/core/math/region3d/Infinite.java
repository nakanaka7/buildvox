package tokyo.nakanaka.buildvox.core.math.region3d;

/**
 * Represents infinite region. contains() always returns true;
 */
public class Infinite implements Region3d {
    @Override
    public boolean contains(double x, double y, double z) {
        return true;
    }
}
