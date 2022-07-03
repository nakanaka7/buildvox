package tokyo.nakanaka.buildvox.core.selectionShape;

import tokyo.nakanaka.buildvox.core.selectionShape.shape.Shape;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

public class SelectionShapeImpl implements SelectionShape {
    private Shape shape;

    public SelectionShapeImpl(Shape shape) {
        this.shape = shape;
    }

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        return shape.createSelection(posArray);
    }

}
