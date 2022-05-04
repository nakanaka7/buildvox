package tokyo.nakanaka.buildVoxCore.command.mixin.shapeMixin;

import tokyo.nakanaka.buildVoxCore.command.MissingPosDataException;
import tokyo.nakanaka.buildVoxCore.command.PosDataSizeException;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.selection.Selection;
import tokyo.nakanaka.buildVoxCore.selection.SelectionCreations;

public class TetrahedronMixin implements ShapeMixin {

    public static final String DESCRIPTION = "a tetrahedron region which vertexes are pos0, pos1, pos2, and pos3";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 4) {
            throw new PosDataSizeException(4);
        }
        Vector3i pos0 = posArray[0];
        Vector3i pos1 = posArray[1];
        Vector3i pos2 = posArray[2];
        Vector3i pos3 = posArray[3];
        if (pos0 == null || pos1 == null || pos2 == null || pos3 == null) {
            throw new MissingPosDataException();
        }
        return SelectionCreations.createTetrahedron(pos0, pos1, pos2, pos3);
    }

}
