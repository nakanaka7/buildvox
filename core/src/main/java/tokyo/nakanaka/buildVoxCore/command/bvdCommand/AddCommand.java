package tokyo.nakanaka.buildVoxCore.command.bvdCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.system.BuildVoxSystem;
import tokyo.nakanaka.buildVoxCore.system.DummyPlayerRepository;

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
        DummyPlayerRepository playerRepo = BuildVoxSystem.DUMMY_PLAYER_REPOSITORY;
        if(playerRepo.nameSet().contains(playerName)){
            err.println(FeedbackMessage.ofDummyPlayerAlreadyExistError(playerName));
            return;
        }
        playerRepo.create(playerName);
        out.println(FeedbackMessage.ofAddNewDummyPlayerExit(playerName));
    }

}
