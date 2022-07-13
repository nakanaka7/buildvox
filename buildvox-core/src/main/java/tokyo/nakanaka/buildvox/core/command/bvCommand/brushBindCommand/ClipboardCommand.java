package tokyo.nakanaka.buildvox.core.command.bvCommand.brushBindCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.Clipboard;
import tokyo.nakanaka.buildvox.core.brushSource.BrushSource;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "clipboard",
        mixinStandardHelpOptions = true,
        description = "Binds clipboard to brush.")
public class ClipboardCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    @CommandLine.ParentCommand
    private BrushBindCommand brushBindCmd;

    @Override
    public void run() {
        PrintWriter out = spec.commandLine().getOut();
        PrintWriter err = spec.commandLine().getErr();
        BvCommand bvCmd = brushBindCmd.getBvCommand();
        Player player = bvCmd.getPlayer();
        Clipboard clip = player.getClipboard();
        if(clip == null) {
            err.println("No clipboard.");
            return;
        }
        BrushSource bs = new BrushSource(clip);
        player.setBrushSource(bs);
        out.println("Bound current clipboard to brush.");
    }

}
