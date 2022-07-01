package tokyo.nakanaka.buildvox.core.command;

import static picocli.CommandLine.*;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.ParseUtils;
import tokyo.nakanaka.buildvox.core.command.mixin.shapeMixin.*;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShapeImpl;

import java.util.*;

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
            ParseUtils.NameStateEntity nse = ParseUtils.parseNameStateEntity(value);
            if(!nse.entity().isEmpty()) throw new Exception();
            String name = nse.name();
            Class<? extends ShapeMixin> shapeClazz = shapeMap.get(name);
            if(shapeClazz == null) throw new Exception();
            CommandLine cmdLine = new CommandLine(new DummyCommand());
            cmdLine.addMixin(name, shapeClazz.getDeclaredConstructor().newInstance());
            List<String> argsList = new ArrayList<>();
            String state = nse.state();
            Map<String, String> stateMap = ParseUtils.parseStateMap(state);
            for(Map.Entry<String, String> e : stateMap.entrySet()) {
                argsList.add("--" + e.getKey());
                argsList.add(e.getValue());
            }
            String[] args;
            args = argsList.toArray(new String[0]);
            cmdLine.execute(args);
            Map<String, Object> mixins = cmdLine.getMixins();
            ShapeMixin shapeMixin = (ShapeMixin) mixins.get(name);
            return new SelectionShapeImpl(shapeMixin);
        }
    }

    @Command
    private static class DummyCommand implements Runnable {
        @Override
        public void run() {

        }
    }

}