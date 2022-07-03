package tokyo.nakanaka.buildvox.core.selectionShape.shapeMixin;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;

public interface ShapeMixin {
    /**
     * @throws PosArrayLengthException if the size of posData is invalid.
     * @throws IllegalStateException if the mixin is not the state to create a selection.
     */
    Selection createSelection(Vector3i[] posArray);

}
