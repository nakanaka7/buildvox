package tokyo.nakanaka.buildvox.core.command;

import picocli.CommandLine.Option;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShape;
import tokyo.nakanaka.buildvox.core.selectionShape.SelectionShapes;

import java.util.Iterator;
import java.util.Set;

import static picocli.CommandLine.ITypeConverter;

public class Shape {
    @Option(names = {"-s", "--shape"}, completionCandidates = Candidates.class,
            converter = Converter.class)
    private SelectionShape shape;

    public SelectionShape shape() {
        return shape;
    }

    public static class Candidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            Set<String> shapes = SelectionShapes.shapeMap.keySet();
            return shapes.iterator();
        }
    }

    public static class Converter implements ITypeConverter<SelectionShape> {
        @Override
        public SelectionShape convert(String value) throws Exception {
            return SelectionShapes.parseSelectionShape(value);
        }
    }

}