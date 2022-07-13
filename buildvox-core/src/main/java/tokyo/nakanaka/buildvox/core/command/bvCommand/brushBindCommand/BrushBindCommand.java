package tokyo.nakanaka.buildvox.core.command.bvCommand.brushBindCommand;

import picocli.CommandLine.*;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;

@Command(name = "brush-bind",
        mixinStandardHelpOptions = true,
        description = "Binds brush type",
        subcommands = SphereCommand.class)
public class BrushBindCommand {
    @ParentCommand
    private BvCommand bvCmd;

    public BvCommand getBvCommand() {
        return bvCmd;
    }

}
