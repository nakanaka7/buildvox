package tokyo.nakanaka.buildvox.core.selectionShape;

/**
 * Threw when invalid length pos-array is set when creating selection in SelectionShape.createSelection().
 */
public class PosArrayLengthException extends RuntimeException {
    private final int acceptableLength;

    /**
     * Creates a new instance.
     * @param acceptableLength the acceptable length to create a selection.
     */
    public PosArrayLengthException(int acceptableLength) {
        this.acceptableLength = acceptableLength;
    }

    /**
     * Gets the acceptable length.
     * @return the acceptable length.
     */
    public int getAcceptableLength() {
        return acceptableLength;
    }

}
