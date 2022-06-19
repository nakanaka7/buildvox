package tokyo.nakanaka.buildvox.core.command.bvdCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.player.DummyPlayer;
import tokyo.nakanaka.buildvox.core.BuildVoxSystem;

import java.io.PrintWriter;

@CommandLine.Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a dummy player.")
public class AddCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.Parameters(description = "A dummy player name to add.")
    private String playerName;
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        var playerRepo = BuildVoxSystem.getDummyPlayerRegistry();
        if(playerRepo.idList().contains(playerName)){
            err.println(FeedbackMessage.ofDummyPlayerAlreadyExistError(playerName));
            return;
        }
        playerRepo.register(new DummyPlayer(playerName));
        out.println(FeedbackMessage.ofAddNewDummyPlayerExit(playerName));
    }

}
