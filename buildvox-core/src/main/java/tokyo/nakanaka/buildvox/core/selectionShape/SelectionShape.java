package tokyo.nakanaka.buildvox.core.selectionShape;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

/**
 * Represents a selection without pos-array information.
 */
public interface SelectionShape {
    /**
     * Creates a selection by the pos-array.
     * @param posArray the pos-array. All the elements should non-null.
     * @return a selection.
     * @throws PosArrayLengthException if the pos array length is invalid.
     */
    Selection createSelection(Vector3i[] posArray);
}
