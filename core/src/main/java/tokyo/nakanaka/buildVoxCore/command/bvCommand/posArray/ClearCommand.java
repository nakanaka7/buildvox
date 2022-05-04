package tokyo.nakanaka.buildVoxCore.command.bvCommand.posArray;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildVoxCore.math.vector.Vector3i;
import tokyo.nakanaka.buildVoxCore.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "clear", mixinStandardHelpOptions = true,
        description = "Clear pos array."
)
public class ClearCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private PosArrayCommand posArrayCmd;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        BvCommand bvCmd = posArrayCmd.getParentCommand();
        Player player = bvCmd.getPlayer();
        Vector3i[] posArray = player.getPosArrayClone();
        if(player.getSelection() == null) {
            player.setPosArrayWithSelectionNull(player.getWorld(), new Vector3i[posArray.length]);
        }
        out.println("Cleared pos array.");
    }

}
