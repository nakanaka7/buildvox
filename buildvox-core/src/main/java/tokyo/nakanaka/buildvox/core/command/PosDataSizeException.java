package tokyo.nakanaka.buildvox.core.command;

public class PosDataSizeException extends RuntimeException {
    private int acceptableSize;

    public PosDataSizeException(int acceptableSize) {
        this.acceptableSize = acceptableSize;
    }

    public int getAcceptableSize() {
        return acceptableSize;
    }

}
