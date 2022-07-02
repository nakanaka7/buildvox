package tokyo.nakanaka.buildvox.core.command.bvdCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.player.DummyPlayer;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.io.PrintWriter;

@Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a dummy player.")
public class AddCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @Parameters(description = "A dummy player name to add.")
    private String playerName;
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        var playerRepo = BuildVoxSystem.getDummyPlayerRegistry();
        if(playerRepo.idList().contains(playerName)){
            err.println(Messages.ofDummyPlayerAlreadyExistError(playerName));
            return;
        }
        playerRepo.register(new DummyPlayer(playerName));
        out.println(Messages.ofAddNewDummyPlayerExit(playerName));
    }

}
