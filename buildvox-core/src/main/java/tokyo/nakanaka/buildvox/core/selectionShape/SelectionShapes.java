package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.ParseUtils;
import tokyo.nakanaka.buildvox.core.selectionShape.shape.*;

import java.util.*;

/**
 * A utility class for SelectionShape.
 */
public class SelectionShapes {
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

    /** Gets shape names. */
    public static Set<String> getShapeNames() {
        return new HashSet<>(shapeMap.keySet());
    }

    /**
     * Parses selection shape. Its format is "name[key=value,...]".
     * @param value the input.
     * @return a selection shape if possible.
     * @throws Exception if failed to parse.
     */
    public static SelectionShape parseSelectionShape(String value) throws Exception {
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

    @CommandLine.Command
    private static class DummyCommand implements Runnable {
        @Override
        public void run() {

        }
    }

}
