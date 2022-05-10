package tokyo.nakanaka.buildvox.core.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.*;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockStateTransformer;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.bvdCommand.BvdCommand;
import tokyo.nakanaka.buildvox.core.commandSender.CommandSender;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
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

    /** Get the config */
    public static Config getConfig() {
        return config;
    }

    /** Get the world registry */
    public static WorldRegistry getWorldRegistry() {
        return WORLD_REGISTRY;
    }

    /** Get the player repository */
    public static PlayerRepository getPlayerRepository() {
        return PLAYER_REPOSITORY;
    }

    /**
     * Run "/bv" command.
     * @param playerId the id of a player who run the command. When a command is run by a non-player like command block
     * or console, set null.
     * @param pos the command execution position.
     * @param args the arguments of the command.
     * @param messageReceiver the receiver the command feedback messages.
     * @throws IllegalArgumentException if the player id is not registered. If the id is null, an exception will not
     * be thrown.
     */
    public static void onBvCommand(String[] args, World world, Vector3i pos, MessageReceiver messageReceiver, UUID playerId) {
        if(playerId != null){
            Player execPlayer = PLAYER_REPOSITORY.get(playerId);
            onBvCommand(execPlayer, args, world, pos);
            return;
        }
        CommandSender sender = new CommandSender() {
            @Override
            public void sendOutMessage(String msg) {
                messageReceiver.println(config.outColor() + msg);
            }

            @Override
            public void sendErrMessage(String msg) {
                messageReceiver.println(config.errColor() + msg);
            }
        };
        onBvCommand(sender, args, world, pos);
    }

    public static void onBvCommand(CommandSender sender, String[] args) {
        onBvCommand(sender, args, sender.getWorld(), sender.getBlockPos());
    }

    private static void onBvCommand(CommandSender sender, String[] args, World execWorld, Vector3i execPos) {
        Writer outWriter = BuildVoxWriter.newOutInstance(sender);
        Writer errWriter = BuildVoxWriter.newErrInstance(sender);
        PrintWriter out = new PrintWriter(outWriter, true);
        PrintWriter err = new PrintWriter(errWriter, true);
        BvCommand bvCmd;
        if(sender instanceof Player execPlayer) {
            bvCmd = new BvCommand(execPlayer, execWorld, execPos);
        }else {
            bvCmd = new BvCommand(null, execWorld, execPos);
        }
        out.println("Running \"/bv " + String.join(" ", args) + "\"...");
        new CommandLine(bvCmd)
                .setOut(out)
                .setErr(err)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .setExecutionStrategy(bvCmd::executionStrategy)
                .execute(args);
        BuildVoxSystem.PARTICLE_GUI_REPOSITORY.update(bvCmd.getTargetPlayer());
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
        BvCommand bvCmd = new BvCommand(null, null, new Vector3i(0, 0, 0));
        CommandLine.Model.CommandSpec spec
                = new CommandLine(bvCmd)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .getCommandSpec();
        return Util.getTabCompletionList(spec, args);
    }

    /** Run "/bvd" command. */
    public static void onBvdCommand(String[] args, MessageReceiver messageReceiver) {
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

    /** Run "/bvd" command. */
    public static void onBvdCommand(String[] args, CommandSender cmdSender) {
        Writer outWriter = BuildVoxWriter.newOutInstance(cmdSender);
        Writer errWriter = BuildVoxWriter.newErrInstance(cmdSender);
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

    /**
     * Handles a left-clicking block event by pos marker.
     * @param player the player who invoked this event.
     * @param pos the position of the clicked block.
     */
    public static void onLeftClickBlockByPosMarker(Player player, World world, Vector3i pos) {
        Vector3i[] posData = new Vector3i[player.getPosArrayClone().length];
        posData[0] = pos;
        player.setPosArrayWithSelectionNull(world, posData);
        PARTICLE_GUI_REPOSITORY.update(player);
        player.getPlayerEntity().println(config.outColor() + FeedbackMessage.ofPosExit(0, pos.x(), pos.y(), pos.z()));
    }

    /**
     * Handles a right-clicking block event by pos marker.
     * @param player  a player who invoked this event.
     * @param pos the position of the clicked block.
     */
    public static void onRightClickBlockByPosMarker(Player player, World world, Vector3i pos) {
        World posOrSelectionWorld = player.getWorld();
        Vector3i[] posData = player.getPosArrayClone();
        if (world != posOrSelectionWorld) {
            posData = new Vector3i[player.getPosArrayClone().length];
        }
        int dataSize = posData.length;
        int index = dataSize - 1;
        for (int i = 0; i < dataSize; ++i) {
            if (posData[i] == null) {
                index = i;
                break;
            }
        }
        posData[index] = pos;
        player.setPosArrayWithSelectionNull(world, posData);
        PARTICLE_GUI_REPOSITORY.update(player);
        player.getPlayerEntity().println(config.outColor() + FeedbackMessage.ofPosExit(index, pos.x(), pos.y(), pos.z()));
    }

}