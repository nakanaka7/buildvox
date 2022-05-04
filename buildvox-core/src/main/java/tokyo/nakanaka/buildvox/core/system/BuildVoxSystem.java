package tokyo.nakanaka.buildvox.core.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tokyo.nakanaka.buildvox.core.BlockValidator;
import tokyo.nakanaka.buildvox.core.ColorCode;
import tokyo.nakanaka.buildvox.core.Scheduler;
import tokyo.nakanaka.buildvox.core.blockStateTransformer.BlockStateTransformer;
import tokyo.nakanaka.buildvox.core.system.clickBlockEventHandler.PosMarkerClickBlockEventHandler;
import tokyo.nakanaka.buildvox.core.system.commandHandler.BvCommandHandler;
import tokyo.nakanaka.buildvox.core.system.commandHandler.BvdCommandHandler;
import tokyo.nakanaka.buildvox.core.world.Block;

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

}
