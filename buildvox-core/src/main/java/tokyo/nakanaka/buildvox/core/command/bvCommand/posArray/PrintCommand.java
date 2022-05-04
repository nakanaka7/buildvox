package tokyo.nakanaka.buildvox.core.command.bvCommand.posArray;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(name = "print", mixinStandardHelpOptions = true,
        description = "Print pos array."
)
public class PrintCommand implements Runnable {
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
        List<String> strPosList = new ArrayList<>();
        for(var pos : posArray) {
            strPosList.add(stringOfPos(pos));
        }
        out.println(String.join(", ", strPosList));
    }

    private static String stringOfPos(Vector3i pos) {
        String xyz;
        if(pos == null) {
            xyz = "";
        }else {
            xyz = pos.x() + " / " + pos.y() + " / " + pos.z();
        }
        return "{" + xyz + "}";
    }

}
