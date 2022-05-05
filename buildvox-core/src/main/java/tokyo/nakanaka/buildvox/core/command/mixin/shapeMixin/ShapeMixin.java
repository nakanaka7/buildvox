package tokyo.nakanaka.buildvox.core.command.mixin.shapeMixin;

import tokyo.nakanaka.buildvox.core.command.IllegalPosException;
import tokyo.nakanaka.buildvox.core.command.MissingPosDataException;
import tokyo.nakanaka.buildvox.core.command.PosDataSizeException;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

public interface ShapeMixin {
    /**
     * @throws PosDataSizeException if the size of posData is invalid.
     * @throws MissingPosDataException if posData has some missing data.
     * @throws IllegalPosException if some pos data is illegal
     * @throws IllegalStateException if the mixin is not the state to create a selection.
     */
    Selection createSelection(Vector3i[] posArray);

}
