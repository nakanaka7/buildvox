package tokyo.nakanaka.buildvox.core.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.AutoComplete;
import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.ColorCode;
import tokyo.nakanaka.buildvox.core.FeedbackMessage;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.Scheduler;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockValidator;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.bvdCommand.BvdCommand;
import tokyo.nakanaka.buildvox.core.commandSender.CommandSender;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.DummyPlayer;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
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
    private static final Registry<World, NamespacedId> worldRegistry = new Registry<>();
    private static final Registry<Block<?,?>, NamespacedId> blockRegistry = new Registry<>();
    private static final Registry<RealPlayer, UUID> realPlayerRegistry = new Registry<>();
    private static final Registry<DummyPlayer, String> dummyPlayerRegistry = new Registry<>();

    private BuildVoxSystem() {
    }

    public static record Environment(BlockValidator blockValidator,
                                     Scheduler scheduler) {
        public static final Environment DEFAULT = new Environment(
                (block) -> false,
                (runnable, tick) -> {});
    }

    public static record Config(String outColor, String errColor, String backgroundBlock, int posArrayLength) {
        public static final Config DEFAULT = new Config(ColorCode.GREEN, ColorCode.RED, "minecraft:air", 2);
    }

    /** Get the config */
    public static Config getConfig() {
        return config;
    }

    /** Get the environment */
    public static Environment getEnvironment() {
        return environment;
    }

    /** Parses the String to a {@link VoxelBlock} */
    public static VoxelBlock parseBlock(String s) {
        return VoxelBlock.valueOf(s);
    }

    /** Set the scheduler */
    public static void setScheduler(Scheduler scheduler) {
        var e = new Environment(environment.blockValidator, scheduler);
        environment = e;
    }

    /** Get the scheduler */
    public static Scheduler getScheduler() {
        return environment.scheduler();
    }

    public static void setBlockValidator(BlockValidator blockValidator) {
        var e = new Environment(blockValidator, environment.scheduler());
        environment = e;
    }

    public static BlockValidator getBlockValidator() {
        return environment.blockValidator();
    }

    /** Get the world registry */
    public static Registry<World, NamespacedId> getWorldRegistry() {
        return worldRegistry;
    }

    /** Gets the block registry */
    public static Registry<Block<?,?>, NamespacedId> getBlockRegistry() {
        return blockRegistry;
    }

    /** Get the real player registry */
    public static Registry<RealPlayer, UUID> getRealPlayerRegistry() {
        return realPlayerRegistry;
    }

    /** Get the dummy player registry */
    public static Registry<DummyPlayer, String> getDummyPlayerRegistry() {
        return dummyPlayerRegistry;
    }

    /**
     * Run "/bv" command.
     * @param args the arguments of the command.
     */
    public static void onBvCommand(CommandSender sender, String[] args) {
        Writer outWriter = BuildVoxWriter.newOutInstance(sender);
        Writer errWriter = BuildVoxWriter.newErrInstance(sender);
        PrintWriter out = new PrintWriter(outWriter, true);
        PrintWriter err = new PrintWriter(errWriter, true);
        World execWorld = sender.getWorld();
        Vector3i execPos = sender.getBlockPos();
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
        return getTabCompletionList(spec, args);
    }

    /** Run "/bvd" command. */
    public static void onBvdCommand(CommandSender sender, String[] args) {
        Writer outWriter = BuildVoxWriter.newOutInstance(sender);
        Writer errWriter = BuildVoxWriter.newErrInstance(sender);
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
        return getTabCompletionList(spec, args);
    }

    private static List<String> getTabCompletionList(CommandLine.Model.CommandSpec spec, String[] args) {
        int argIndex = args.length - 1;
        List<CharSequence> candidates = new ArrayList<>();
        int positionInArg = 0;
        int cursor = 0;
        AutoComplete.complete(spec, args, argIndex, positionInArg, cursor, candidates);
        List<String> list = new ArrayList<>();
        String lastArg = args[argIndex];
        for (CharSequence s0 : candidates) {
            String s = s0.toString();
            boolean put = false;
            if(s.startsWith(lastArg)) {
                put = true;
            }else {
                String m = "minecraft:";
                if (s.startsWith(m) && !lastArg.startsWith(m)) {
                    if (s.startsWith(m + lastArg)) {
                        put = true;
                    }
                }
            }
            if(put){
                list.add(s);
            }
        }
        return list;
    }

    /**
     * Handles a left-clicking block event by pos marker.
     * @param player the player who invoked this event.
     * @param pos the position of the clicked block.
     */
    public static void onLeftClickBlockByPosMarker(Player player, World world, Vector3i pos) {
        Vector3i[] posData = new Vector3i[player.getPosArrayClone().length];
        posData[0] = pos;
        player.setPosArray(world, posData);
        player.getPlayerEntity().println(config.outColor() + FeedbackMessage.ofPosExit(0, pos.x(), pos.y(), pos.z()));
    }

    /**
     * Handles a right-clicking block event by pos marker.
     * @param player  a player who invoked this event.
     * @param pos the position of the clicked block.
     */
    public static void onRightClickBlockByPosMarker(Player player, World world, Vector3i pos) {
        World posOrSelectionWorld = player.getEditTargetWorld();
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
        player.setPosArray(world, posData);
        player.getPlayerEntity().println(config.outColor() + FeedbackMessage.ofPosExit(index, pos.x(), pos.y(), pos.z()));
    }

}