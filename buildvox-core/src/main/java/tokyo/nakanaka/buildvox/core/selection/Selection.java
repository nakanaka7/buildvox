package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a selection. Selection consists of the region and a parallelepiped bound. Points which both
 * region and bound contain will be regarded selection points.
 */
public class Selection {
    private BoundRegion3d boundRegion3d;

    private Selection(BoundRegion3d boundRegion3d) {
        this.boundRegion3d = boundRegion3d;
    }

    /**
     * Constructs a selection
     * @param region3d a region of the selection
     * @param bound a bound of the selection
     */
    public Selection(Region3d region3d, Parallelepiped bound) {
        this.boundRegion3d = new BoundRegion3d(region3d, bound);
    }

    public Selection(Region3d region3d, Cuboid bound) {
        this.boundRegion3d = new BoundRegion3d(region3d, bound);
    }

    public Selection(Region3d region, double ubx, double uby, double ubz,
                     double lbx, double lby, double lbz) {
        this.boundRegion3d = new BoundRegion3d(region, ubx, uby, ubz, lbx, lby, lbz);
    }

    /**
     * Affine transform this selection
     * @param trans the affine transformation which this selection will be applied to.
     * @return a new instance
     */
    public Selection affineTransform(AffineTransformation3d trans){
        return new Selection(boundRegion3d.affineTransform(trans));
    }

    /**
     * Translate this selection
     * @param dx the displacement along x-axis
     * @param dy the displacement along y-axis
     * @param dz the displacement along z-axis
     * @return new instance
     */
    public Selection translate(double dx, double dy, double dz){
        return new Selection(boundRegion3d.translate(dx, dy, dz));
    }

    /**
     * Translate this selection
     * @param dr the displacement
     * @return new instance
     */
    public Selection translate(Vector3d dr) {
        return translate(dr.x(), dr.y(), dr.z());
    }

    /**
     * Returns the rotated selection about x-axis.
     * @param angle the angle(radian)
     * @return the rotated selection about x-axis.
     */
    public Selection rotateX(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationX(angle));
    }

    /**
     * Returns the rotated selection about y-axis.
     * @param angle the angle(radian)
     * @return the rotated selection about y-axis.
     */
    public Selection rotateY(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationY(angle));
    }

    /**
     * Returns the rotated selection about z-axis.
     * @param angle the angle(radian)
     * @return the rotated selection about z-axis.
     */
    public Selection rotateZ(double angle){
        return this.affineTransform(AffineTransformation3d.ofRotationZ(angle));
    }

    /**
     * Return a scaled selection
     * @param factorX factor along x-axis
     * @param factorY factor along y-axis
     * @param factorZ factor along z-axis
     * @return new instance
     * @throws IllegalArgumentException if a factor is zero
     */
    public Selection scale(double factorX, double factorY, double factorZ) {
        if(factorX * factorY * factorZ == 0) throw new IllegalArgumentException();
        return affineTransform(AffineTransformation3d.ofScale(factorX, factorY, factorZ));
    }

    /**
     * Get the region of this selection
     * @return the region of this selection
     */
    public Region3d getRegion3d() {
        return boundRegion3d;
    }

    /**
     * Get the bound of this selection
     * @return the bound of this selection
     */
    public Parallelepiped getBound() {
        return boundRegion3d.bound();
    }

    /**
     * Calculate block positions of this selection
     * @return block positions of this selection
     */
    public Set<Vector3i> calculateBlockPosSet() {
        BoundRegion3d translated = boundRegion3d.translate(-0.5, -0.5, -0.5);
        Set<Vector3i> posSet = new HashSet<>();
        Iterator<LatticePoint3> iteL3 = LatticePoint3.getIteratorOfRegion3d(translated, translated.bound());
        while(iteL3.hasNext()) {
            LatticePoint3 p = iteL3.next();
            posSet.add(new Vector3i(p.x(), p.y(), p.z()));
        }
        return posSet;
    }

}
