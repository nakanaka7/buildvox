package tokyo.nakanaka.buildvox.core.command.bvCommand.posArray;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;

@CommandLine.Command(name = "pos-array", mixinStandardHelpOptions = true,
        description = "Pos array commands.",
        subcommands = {CreateCommand.class, ClearCommand.class, PrintCommand.class}
)
public class PosArrayCommand {
    @CommandLine.ParentCommand
    private BvCommand bvCmd;

    public BvCommand getParentCommand() {
        return bvCmd;
    }

}
