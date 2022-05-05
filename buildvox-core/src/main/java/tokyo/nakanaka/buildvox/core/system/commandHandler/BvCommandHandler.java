package tokyo.nakanaka.buildvox.core.system.commandHandler;

import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.World;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.UUID;

import static tokyo.nakanaka.buildvox.core.system.BuildVoxSystem.*;

public class BvCommandHandler implements CommandHandler {
    /**
     * Run "/bv" command.
     * @param playerId the id of a player who run the command. When a command is run by a non-player like command block
     * or console, set null.
     * @param worldId the id of the world where the command is run.
     * @param x the x-coordinate of the position where the command is run.
     * @param y the y-coordinate of the position where the command is run.
     * @param z the z-coordinate of the position where the command is run.
     * @param args the arguments of the command.
     * @param messageReceiver the receiver the command feedback messages.
     * @throws IllegalArgumentException if the player id is not registered. If the id is null, an exception will not
     * be thrown.
     * @throws IllegalArgumentException if the world id is not registered.
     */
    @Override
    public void onCommand(String[] args, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver, UUID playerId) {
        if(playerId != null && PLAYER_REPOSITORY.get(playerId) == null)throw new IllegalArgumentException();
        if(!BuildVoxSystem.WORLD_REGISTRY.worldIsRegistered(worldId)) {
            throw new IllegalArgumentException();
        }
        World world = BuildVoxSystem.WORLD_REGISTRY.get(worldId);
        Writer outWriter = new BuildVoxWriter(config.outColor(), messageReceiver);
        Writer errWriter = new BuildVoxWriter(config.errColor(), messageReceiver);
        PrintWriter out = new PrintWriter(outWriter, true);
        PrintWriter err = new PrintWriter(errWriter, true);
        out.println("Running \"/bv " + String.join(" ", args) + "\"...");
        BvCommand bvCmd = new BvCommand(playerId, world, x, y, z);
        new CommandLine(bvCmd)
                .setOut(out)
                .setErr(err)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .setExecutionStrategy(bvCmd::executionStrategy)
                .execute(args);
        BuildVoxSystem.PARTICLE_GUI_REPOSITORY.update(bvCmd.getPlayer());
    }

    /**
     * Gets the String list which is shown on tab completion of /bv command. This method has an issue about
     * positional parameters due to picocli. (picocli Issues #1018)
     * @param args an arguments of the command.
     * @throws IllegalArgumentException if this system does not contain the player data of playerId.
     * @throws IllegalArgumentException if the player id is not registered. If the id is null, an exception will not
     * be thrown.
     */
    //from https://issueexplorer.com/issue/remkop/picocli/1402
    @Override
    public List<String> onTabComplete(String[] args) {
        BvCommand bvCmd = new BvCommand(null, null, 0, 0, 0);
        CommandLine.Model.CommandSpec spec
                = new CommandLine(bvCmd)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .getCommandSpec();
        return Util.getTabCompletionList(spec, args);
    }

}
