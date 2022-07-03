package tokyo.nakanaka.buildvox.core.selectionShape.shape;

import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selectionShape.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

public interface Shape extends SelectionShape {
    /**
     * @throws PosArrayLengthException if the size of posData is invalid.
     */
    Selection createSelection(Vector3i[] posArray);

}
