package tokyo.nakanaka.buildVoxCore.system.commandHandler;

import picocli.CommandLine;
import tokyo.nakanaka.buildVoxCore.MessageReceiver;
import tokyo.nakanaka.buildVoxCore.NamespacedId;
import tokyo.nakanaka.buildVoxCore.command.bvdCommand.BvdCommand;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.UUID;

import static tokyo.nakanaka.buildVoxCore.system.BuildVoxSystem.config;

public class BvdCommandHandler implements CommandHandler {
    /**
     * Run "/bvd" command
     * @param args the arguments of the command.
     * @param messageReceiver the receiver the command feedback messages.
     */
    @Override
    public void onCommand(String[] args, NamespacedId worldId, int x, int y, int z,
                          MessageReceiver messageReceiver, UUID playerId) {
        Writer outWriter = new BuildVoxWriter(config.outColor(), messageReceiver);
        Writer errWriter = new BuildVoxWriter(config.errColor(), messageReceiver);
        PrintWriter out = new PrintWriter(outWriter, true);
        PrintWriter err = new PrintWriter(errWriter, true);
        new CommandLine(new BvdCommand())
                .setOut(out)
                .setErr(err)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
    }

    /**
     * Gets the String list which is shown on tab completion of /bvd command.
     * @param args the arguments of /bvd command.
     * @return the String list which is shown on tab complete of /bvd command.
     */
    @Override
    public List<String> onTabComplete(String[] args) {
        CommandLine.Model.CommandSpec spec
                = new CommandLine(new BvdCommand())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .getCommandSpec();
        return Util.getTabCompletionList(spec, args);
    }

}
