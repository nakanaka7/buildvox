package tokyo.nakanaka.buildvox.core.selectionShape;

public class PosArrayLengthException extends RuntimeException {
    private int acceptableSize;

    public PosArrayLengthException(int acceptableSize) {
        this.acceptableSize = acceptableSize;
    }

    public int getAcceptableSize() {
        return acceptableSize;
    }

}
