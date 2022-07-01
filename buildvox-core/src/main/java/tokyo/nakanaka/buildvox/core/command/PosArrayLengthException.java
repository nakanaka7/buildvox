package tokyo.nakanaka.buildvox.core.command;

public class PosArrayLengthException extends RuntimeException {
    private int acceptableSize;

    public PosArrayLengthException(int acceptableSize) {
        this.acceptableSize = acceptableSize;
    }

    public int getAcceptableSize() {
        return acceptableSize;
    }

}
