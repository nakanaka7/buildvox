package tokyo.nakanaka.buildvox.core.command.bvCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.player.PlayerEntity;
import tokyo.nakanaka.buildvox.core.player.Player;

import java.io.PrintWriter;

@Command(name = "pos-marker", mixinStandardHelpOptions = true,
        description = "Get a pos marker."
)
public class PosMarkerCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @ParentCommand
    private BvCommand bvCmd;

    @Override
    public void run() {
        PrintWriter err = commandSpec.commandLine().getErr();
        Player client = bvCmd.getPlayer();
        PlayerEntity player = client.getPlayerEntity();
        if(player == null){
            err.println(Messages.CANNOT_FIND_PLAYER_ERROR);
        }else {
            player.givePosMarker();
        }
    }

}
