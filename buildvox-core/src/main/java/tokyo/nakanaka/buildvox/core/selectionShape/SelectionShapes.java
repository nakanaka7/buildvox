package tokyo.nakanaka.buildvox.core.selectionShape;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.ParseUtils;
import tokyo.nakanaka.buildvox.core.selectionShape.shape.*;

import java.util.*;

/**
 * A utility class for SelectionShape.
 */
public class SelectionShapes {
    private static final Map<String, Class<? extends Shape>> shapeMap = new HashMap<>();

    static {
        shapeMap.put("cone", Cone.class);
        shapeMap.put("cuboid", Cuboid.class);
        shapeMap.put("cylinder", Cylinder.class);
        shapeMap.put("ellipse", Ellipse.class);
        shapeMap.put("frame", Frame.class);
        shapeMap.put("line", Line.class);
        shapeMap.put("plate", Plate.class);
        shapeMap.put("pyramid", Pyramid.class);
        shapeMap.put("tetrahedron", Tetrahedron.class);
        shapeMap.put("torus", Torus.class);
        shapeMap.put("triangle", Triangle.class);
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
        Class<? extends Shape> shapeClazz = shapeMap.get(name);
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
        Shape shape = (Shape) mixins.get(name);
        return new SelectionShapeImpl(shape);
    }

    @CommandLine.Command
    private static class DummyCommand implements Runnable {
        @Override
        public void run() {

        }
    }

}
