package tokyo.nakanaka.buildvox.core.command.util;

import java.util.Iterator;
import java.util.List;

public class NumberCompletionCandidates {
    private NumberCompletionCandidates() {
    }

    public static class Integer implements Iterable<String>{
        @Override
        public Iterator<String> iterator() {
            List<String> list = List.of("-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0",
                    "1", "2", "3", "4", "5", "6", "7", "8", "9");
            return list.iterator();
        }
    }

    public static class PositiveInteger implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9").iterator();
        }
    }

    public static class Double implements Iterable<String>{
        @Override
        public Iterator<String> iterator() {
            return List.of("-9.5", "-9.0", "-8.5", "-8.0", "-7.5", "-7.0", "-6.5", "-6.0", "-5.5", "-5.0",
                    "-4.5", "-4.0", "-3.5", "-3.0", "-2.5", "-2.0", "-1.5", "-1.0", "-0.5",
                    "0.0", "0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5",
                    "5.0", "5.5", "6.0", "6.5", "7.0", "7.5", "8.0", "8.5", "9.0", "9.5").iterator();
        }
    }

    public static class PositiveDouble implements Iterable<String>{
        @Override
        public Iterator<String> iterator() {
            return List.of("0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5",
                    "5.0", "5.5", "6.0", "6.5", "7.0", "7.5", "8.0", "8.5", "9.0", "9.5").iterator();
        }
    }

}
