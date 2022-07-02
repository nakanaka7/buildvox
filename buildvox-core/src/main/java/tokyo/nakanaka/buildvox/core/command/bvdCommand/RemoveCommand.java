package tokyo.nakanaka.buildvox.core.command.bvdCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.Messages;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;

import java.io.PrintWriter;

@Command(name = "remove",
        mixinStandardHelpOptions = true,
        description = "Remove a dummy player.")
public class RemoveCommand implements Runnable {
    @Spec
    private Model.CommandSpec commandSpec;
    @Parameters(description = "A dummy player name to add.")
    private String playerName;
    @Override
    public void run() {
        PrintWriter out = commandSpec.commandLine().getOut();
        PrintWriter err = commandSpec.commandLine().getErr();
        var dummyPlayerRepository = BuildVoxSystem.getDummyPlayerRegistry();
        if(!dummyPlayerRepository.idList().contains(playerName)){
            err.println(Messages.ofNotFoundDummyPlayerError(playerName));
        }else{
            dummyPlayerRepository.unregister(playerName);
            out.println(Messages.ofRemoveDummyPlayerExit(playerName));
        }
    }

}
