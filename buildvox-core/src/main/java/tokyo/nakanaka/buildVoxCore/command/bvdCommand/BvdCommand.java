package tokyo.nakanaka.buildVoxCore.command.bvdCommand;

import picocli.CommandLine;

@CommandLine.Command(name = "bv",
        mixinStandardHelpOptions = true,
        description = "Dummy player commands for /bv command",
        subcommands = {AddCommand.class, RemoveCommand.class, ListCommand.class}
)
public class BvdCommand {

}
