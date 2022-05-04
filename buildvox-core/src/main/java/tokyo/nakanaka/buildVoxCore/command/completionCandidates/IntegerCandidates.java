package tokyo.nakanaka.buildVoxCore.command.completionCandidates;

import java.util.Iterator;
import java.util.List;

public class IntegerCandidates implements Iterable<String>{
    @Override
    public Iterator<String> iterator() {
        List<String> list = List.of("-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0",
                "1", "2", "3", "4", "5", "6", "7", "8", "9");
        return list.iterator();
    }
}
