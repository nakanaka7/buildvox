package tokyo.nakanaka.buildVoxCore.command.mixin.shapeMixin;

import tokyo.nakanaka.buildVoxCore.command.IllegalPosException;
import tokyo.nakanaka.buildVoxCore.command.MissingPosDataException;
import tokyo.nakanaka.buildVoxCore.command.PosDataSizeException;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.selection.Selection;

public interface ShapeMixin {
    /**
     * @throws PosDataSizeException if the size of posData is invalid.
     * @throws MissingPosDataException if posData has some missing data.
     * @throws IllegalPosException if some pos data is illegal
     * @throws IllegalStateException if the mixin is not the state to create a selection.
     */
    Selection createSelection(Vector3i[] posArray);

}
