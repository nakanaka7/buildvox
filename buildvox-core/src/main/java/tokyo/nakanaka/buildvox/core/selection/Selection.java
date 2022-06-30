package tokyo.nakanaka.buildvox.core.selection;

import tokyo.nakanaka.buildvox.core.math.region3d.Cuboid;
import tokyo.nakanaka.buildvox.core.math.region3d.EditableRegion3d;
import tokyo.nakanaka.buildvox.core.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildvox.core.math.region3d.Region3d;
import tokyo.nakanaka.buildvox.core.math.transformation.AffineTransformation3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
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

    @Deprecated
    private static class BoundRegion3d implements Region3d {
        private EditableRegion3d region;
        private Parallelepiped piped;

        public BoundRegion3d(Region3d region, double ubx, double uby, double ubz,
                             double lbx, double lby, double lbz) {
            if(region instanceof EditableRegion3d editRegion){
                this.region = editRegion;
            }else{
                this.region = new EditableRegion3d(region);
            }
            this.piped = new Parallelepiped(ubx, uby, ubz, lbx, lby, lbz);
        }

        public BoundRegion3d(Region3d region, Parallelepiped piped) {
            this.region = new EditableRegion3d(region);
            this.piped = piped;
        }

        public BoundRegion3d(Region3d region, Cuboid bound) {
            this.region = new EditableRegion3d(region);
            this.piped = new Parallelepiped(bound.x1(), bound.y1(), bound.z1(), bound.x2(), bound.y2(), bound.z2());
        }

        public boolean contains(double x, double y, double z) {
            return this.region.contains(x, y, z);
        }

        public Region3d region(){
            return this.region;
        }

        public Parallelepiped bound(){
            return this.piped;
        }

        public BoundRegion3d affineTransform(AffineTransformation3d trans){
            var transRegion = this.region.affineTransform(trans);
            var transPiped = this.piped.affineTransform(trans);
            return new BoundRegion3d(transRegion, transPiped);
        }

        public BoundRegion3d translate(double dx, double dy, double dz){
            return this.affineTransform(AffineTransformation3d.ofTranslation(dx, dy, dz));
        }

        /**
         * Returns the rotated bound region about x-axis.
         * @param angle the angle(radian)
         * @return the rotated bound region about x-axis.
         */
        public BoundRegion3d rotateX(double angle){
            return this.affineTransform(AffineTransformation3d.ofRotationX(angle));
        }

        /**
         * Returns the rotated bound region about y-axis.
         * @param angle the angle(radian)
         * @return the rotated bound region about y-axis.
         */
        public BoundRegion3d rotateY(double angle){
            return this.affineTransform(AffineTransformation3d.ofRotationY(angle));
        }

        /**
         * Returns the rotated bound region about z-axis.
         * @param angle the angle(radian)
         * @return the rotated bound region about z-axis.
         */
        public BoundRegion3d rotateZ(double angle){
            return this.affineTransform(AffineTransformation3d.ofRotationZ(angle));
        }

    }

    /**
     * Stores 3 integers. Represents a lattice point in 3-dimension space.
     */
    @Deprecated
    private static record LatticePoint3(int x, int y, int z) {
        /**
         * Gets the iterator of LatticePoint3 which are contained in the given Region3d.
         * @param region the region
         * @param bound the bound of the region
         * @return the iterator of LatticePoint3 which are contained in the given Region3d.
         */
        public static Iterator<LatticePoint3> getIteratorOfRegion3d(Region3d region, Parallelepiped bound){
            return new Iterator<>() {
                private int minX = (int)Math.floor(bound.minX());
                private int minY = (int)Math.floor(bound.minY());
                private int minZ = (int)Math.floor(bound.minZ());
                private int maxX = (int)Math.floor(bound.maxX());
                private int maxY = (int)Math.floor(bound.maxY());
                private int maxZ = (int)Math.floor(bound.maxZ());

                //target pos (x, y, z)
                private int x = minX;
                private int y = minY;
                private int z = minZ;

                //used hasNext() and next()
                private LatticePoint3 next = null;
                private boolean tryFind = false;

                @Override
                public boolean hasNext() {
                    if (!tryFind) {
                        next = find();
                        tryFind = true;
                    }
                    return next != null;
                }

                @Override
                public LatticePoint3 next() {
                    if (!tryFind) {
                        next = find();
                        tryFind = true;
                    }
                    if (next != null) {
                        var e = next;
                        next = null;
                        tryFind = false;
                        return e;
                    } else {
                        throw new NoSuchElementException();
                    }
                }

                //shift to next target position; z -> x -> y order
                //target pos will be (minX, minZ, maxY + 1) in the end
                private void nextTargetPos() {
                    if (y == maxY + 1) {
                        return;
                    }
                    if (z < maxZ) {
                        ++z;
                    } else {
                        z = minZ;
                        if (x < maxX) {
                            ++x;
                        } else {
                            x = minX;
                            ++y;
                        }
                    }
                }

                /**
                 * Find next BlockPosition3d
                 *
                 * @return next BlockPosition3d, or null if it cannot find one.
                 */
                private LatticePoint3 find() {
                    while (true) {
                        if (y == maxY + 1) return null;
                        if (region.contains(x, y, z)) {
                            var pos = new LatticePoint3(x, y, z);
                            this.nextTargetPos();
                            return pos;
                        }
                        this.nextTargetPos();
                    }
                }

            };
        }

        /**
         * Legacy method, inefficient but so simple code
         */
        @SuppressWarnings("unused")
        @Deprecated
        public static Iterator<LatticePoint3> getIteratorOfRegion3dLegacy(Region3d region, Parallelepiped bound){
            Set<LatticePoint3> posSet = new HashSet<>();
            int minX = (int)Math.floor(bound.minX());
            int minY = (int)Math.floor(bound.minY());
            int minZ = (int)Math.floor(bound.minZ());
            int maxX = (int)Math.floor(bound.maxX());
            int maxY = (int)Math.floor(bound.maxY());
            int maxZ = (int)Math.floor(bound.maxZ());
            for (int x = minX; x <= maxX; ++x){
                for (int y = minY; y <= maxY; ++y){
                    for (int z = minZ; z <= maxZ; ++z){
                        if (region.contains(x, y, z)){
                            posSet.add(new LatticePoint3(x, y, z));
                        }
                    }
                }
            }
            return posSet.iterator();
        }

    }
    
}
