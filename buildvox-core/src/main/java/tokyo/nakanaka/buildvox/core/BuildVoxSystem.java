package tokyo.nakanaka.buildvox.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.AutoComplete;
import picocli.CommandLine;
import tokyo.nakanaka.buildvox.core.block.Block;
import tokyo.nakanaka.buildvox.core.block.BlockValidator;
import tokyo.nakanaka.buildvox.core.command.bvCommand.BvCommand;
import tokyo.nakanaka.buildvox.core.command.bvdCommand.BvdCommand;
import tokyo.nakanaka.buildvox.core.commandSender.CommandSender;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.DummyPlayer;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;
import tokyo.nakanaka.buildvox.core.registry.Registry;
import tokyo.nakanaka.buildvox.core.world.VoxelBlock;
import tokyo.nakanaka.buildvox.core.world.World;

import java.io.IOException;
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
    /** Use getter and setter of BlockValidator or Scheduler */
    @Deprecated
    public static Environment environment = Environment.DEFAULT;
    @Deprecated
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

    @Deprecated
    public static Config getConfig() {
        return config;
    }

    /** Use VoxelBlock.valueOf() */
    @Deprecated
    public static VoxelBlock parseBlock(String s) {
        return VoxelBlock.valueOf(s);
    }

    /** Sets the scheduler */
    public static void setScheduler(Scheduler scheduler) {
        var e = new Environment(environment.blockValidator, scheduler);
        environment = e;
    }

    /** Gets the scheduler */
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

    public String getOutColor() {
        return config.outColor;
    }

    public String getErrColor() {
        return config.errColor;
    }

    /** Gets the default background block id */
    public NamespacedId getDefaultBackgroundBlockId() {
        return new NamespacedId("air");
    }

    /** Gets the world registry */
    public static Registry<World, NamespacedId> getWorldRegistry() {
        return worldRegistry;
    }

    /** Gets the block registry */
    public static Registry<Block<?,?>, NamespacedId> getBlockRegistry() {
        return blockRegistry;
    }

    /** Gets the real player registry */
    public static Registry<RealPlayer, UUID> getRealPlayerRegistry() {
        return realPlayerRegistry;
    }

    /** Gets the dummy player registry */
    public static Registry<DummyPlayer, String> getDummyPlayerRegistry() {
        return dummyPlayerRegistry;
    }

    /**
     * @throws IllegalArgumentException if the player of the id is not registered.
     */
    public static void onBvCommand(String[] args, UUID playerId) {
        var player = realPlayerRegistry.get(playerId);
        if(player == null) throw new IllegalArgumentException();
        onBvCommand(player, args);
    }

    /**
     * @throws IllegalArgumentException if the world of the world id is not registered.
     */
    public static void onBvCommand(String[] args, NamespacedId worldId, Vector3i pos, Messenger messenger) {
        var world = worldRegistry.get(worldId);
        if(world == null) throw new IllegalArgumentException();
        var sender = new CommandSender() {
            public void sendOutMessage(String msg) {
                messenger.sendOutMessage(msg);
            }
            public void sendErrMessage(String msg) {
                messenger.sendErrMessage(msg);
            }
            public World getWorld() {
                return world;
            }
            public Vector3i getBlockPos() {
                return pos;
            }
        };
        onBvCommand(sender, args);
    }

    /**
     * Run "/bv" command.
     * @param args the arguments of the command.
     */
    @Deprecated
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

    /**
     * @throws IllegalArgumentException if the player of the id is not registered.
     */
    public static void onBvdCommand(String[] args, UUID playerId) {
        var player = realPlayerRegistry.get(playerId);
        if(player == null) throw new IllegalArgumentException();
        onBvdCommand(player, args);
    }

    public static void onBvdCommand(String[] args, Messenger messenger) {
        var sender = new CommandSender() {
            public void sendOutMessage(String msg) {
                messenger.sendOutMessage(msg);
            }
            public void sendErrMessage(String msg) {
                messenger.sendErrMessage(msg);
            }
            public World getWorld() {
                return null;
            }
            public Vector3i getBlockPos() {
                return null;
            }
        };
        onBvdCommand(sender, args);
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
     * @throws IllegalArgumentException if the player of the id is not registered.
     */
    public static void onLeftClickBlockByPosMarker(UUID playerId, Vector3i pos) {
        var player = realPlayerRegistry.get(playerId);
        if(player == null) throw new IllegalArgumentException();
        var world = player.getWorld();
        onLeftClickBlockByPosMarker(player, world, pos);
    }

    /**
     * Handles a left-clicking block event by pos marker.
     * @param player the player who invoked this event.
     * @param pos the position of the clicked block.
     */
    @Deprecated
    public static void onLeftClickBlockByPosMarker(Player player, World world, Vector3i pos) {
        Vector3i[] posArray = new Vector3i[player.getPosArrayClone().length];
        posArray[0] = pos;
        player.setPosArray(world, posArray);
        player.sendOutMessage(FeedbackMessage.ofPosExit(0, pos.x(), pos.y(), pos.z()));
    }

    /**
     * @throws IllegalArgumentException if the player of the id is not registered.
     */
    public static void onRightClickBlockByPosMarker(UUID playerId, Vector3i pos) {
        var player = realPlayerRegistry.get(playerId);
        if(player == null) throw new IllegalArgumentException();
        var world = player.getWorld();
        onRightClickBlockByPosMarker(player, world, pos);
    }

    /**
     * Handles a right-clicking block event by pos marker.
     * @param player  a player who invoked this event.
     * @param pos the position of the clicked block.
     */
    @Deprecated
    public static void onRightClickBlockByPosMarker(Player player, World world, Vector3i pos) {
        World editWorld = player.getEditTargetWorld();
        Vector3i[] posArray = player.getPosArrayClone();
        if (world != editWorld) {
            posArray = new Vector3i[player.getPosArrayClone().length];
        }
        int l = posArray.length;
        int index = l - 1;
        for (int i = 0; i < l; ++i) {
            if (posArray[i] == null) {
                index = i;
                break;
            }
        }
        posArray[index] = pos;
        player.setPosArray(world, posArray);
        player.sendOutMessage(FeedbackMessage.ofPosExit(index, pos.x(), pos.y(), pos.z()));
    }

    private static class BuildVoxWriter extends Writer {
        private String colorCode;
        private MessageReceiver messageReceiver;
        private String str = "";
        private boolean closed = false;

        private BuildVoxWriter(String colorCode, MessageReceiver messageReceiver){
            this.colorCode = colorCode;
            this.messageReceiver = messageReceiver;
        }

        public static BuildVoxWriter newOutInstance(CommandSender cmdSender) {
            return new BuildVoxWriter("", cmdSender::sendOutMessage);
        }

        public static BuildVoxWriter newErrInstance(CommandSender cmdSender) {
            return new BuildVoxWriter("", cmdSender::sendErrMessage);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if(this.closed){
                throw new IOException();
            }
            if(off < 0 || len < 0 || off + len < 0 || off + len > cbuf.length){
                throw new IndexOutOfBoundsException();
            }
            char[] shifted = new char[len];
            System.arraycopy(cbuf, off, shifted, 0, len);
            String add = new String(shifted);
            this.str += add;
        }

        @Override
        public void flush() throws IOException {
            if(this.closed){
                throw new IOException();
            }
            this.str = str.replaceAll("\\r", "");
            String[] msgs = this.str.split("\\n", -1);
            String last = msgs[msgs.length - 1];
            if(last.isEmpty()){
                String[] lastRemoved = new String[msgs.length - 1];
                System.arraycopy(msgs, 0, lastRemoved, 0, msgs.length - 1);
                msgs = lastRemoved;
                this.str = "";
            }else{
                msgs[msgs.length - 1] += "...";
                this.str = "...";
            }
            for(String msg : msgs) {
                this.messageReceiver.println(colorCode + msg);
            }
        }

        @Override
        public void close() throws IOException {
            this.flush();
            this.closed = true;
        }

        /**
         * An entity to receive messages.
         */
        @Deprecated
        private interface MessageReceiver {
            /**
             * Prints the message as a line.
             * @param msg the message.
             */
            void println(String msg);

        }

    }

}