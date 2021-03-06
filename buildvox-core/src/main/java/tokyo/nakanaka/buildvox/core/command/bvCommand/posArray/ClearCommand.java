package tokyo.nakanaka.buildvox.core.command.bvCommand.posArray;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@Command(name = "clear", mixinStandardHelpOptions = true,
        description = "Clear pos array."
)
public class ClearCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private PosArrayCommand posArrayCmd;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        BvCommand bvCmd = posArrayCmd.getParentCommand();
        Player player = bvCmd.getPlayer();
        Vector3i[] posArray = player.getPosArrayClone();
        if(player.getSelection() == null) {
            player.setPosArray(new Vector3i[posArray.length]);
        }
        out.println("Cleared pos array.");
    }

}
