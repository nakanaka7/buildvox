package tokyo.nakanaka.buildvox.core.command.mixin.shapeMixin;

import tokyo.nakanaka.buildvox.core.command.IllegalPosException;
import tokyo.nakanaka.buildvox.core.command.MissingPosException;
import tokyo.nakanaka.buildvox.core.command.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

public interface ShapeMixin {
    /**
     * @throws PosArrayLengthException if the size of posData is invalid.
     * @throws MissingPosException if posData has some missing data.
     * @throws IllegalPosException if some pos data is illegal
     * @throws IllegalStateException if the mixin is not the state to create a selection.
     */
    Selection createSelection(Vector3i[] posArray);

}
