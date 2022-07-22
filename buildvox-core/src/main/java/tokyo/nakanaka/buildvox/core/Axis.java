package tokyo.nakanaka.buildvox.core;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

/**
 * Represents 3-dimensional axis
 */
public enum Axis {
    X("x"),
    Y("y"),
    Z("z");

    private String str;
    Axis(String str) {
        this.str = str;
    }

    public String toString(){
        return str;
    }

    /**
     * Gets a Vector3i which corresponds to the axis. One of the coordinates is 1, and the others is 0.
     * @return a Vector3i.
     */
    public Vector3i toVector3i() {
        return switch (this) {
            case X -> Vector3i.PLUS_I;
            case Y -> Vector3i.PLUS_J;
            case Z -> Vector3i.PLUS_K;
        };
    }

}
