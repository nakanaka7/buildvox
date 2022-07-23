package tokyo.nakanaka.buildvox.core.command.bvCommand.brushBindCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Axis;
import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSource;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSourceClipboards;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.mixin.Block;
import tokyo.nakanaka.buildvox.core.command.mixin.PositiveInteger;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

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
            completionCandidates = PositiveInteger.PositiveIntegerCandidates.class,
            converter = PositiveInteger.PositiveIntegerConverter.class)
    private int diameter;

    @Parameters(description = "thickness(default = 1)",
            defaultValue = "1",
            completionCandidates = PositiveInteger.PositiveIntegerCandidates.class,
            converter = PositiveInteger.PositiveIntegerConverter.class
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

}
