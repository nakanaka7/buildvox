package tokyo.nakanaka.buildvox.core.selectionShape.util;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model;
import tokyo.nakanaka.buildvox.core.ParseUtils;
import tokyo.nakanaka.buildvox.core.selectionShape.*;

import java.util.*;

/**
 * A utility class for SelectionShape.
 */
public class SelectionShapes {
    private static final Map<String, Class<? extends SelectionShape>> shapeMap = new HashMap<>();

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

    public static List<String> getKeys(String shapeName) {
        List<Model.OptionSpec> options = getOptionSpecs(shapeName);
        List<String> keys = new ArrayList<>();
        for(var opt : options) {
            String longName = opt.longestName();
            keys.add(longName.substring(2));
        }
        return keys;
    }

    public static List<String> getValues(String shapeName, String key) {
        List<Model.OptionSpec> optionSpecs = getOptionSpecs(shapeName);
        for(var spec : optionSpecs) {
            if(spec.longestName().equals("--" + key)) {
                Iterable<String> candidates = spec.completionCandidates();
                List<String> list = new ArrayList<>();
                for(var e : candidates) {
                    list.add(e);
                }
                return list;
            }
        }
        return List.of();
    }

    private static List<Model.OptionSpec> getOptionSpecs(String shapeName) {
        Class<? extends SelectionShape> shapeClazz = shapeMap.get(shapeName);
        if(shapeClazz == null) return new ArrayList<>();
        CommandLine cmdLine = new CommandLine(new DummyCommand());
        try {
            cmdLine.addMixin(shapeName, shapeClazz.getDeclaredConstructor().newInstance());
        }catch (Exception ex) {
            return new ArrayList<>();
        }
        Model.CommandSpec spec = cmdLine.getCommandSpec();
        return spec.options();
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
        Class<? extends SelectionShape> shapeClazz = shapeMap.get(name);
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
        SelectionShape shape = (SelectionShape) mixins.get(name);
        return shape;
    }

    @Command
    private static class DummyCommand implements Runnable {
        @Override
        public void run() {

        }
    }

}
