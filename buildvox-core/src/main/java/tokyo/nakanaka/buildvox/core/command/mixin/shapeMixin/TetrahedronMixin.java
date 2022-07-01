package tokyo.nakanaka.buildvox.core.command.mixin.shapeMixin;

import tokyo.nakanaka.buildvox.core.command.MissingPosDataException;
import tokyo.nakanaka.buildvox.core.command.PosArrayLengthException;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;
import tokyo.nakanaka.buildvox.core.selection.SelectionCreations;

public class TetrahedronMixin implements ShapeMixin {

    public static final String DESCRIPTION = "a tetrahedron region which vertexes are pos0, pos1, pos2, and pos3";

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        if (posArray.length != 4) {
            throw new PosArrayLengthException(4);
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
