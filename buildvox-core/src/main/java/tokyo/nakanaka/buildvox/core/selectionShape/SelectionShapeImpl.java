package tokyo.nakanaka.buildvox.core.selectionShape;

import tokyo.nakanaka.buildvox.core.command.mixin.shapeMixin.ShapeMixin;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.selection.Selection;

public class SelectionShapeImpl implements SelectionShape {
    private ShapeMixin shapeMixin;

    public SelectionShapeImpl(ShapeMixin shapeMixin) {
        this.shapeMixin = shapeMixin;
    }

    @Override
    public Selection createSelection(Vector3i[] posArray) {
        return shapeMixin.createSelection(posArray);
    }

}
