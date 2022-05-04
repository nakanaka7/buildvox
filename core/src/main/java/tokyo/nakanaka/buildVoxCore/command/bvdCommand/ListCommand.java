package tokyo.nakanaka.buildVoxCore.command.bvdCommand;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.FeedbackMessage;
import tokyo.nakanaka.buildVoxCore.system.BuildVoxSystem;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(name = "list",
        mixinStandardHelpOptions = true,
        description = "List dummy player(s).")
public class ListCommand implements Runnable {
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec commandSpec;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        List<String> playerNameList = new ArrayList<>(BuildVoxSystem.DUMMY_PLAYER_REPOSITORY.nameSet());
        out.println(FeedbackMessage.ofDummyPlayerListExit(playerNameList));
    }

}
