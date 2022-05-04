package tokyo.nakanaka.buildVoxCore.selection;

import tokyo.nakanaka.buildVoxCore.math.region3d.Parallelepiped;
import tokyo.nakanaka.buildVoxCore.math.region3d.Region3d;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Stores 3 integers. Represents a lattice point in 3-dimension space.
 */
@Deprecated
public record LatticePoint3(int x, int y, int z) {
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
