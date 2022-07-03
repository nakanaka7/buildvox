package tokyo.nakanaka.buildvox.core.command.bvdCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.io.PrintWriter;
import java.util.List;

@Command(name = "list",
        mixinStandardHelpOptions = true,
        description = "List dummy player(s).")
public class ListCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;

    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        List<String> playerNameList = BuildVoxSystem.getDummyPlayerRegistry().idList();
        out.println(Messages.ofDummyPlayerListExit(playerNameList));
    }

}
