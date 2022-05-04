package tokyo.nakanaka.buildVoxCore.system.commandHandler;

import picocli.AutoComplete;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<String> getTabCompletionList(CommandLine.Model.CommandSpec spec, String[] args) {
        int argIndex = args.length - 1;
        List<CharSequence> candidates = new ArrayList<>();
        int positionInArg = 0;
        int cursor = 0;
        AutoComplete.complete(spec, args, argIndex, positionInArg, cursor, candidates);
        List<String> list = new ArrayList<>();
        String lastArg = args[argIndex];
        for (CharSequence s0 : candidates) {
            String s = s0.toString();
            boolean put = false;
            if(s.startsWith(lastArg)) {
                put = true;
            }else {
                String m = "minecraft:";
                if (s.startsWith(m) && !lastArg.startsWith(m)) {
                    if (s.startsWith(m + lastArg)) {
                        put = true;
                    }
                }
            }
            if(put){
                list.add(s);
            }
        }
        return list;
    }

}
