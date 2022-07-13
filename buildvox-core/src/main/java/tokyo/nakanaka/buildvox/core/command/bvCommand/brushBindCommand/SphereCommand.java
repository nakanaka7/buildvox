package tokyo.nakanaka.buildvox.core.command.bvCommand.brushBindCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.brushSource.SphereBrushSource;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.Block;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

@Command(name = "sphere")
public class SphereCommand implements Runnable {
    @Spec
    private Model.CommandSpec spec;

    @ParentCommand
    private BrushBindCommand brushBindCmd;

    @Mixin
    private Block block;

    @Mixin
    private Size size;

    @Override
    public void run() {
        PrintWriter out = spec.commandLine().getOut();
        BvCommand bvCmd = brushBindCmd.getBvCommand();
        Player player = bvCmd.getPlayer();
        var sbs = SphereBrushSource.newInstance(block.block(), size.size());
        player.setBrushSource(sbs);
        out.println("Bound sphere(" + "block:" + block.block().getBlockId().name() + ",size:" + size.size() + ") to brush.");
    }

    @Command
    private static class Size {
        @Parameters(completionCandidates = Candidates.class, converter = Converter.class)
        private int size;

        public int size() {
            return size;
        }

        private static class Candidates implements Iterable<String> {
            @Override
            public Iterator<String> iterator() {
                return List.of("1", "2", "3", "4", "5", "6").iterator();
            }
        }

        private static class Converter implements ITypeConverter<Integer> {
            @Override
            public Integer convert(String value) {
                int i = Integer.parseInt(value);
                if(i < 1) throw new IllegalArgumentException();
                return i;
            }
        }

    }

}