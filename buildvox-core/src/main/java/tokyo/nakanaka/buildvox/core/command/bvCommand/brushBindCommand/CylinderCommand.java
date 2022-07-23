package tokyo.nakanaka.buildvox.core.command.bvCommand.brushBindCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSource;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSourceClipboards;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.Block;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

@Command(name = "cylinder",
        mixinStandardHelpOptions = true,
        description = "Binds a cylinder to the brush")
public class CylinderCommand implements Runnable {
    @Spec
    private Model.CommandSpec spec;

    @ParentCommand
    private BrushBindCommand brushBindCmd;

    @Mixin
    private Block block;

    @Parameters(description = "diameter(default = 3)",
            defaultValue = "3",
            completionCandidates = PositiveIntegerCandidates.class,
            converter = PositiveIntegerConverter.class)
    private int diameter;

    @Parameters(description = "thickness(default = 1)",
            defaultValue = "1",
            completionCandidates = PositiveIntegerCandidates.class,
            converter = PositiveIntegerConverter.class
    )
    private int thickness;

    @Option(names = {"--axis", "-a"})
    private Axis axis = Axis.Y;

    @Override
    public void run() {
        PrintWriter out = spec.commandLine().getOut();
        BvCommand bvCmd = brushBindCmd.getBvCommand();
        Player player = bvCmd.getPlayer();
        Clipboard clipboard = BrushSourceClipboards.createCylinder(block.block(), axis, diameter, thickness);
        BrushSource src = new BrushSource(clipboard, brushBindCmd.getBlockSettingOptions());
        player.setBrushSource(src);
        out.println("Bound " + createBrushDescription() + " to brush.");
    }

    private String createBrushDescription() {
        String blockStr = "block=" + block.block();
        String diameterStr = "diameter=" + diameter;
        String thicknessStr = "thickness=" + thickness;
        String axisStr = "axis=" + axis;
        return "cylinder[" + String.join(",", blockStr, diameterStr, thicknessStr, axisStr) + "]";
    }

    private static class PositiveIntegerCandidates implements Iterable<String> {
        @Override
        public Iterator<String> iterator() {
            return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9").iterator();
        }
    }

    private static class PositiveIntegerConverter implements ITypeConverter<Integer> {
        @Override
        public Integer convert(String value) {
            int i = Integer.parseInt(value);
            if(i < 1) throw new IllegalArgumentException();
            return i;
        }
    }

}
