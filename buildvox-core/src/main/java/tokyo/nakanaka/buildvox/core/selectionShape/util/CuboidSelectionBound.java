package tokyo.nakanaka.buildvox.core.selectionShape.util;

import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

/** Represents a cuboid bound of a selection. Assumed pos-array length is 2. */

class CuboidSelectionBound {
    private final Vector3d pos0;
    private final Vector3d pos1;

    public CuboidSelectionBound(Vector3d pos0, Vector3d pos1) {
        this.pos0 = pos0;
        this.pos1 = pos1;
    }

    public CuboidSelectionBound(Vector3i pos0, Vector3i pos1) {
        this.pos0 = pos0.toVector3d();
        this.pos1 = pos1.toVector3d();
    }

    Vector3i getPos0Vector3i() {
        return toVector3i(pos0);
    }

    Vector3i getPos1Vector3i() {
        return toVector3i(pos1);
    }

    private Vector3i toVector3i(Vector3d v) {
        int x = (int)Math.floor(v.x());
        int y = (int)Math.floor(v.y());
        int z = (int)Math.floor(v.z());
        return new Vector3i(x, y, z);
    }

    /** Gets the maximum x-coordinate of this bound. */
    double getMaxDoubleX() {
        return Math.max(pos0.x(), pos1.x()) + 1;
    }

    /** Gets the maximum y-coordinate of this bound. */
    double getMaxDoubleY() {
        return Math.max(pos0.y(), pos1.y()) + 1;
    }

    /** Gets the maximum z-coordinate of this bound. */
    double getMaxDoubleZ() {
        return Math.max(pos0.z(), pos1.z()) + 1;
    }

    /** Gets the minimum x-coordinate of this bound. */
    double getMinDoubleX() {
        return Math.min(pos0.x(), pos1.x());
    }

    /** Gets the minimum y-coordinate of this bound. */
    double getMinDoubleY() {
        return Math.min(pos0.y(), pos1.y());
    }

    /** Gets the minimum z-coordinate of this bound. */
    double getMinDoubleZ() {
        return Math.min(pos0.z(), pos1.z());
    }

    /** Calculates the length along the axis. */
    double calculateLength(Axis axis) {
        Vector3d v = pos0.subtract(pos1);
        return switch (axis) {
            case X -> Math.abs(v.x()) + 1;
            case Y -> Math.abs(v.y()) + 1;
            case Z -> Math.abs(v.z()) + 1;
        };
    }

    /** Calculates the max of the side length. */
    double calculateMaxSideLength(Axis axis) {
        Vector3d v = pos0.subtract(pos1);
        return switch (axis) {
            case X -> Math.max(Math.abs(v.y()), Math.abs(v.z())) + 1;
            case Y -> Math.max(Math.abs(v.z()), Math.abs(v.x())) + 1;
            case Z -> Math.max(Math.abs(v.x()), Math.abs(v.y())) + 1;
        };
    }

    /** Shrink the top of the bound. (The direction of the axis is from "bottom" to "top")
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    CuboidSelectionBound shrinkTop(Axis axis, int length) {
        Vector3d s = pos0.subtract(pos1);
        double t = switch (axis) {
            case X -> s.x();
            case Y -> s.y();
            case Z -> s.z();
        };
        if(Math.abs(t) < length) throw new IllegalStateException();
        Direction dir = calculateDirection(axis);
        Vector3i dirV = dir.toVector3i();
        Vector3d q1 = pos1.subtract(dirV.scalarMultiply(length).toVector3d());
        return new CuboidSelectionBound(pos0, q1);
    }

    /** Shrink the bottom of the bound. (The direction of the axis is from "bottom" to "top")
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    CuboidSelectionBound shrinkBottom(Axis axis, int length) {
        Vector3d s = pos0.subtract(pos1);
        double t = switch (axis) {
            case X -> s.x();
            case Y -> s.y();
            case Z -> s.z();
        };
        if(Math.abs(t) < length) throw new IllegalStateException();
        Direction dir = calculateDirection(axis);
        Vector3i dirV = dir.toVector3i();
        Vector3d q0 = pos0.add(dirV.scalarMultiply(length).toVector3d());
        return new CuboidSelectionBound(q0, pos1);
    }

    /**
     * Shrinks the sides along the axis.
     * @param thickness the displacement of the wall.
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    CuboidSelectionBound shrinkSides(Axis axis, int thickness) {
        return switch (axis) {
            case X -> shrinkTopBottom(Axis.Y, thickness).shrinkTopBottom(Axis.Z, thickness);
            case Y -> shrinkTopBottom(Axis.Z, thickness).shrinkTopBottom(Axis.X, thickness);
            case Z -> shrinkTopBottom(Axis.X, thickness).shrinkTopBottom(Axis.Y, thickness);
        };
    }

    /**
     * Shrinks both top and bottom.
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    private CuboidSelectionBound shrinkTopBottom(Axis axis, int thickness) {
        Vector3d s = pos0.subtract(pos1);
        double t = switch (axis) {
            case X -> s.x();
            case Y -> s.y();
            case Z -> s.z();
        };
        if(Math.abs(t) < 2 * thickness) throw new IllegalStateException();
        Direction dir = calculateDirection(axis);
        Vector3i dirV = dir.toVector3i();
        Vector3d q0 = pos0.add(dirV.scalarMultiply(thickness).toVector3d());
        Vector3d q1 = pos1.subtract(dirV.scalarMultiply(thickness).toVector3d());
        return new CuboidSelectionBound(q0, q1);
    }

    /**
     * Calculates a direction which is parallel to the axis and the coordinate of the direction is
     * from pos0's to pos1's coordinate.
     */
    Direction calculateDirection(Axis axis) {
        Direction dir;
        switch (axis) {
            case X -> {
                if (pos1.x() - pos0.x() >= 0) {
                    dir = Direction.EAST;
                } else {
                    dir = Direction.WEST;
                }
            }
            case Y -> {
                if(pos1.y() - pos0.y() >= 0) {
                    dir = Direction.UP;
                } else {
                    dir = Direction.DOWN;
                }
            }
            case Z -> {
                if(pos1.z() - pos0.z() >= 0) {
                    dir = Direction.SOUTH;
                }else {
                    dir = Direction.NORTH;
                }
            }
            default -> dir = Direction.UP;
        }
        return dir;
    }

}
