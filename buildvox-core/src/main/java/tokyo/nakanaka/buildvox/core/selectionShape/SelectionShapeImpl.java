package tokyo.nakanaka.buildvox.core.selectionShape;

import tokyo.nakanaka.buildvox.core.selectionShape.shape.Shape;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

public class SelectionShapeImpl implements SelectionShape {
    private Shape shape;

    public SelectionShapeImpl(Shape shape) {
        this.shape = shape;
    }

    /**
     * Creates a selection by the pos-array.
     * @param posArray the pos-array. All the elements should non-null.
     * @return a selection.
     * @throws PosArrayLengthException if the pos array length is invalid.
     */
    @Override
    public Selection createSelection(Vector3i[] posArray) {
        return shape.createSelection(posArray);
    }

}
