package tokyo.nakanaka.buildvox.core;

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

}
