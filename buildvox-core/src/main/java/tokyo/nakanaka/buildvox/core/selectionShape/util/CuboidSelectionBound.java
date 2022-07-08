package tokyo.nakanaka.buildvox.core.selectionShape.util;

import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

/** Represents a cuboid bound of a selection. Assumed pos-array length is 2. */

class CuboidSelectionBound {
    private final Vector3i pos0;
    private final Vector3i pos1;

    public CuboidSelectionBound(Vector3i pos0, Vector3i pos1) {
        this.pos0 = pos0;
        this.pos1 = pos1;
    }

    Vector3i pos0() {
        return pos0;
    }

    Vector3i pos1() {
        return pos1;
    }

    /** Calculates the length along the axis. */
    int calculateLength(Axis axis) {
        Vector3i v = pos0.subtract(pos1);
        return switch (axis) {
            case X -> Math.abs(v.x());
            case Y -> Math.abs(v.y());
            case Z -> Math.abs(v.z());
        };
    }

    /** Shrink the top of the bound. (The direction of the axis is from "bottom" to "top")
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    CuboidSelectionBound shrinkTop(Axis axis, int length) {
        Vector3i s = pos0.subtract(pos1);
        int t = switch (axis) {
            case X -> s.x();
            case Y -> s.y();
            case Z -> s.z();
        };
        if(Math.abs(t) < length) throw new IllegalStateException();
        Direction dir = calculateDirection(axis);
        Vector3i dirV = dir.toVector3i();
        Vector3i q1 = pos1.subtract(dirV.scalarMultiply(length));
        return new CuboidSelectionBound(pos0, q1);
    }

    /**
     * Shrinks the sides along the axis.
     * @param thickness the displacement of the wall.
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    CuboidSelectionBound shrinkSides(Axis axis, int thickness) {
        return switch (axis) {
            case X -> shrink(Axis.Y, thickness).shrink(Axis.Z, thickness);
            case Y -> shrink(Axis.Z, thickness).shrink(Axis.X, thickness);
            case Z -> shrink(Axis.X, thickness).shrink(Axis.Y, thickness);
        };
    }

    /**
     * Shrinks the cuboid along the axis.
     * @param axis the axis to shrink this cuboid along.
     * @param thickness the displacement of the wall.
     * @throws IllegalStateException if it cannot shrink anymore.
     */
    CuboidSelectionBound shrink(Axis axis, int thickness) {
        Vector3i s = pos0.subtract(pos1);
        int t = switch (axis) {
            case X -> s.x();
            case Y -> s.y();
            case Z -> s.z();
        };
        if(Math.abs(t) < 2 * thickness) throw new IllegalStateException();
        Direction dir = calculateDirection(axis);
        Vector3i dirV = dir.toVector3i();
        Vector3i q0 = pos0.add(dirV.scalarMultiply(thickness));
        Vector3i q1 = pos1.subtract(dirV.scalarMultiply(thickness));
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
