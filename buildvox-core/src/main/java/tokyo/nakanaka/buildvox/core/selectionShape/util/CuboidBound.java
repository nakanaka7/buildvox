package tokyo.nakanaka.buildvox.core.selectionShape.util;

import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3d;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

/**
 * Internal.
 * Represents a cuboid bound of a selection. Assumed pos-array length is 2.
 */

class CuboidBound {
    private final Vector3d pos0;
    private final Vector3d pos1;

    public CuboidBound(Vector3d pos0, Vector3d pos1) {
        this.pos0 = pos0;
        this.pos1 = pos1;
    }

    public CuboidBound(Vector3i pos0, Vector3i pos1) {
        this.pos0 = pos0.toVector3d();
        this.pos1 = pos1.toVector3d();
    }

    public Vector3d getPos0() {
        return pos0;
    }

    public Vector3d getPos1() {
        return pos1;
    }

    /** Gets the maximum x-coordinate of this bound. */
    double getMaxX() {
        return Math.max(pos0.x(), pos1.x()) + 1;
    }

    /** Gets the maximum y-coordinate of this bound. */
    double getMaxY() {
        return Math.max(pos0.y(), pos1.y()) + 1;
    }

    /** Gets the maximum z-coordinate of this bound. */
    double getMaxZ() {
        return Math.max(pos0.z(), pos1.z()) + 1;
    }

    /** Gets the minimum x-coordinate of this bound. */
    double getMinX() {
        return Math.min(pos0.x(), pos1.x());
    }

    /** Gets the minimum y-coordinate of this bound. */
    double getMinY() {
        return Math.min(pos0.y(), pos1.y());
    }

    /** Gets the minimum z-coordinate of this bound. */
    double getMinZ() {
        return Math.min(pos0.z(), pos1.z());
    }

    /** Gets the center point */
    Vector3d getCenter() {
        return pos0.add(pos1).add(1, 1, 1).scalarMultiply(0.5);
    }

    /** Gets the mid x-coordinate. */
    double getMidX() {
        return getCenter().x();
    }

    /** Gets the mid y-coordinate. */
    double getMidY() {
        return getCenter().y();
    }

    /** Gets the mid z-coordinate. */
    double getMidZ() {
        return getCenter().z();
    }

    private Vector3d vec10() {
        return pos1.subtract(pos0);
    }

    /** Gets the length along x-axis. */
    double getLengthX() {
        return Math.abs(vec10().x()) + 1;
    }

    /** Gets the length along y-axis. */
    double getLengthY() {
        return Math.abs(vec10().y()) + 1;
    }

    /** Gets the length along z-axis. */
    double getLengthZ() {
        return Math.abs(vec10().z()) + 1;
    }

    /** Gets the half-length along x-axis. */
    double getHalfLengthX() {
        return getLengthX() * 0.5;
    }

    /** Gets the half-length along y-axis. */
    double getHalfLengthY() {
        return getLengthY() * 0.5;
    }

    /** Gets the half-length along z-axis. */
    double getHalfLengthZ() {
        return getLengthZ() * 0.5;
    }

    /** Gets the length along the axis. */
    double getLength(Axis axis) {
        Vector3d v = pos0.subtract(pos1);
        return switch (axis) {
            case X -> Math.abs(v.x()) + 1;
            case Y -> Math.abs(v.y()) + 1;
            case Z -> Math.abs(v.z()) + 1;
        };
    }

    /** Gets the max of the side length. */
    double getMaxSideLength(Axis axis) {
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
    CuboidBound shrinkTop(Axis axis, double length) {
        Vector3d s = pos0.subtract(pos1);
        double t = switch (axis) {
            case X -> s.x();
            case Y -> s.y();
            case Z -> s.z();
        };
        if(Math.abs(t) < length) throw new IllegalStateException();
        Direction dir = calculateDirection(axis);
        Vector3i dirV = dir.toVector3i();
        Vector3d q1 = pos1.subtract(dirV.toVector3d().scalarMultiply(length));
        return new CuboidBound(pos0, q1);
    }

    /** Shrink the bottom of the bound. (The direction of the axis is from "bottom" to "top")
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    CuboidBound shrinkBottom(Axis axis, double length) {
        Vector3d s = pos0.subtract(pos1);
        double t = switch (axis) {
            case X -> s.x();
            case Y -> s.y();
            case Z -> s.z();
        };
        if(Math.abs(t) < length) throw new IllegalStateException();
        Direction dir = calculateDirection(axis);
        Vector3i dirV = dir.toVector3i();
        Vector3d q0 = pos0.add(dirV.toVector3d().scalarMultiply(length));
        return new CuboidBound(q0, pos1);
    }

    /**
     * Shrinks the sides along the axis.
     * @param length the displacement of the wall.
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    CuboidBound shrinkSides(Axis axis, double length) {
        return switch (axis) {
            case X -> shrinkTopBottom(Axis.Y, length).shrinkTopBottom(Axis.Z, length);
            case Y -> shrinkTopBottom(Axis.Z, length).shrinkTopBottom(Axis.X, length);
            case Z -> shrinkTopBottom(Axis.X, length).shrinkTopBottom(Axis.Y, length);
        };
    }

    /**
     * Shrinks both top and bottom.
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    private CuboidBound shrinkTopBottom(Axis axis, double thickness) {
        Vector3d s = pos0.subtract(pos1);
        double t = switch (axis) {
            case X -> s.x();
            case Y -> s.y();
            case Z -> s.z();
        };
        if(Math.abs(t) < 2 * thickness) throw new IllegalStateException();
        Direction dir = calculateDirection(axis);
        Vector3i dirV = dir.toVector3i();
        Vector3d q0 = pos0.add(dirV.toVector3d().scalarMultiply(thickness));
        Vector3d q1 = pos1.subtract(dirV.toVector3d().scalarMultiply(thickness));
        return new CuboidBound(q0, q1);
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
