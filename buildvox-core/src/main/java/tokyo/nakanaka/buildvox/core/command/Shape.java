package tokyo.nakanaka.buildvox.core.command;

import picocli.CommandLine.Option;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;
import tokyo.nakanaka.buildvox.core.selectionShape.util.SelectionShapes;

import java.util.*;

import static picocli.CommandLine.ITypeConverter;

public class Shape {
    @Option(names = {"-s", "--shape"}, description = "selection shape", completionCandidates = Candidates.class,
            converter = Converter.class)
    private SelectionShape shape;

    public SelectionShape shape() {
        return shape;
    }

    public static class Candidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            Map<String, Map<String, List<String>>> nameKeysValues = getNameKeysValues();
            Set<String> candidates = new HashSet<>();
            for(String shapeName : nameKeysValues.keySet()) {
                candidates.add(shapeName);
                Map<String, List<String>> keysValues = nameKeysValues.get(shapeName);
                if(keysValues.size() != 0) {
                    List<Set<String>> keyValueSetList = new ArrayList<>();
                    for (String key : keysValues.keySet()) {
                        Set<String> keyValueSet = new HashSet<>();
                        List<String> values = keysValues.get(key);
                        for (String value : values) {
                            keyValueSet.add(key + "=" + value);
                        }
                        keyValueSetList.add(keyValueSet);
                    }
                    Set<String> stateCandidates = new HashSet<>();
                    for (Set<String> keyValueSet : keyValueSetList) {
                        if (stateCandidates.size() == 0) {
                            stateCandidates.addAll(keyValueSet);
                        } else {
                            Set<String> clone = new HashSet<>(stateCandidates);
                            stateCandidates = new HashSet<>();
                            for (String keyValue : keyValueSet) {
                                for (String e : clone) {
                                    stateCandidates.add(e + "," + keyValue);
                                }
                            }
                        }
                    }
                    for(String state : stateCandidates) {
                        candidates.add(shapeName + "[" + state + "]");
                    }
                }
            }
            return candidates.iterator();
        }

        public Map<String, Map<String, List<String>>> getNameKeysValues() {
            Map<String, Map<String, List<String>>> nameKeysValues = new HashMap<>();
            Set<String> shapeNames = SelectionShapes.getShapeNames();
            for(String shapeName : shapeNames) {
                Map<String, List<String>> keysValues = new HashMap<>();
                for(String key : SelectionShapes.getKeys(shapeName)) {
                    keysValues.put(key, SelectionShapes.getValues(shapeName, key));
                }
                nameKeysValues.put(shapeName, keysValues);
            }
            return nameKeysValues;
        }


    }

    public static class Converter implements ITypeConverter<SelectionShape> {
        @Override
        public SelectionShape convert(String value) throws Exception {
            return SelectionShapes.parseSelectionShape(value);
        }
    }

}