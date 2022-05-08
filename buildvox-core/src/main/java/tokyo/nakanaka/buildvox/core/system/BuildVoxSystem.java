package tokyo.nakanaka.buildvox.core.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tokyo.nakanaka.buildvox.core.*;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockStateTransformer;
import tokyo.nakanaka.buildvox.core.system.clickBlockEventHandler.PosMarkerClickBlockEventHandler;
import tokyo.nakanaka.buildvox.core.system.commandHandler.BvCommandHandler;
import tokyo.nakanaka.buildvox.core.system.commandHandler.BvdCommandHandler;
import tokyo.nakanaka.buildvox.core.world.Block;

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
    /** Command handler of this system */
    @SuppressWarnings("unused")
    public static final CommandEventManager COMMAND_EVENT_MANAGER = new CommandEventManager();
    /** Event handler of this system */
    @SuppressWarnings("unused")
    public static final ClickBlockEventManager CLICK_BLOCK_EVENT_MANAGER = new ClickBlockEventManager();

    static {
        COMMAND_EVENT_MANAGER.register("bv", new BvCommandHandler());
        COMMAND_EVENT_MANAGER.register("bvd", new BvdCommandHandler());
        CLICK_BLOCK_EVENT_MANAGER.register(ToolType.POS_MARKER, new PosMarkerClickBlockEventHandler());
    }

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

    /** Run "/bv" command. */
    public static void onBvCommand(String[] args, NamespacedId worldId, int x, int y, int z, MessageReceiver messageReceiver, UUID playerId) {
        new BvCommandHandler().onCommand(args, worldId, x, y, z, messageReceiver, playerId);
    }

    /** Returns String list of "/bv" command's tab completion. */
    public static List<String> onBvTabComplete(String[] args) {
        return new BvCommandHandler().onTabComplete(args);
    }

    /** Run "/bvd" command. */
    public static void onBvdCommand(String[] args, NamespacedId worldId, int x, int y, int z,
                                    MessageReceiver messageReceiver, UUID playerId) {
        new BvdCommandHandler().onCommand(args, worldId, x, y, z, messageReceiver, playerId);
    }

    /** Returns String list of "/bvd" command's tab completion. */
    public static List<String> onBvdTabComplete(String[] args) {
        return new BvdCommandHandler().onTabComplete(args);
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
