package tokyo.nakanaka.buildvox.core.command;

import picocli.CommandLine.ITypeConverter;
import tokyo.nakanaka.buildvox.core.command.mixin.shapeMixin.*;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SelectionShapeParameter {
    private static final Map<String, Class<? extends ShapeMixin>> shapeMap = new HashMap<>();

    static {
        shapeMap.put("cone", ConeMixin.class);
        shapeMap.put("cuboid", CuboidMixin.class);
        shapeMap.put("cylinder", CylinderMixin.class);
        shapeMap.put("ellipse", EllipseMixin.class);
        shapeMap.put("frame", FrameMixin.class);
        shapeMap.put("line", LineMixin.class);
        shapeMap.put("plate", PlateMixin.class);
        shapeMap.put("pyramid", PyramidMixin.class);
        shapeMap.put("tetrahedron", TetrahedronMixin.class);
        shapeMap.put("torus", TorusMixin.class);
        shapeMap.put("triangle", TriangleMixin.class);
    }

    private SelectionShapeParameter() {
    }

    public static class Candidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            Set<String> shapes = shapeMap.keySet();
            return shapes.iterator();
        }
    }

    public static class Converter implements ITypeConverter<SelectionShape> {
        @Override
        public SelectionShape convert(String value) throws Exception {
            return null;
        }
    }

}