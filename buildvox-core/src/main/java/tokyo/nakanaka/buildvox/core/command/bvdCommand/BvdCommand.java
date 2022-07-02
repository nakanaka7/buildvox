package tokyo.nakanaka.buildvox.core.command.bvdCommand;

import picocli.CommandLine.*;

@Command(name = "bv",
        mixinStandardHelpOptions = true,
        description = "Dummy player commands for /bv command",
        subcommands = {AddCommand.class, RemoveCommand.class, ListCommand.class}
)
public class BvdCommand {

}
