package tokyo.nakanaka.buildvox.core.selectionShape;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

/**
 * Represents a selection without pos-array information.
 */
public interface SelectionShape {
    /**
     * Creates a selection by the pos-array.
     * @param posArray the pos-array.
     * @return a selection.
     */
    Selection createSelection(Vector3i[] posArray);
}
