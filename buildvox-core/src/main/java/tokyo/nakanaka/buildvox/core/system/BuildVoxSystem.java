package tokyo.nakanaka.buildvox.core.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.*;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockStateTransformer;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.bvdCommand.BvdCommand;
import tokyo.nakanaka.buildvox.core.system.clickBlockEventHandler.PosMarkerClickBlockEventHandler;
import tokyo.nakanaka.buildvox.core.world.Block;
import tokyo.nakanaka.buildvox.core.world.World;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.UUID;

/**
 * The entrypoint for the platforms which use BuildVox Core project.
 */
public class BuildVoxSystem {
    public static final Logger CORE_LOGGER = LoggerFactory.getLogger("BuildVoxCore");
    /** The environment of this system */
    public static Environment environment = Environment.DEFAULT;
    /** The config of this system */
    public static Config config = Config.DEFAULT;
    /** World registry */
    public static final WorldRegistry WORLD_REGISTRY = new WorldRegistry();
    /** Block registry */
    public static final BlockRegistry BLOCK_REGISTRY = new BlockRegistry();
    /** Player repository */
    public static final PlayerRepository PLAYER_REPOSITORY = new PlayerRepository();
    public static final DummyPlayerRepository DUMMY_PLAYER_REPOSITORY = new DummyPlayerRepository();
    public static final ParticleGuiRepository PARTICLE_GUI_REPOSITORY = new ParticleGuiRepository();

    private BuildVoxSystem() {
    }

    public static record Environment(BlockValidator blockValidator, BlockStateTransformer blockStateTransformer,
                                     Scheduler scheduler) {
        public static final Environment DEFAULT = new Environment(
                (block) -> false,
                (blockId, stateMap, trans) -> stateMap,
                (runnable, tick) -> {});
    }

    public static record Config(String outColor, String errColor, Block backgroundBlock, int posArrayLength) {
        public static final Config DEFAULT = new Config(ColorCode.GREEN, ColorCode.RED, Block.valueOf("minecraft:air"), 2);
    }

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
    public static void onBvCommand(String[] args, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver, UUID playerId) {
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
     * Returns String list of "/bv" command's tab completion.
     * This method has an issue about positional parameters due to picocli. (picocli Issues #1018)
     * @param args an arguments of the command.
     * @throws IllegalArgumentException if this system does not contain the player data of playerId.
     * @throws IllegalArgumentException if the player id is not registered. If the id is null, an exception will not
     * be thrown.
     */
    public static List<String> onBvTabComplete(String[] args) {
        BvCommand bvCmd = new BvCommand(null, null, 0, 0, 0);
        CommandLine.Model.CommandSpec spec
                = new CommandLine(bvCmd)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .getCommandSpec();
        return Util.getTabCompletionList(spec, args);
    }

    /** Run "/bvd" command. */
    public static void onBvdCommand(String[] args, NamespacedId worldId, int x, int y, int z,
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

    /** Returns String list of "/bvd" command's tab completion. */
    public static List<String> onBvdTabComplete(String[] args) {
        CommandLine.Model.CommandSpec spec
                = new CommandLine(new BvdCommand())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .getCommandSpec();
        return Util.getTabCompletionList(spec, args);
    }

    /** Handles a left-clicking block event by pos marker. */
    public static void onLeftClickBlockByPosMarker(UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver) {
        new PosMarkerClickBlockEventHandler().onLeft(playerId, worldId, x, y, z, messageReceiver);
    }

    /** Handles a right-clicking block event by pos marker. */
    public static void onRightClickBlockByPosMarker(UUID playerId, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver) {
        new PosMarkerClickBlockEventHandler().onRight(playerId, worldId, x, y, z, messageReceiver);
    }

}
