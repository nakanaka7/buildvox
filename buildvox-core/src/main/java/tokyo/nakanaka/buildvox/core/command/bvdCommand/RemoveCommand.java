package tokyo.nakanaka.buildvox.core.command.bvdCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.io.PrintWriter;

@CommandLine.Command(name = "remove",
        mixinStandardHelpOptions = true,
        description = "Remove a dummy player.")
public class RemoveCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;
    @CommandLine.Parameters(description = "A dummy player name to add.")
    private String playerName;
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        var dummyPlayerRepository = BuildVoxSystem.getDummyPlayerRegistry();
        if(!dummyPlayerRepository.idList().contains(playerName)){
            err.println(FeedbackMessage.ofNotFoundDummyPlayerError(playerName));
        }else{
            dummyPlayerRepository.unregister(playerName);
            out.println(FeedbackMessage.ofRemoveDummyPlayerExit(playerName));
        }
    }

}
