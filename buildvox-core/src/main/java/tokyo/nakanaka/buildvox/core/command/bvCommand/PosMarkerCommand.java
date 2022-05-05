package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.PlayerEntity;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@CommandLine.Command(name = "pos-marker", mixinStandardHelpOptions = true,
        description = "Get a pos marker."
)
public class PosMarkerCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.ParentCommand
    private BvCommand bvCmd;

    @Override
    public void run() {
        PrintWriter err = commandSpec.commandLine().getErr();
        Player client = bvCmd.getPlayer();
        PlayerEntity player = client.getPlayerEntity();
        if(player == null){
            err.println(FeedbackMessage.CANNOT_FIND_PLAYER_ERROR);
        }else {
            player.givePosMarker();
        }
    }

}