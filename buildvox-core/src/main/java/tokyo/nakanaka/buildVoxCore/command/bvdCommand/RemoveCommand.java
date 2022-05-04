package tokyo.nakanaka.buildVoxCore.command.bvdCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.system.BuildVoxSystem;
import tokyo.nakanaka.buildVoxCore.system.DummyPlayerRepository;

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
        DummyPlayerRepository dummyPlayerRepository = BuildVoxSystem.DUMMY_PLAYER_REPOSITORY;
        if(!dummyPlayerRepository.nameSet().contains(playerName)){
            err.println(FeedbackMessage.ofNotFoundDummyPlayerError(playerName));
        }else{
            dummyPlayerRepository.delete(playerName);
            out.println(FeedbackMessage.ofRemoveDummyPlayerExit(playerName));
        }
    }

}
