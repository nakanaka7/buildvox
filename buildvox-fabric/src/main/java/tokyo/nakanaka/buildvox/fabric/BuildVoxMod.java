package tokyo.nakanaka.buildvox.fabric;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.World;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.player.PlayerEntity;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.system.CommandSource;
import tokyo.nakanaka.buildvox.core.system.Messenger;
import tokyo.nakanaka.buildvox.fabric.block.BlockRegistering;
import tokyo.nakanaka.buildvox.fabric.block.FabricBlockValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static tokyo.nakanaka.buildvox.fabric.NamespacedIds.getId;

/**
 * The entry point of BuildVox Fabric
 */
public class BuildVoxMod implements ModInitializer {
	public static final Item POS_MARKER = new Item(new FabricItemSettings().group(ItemGroup.TOOLS));
	public static final Item BRUSH = new Item(new FabricItemSettings().group(ItemGroup.TOOLS));

	@Override
	public void onInitialize() {
		registerItems();
		BuildVoxSystem.setScheduler(FabricScheduler.getInstance());
		BuildVoxSystem.setBlockValidator(new FabricBlockValidator());
		BlockRegistering.registerBlocks();
		WorldManager.init();
		PlayerManager.init();
		new CommandInitializer().init();
		new ClickBlockEventInitializer().init();
	}

	private static void registerItems() {
		Registry.register(Registry.ITEM, new Identifier("buildvox", "pos_marker"), POS_MARKER);
		Registry.register(Registry.ITEM, new Identifier("buildvox", "brush"), BRUSH);
	}

	private static class WorldManager {
		/**
		 * Handles world load and unload.
		 */
		private static void init() {
			ServerWorldEvents.LOAD.register(WorldManager::onWorldLoad);
			ServerWorldEvents.UNLOAD.register(WorldManager::onWorldUnLoad);
		}

		private static void onWorldLoad(MinecraftServer server, ServerWorld world) {
			World fabricWorld = new FabricWorld(world);
			BuildVoxSystem.getWorldRegistry().register(fabricWorld);
		}

		private static void onWorldUnLoad(MinecraftServer server, ServerWorld world) {
			NamespacedId worldId = getId(world);
			BuildVoxSystem.getWorldRegistry().unregister(worldId);
		}
	}

	private static class PlayerManager {
		/** Handles player load and unload. */
		private static void init() {
			ServerEntityEvents.ENTITY_LOAD.register(PlayerManager::onEntityLoad);
			ServerEntityEvents.ENTITY_UNLOAD.register(PlayerManager::onEntityUnload);
		}

		private static void onEntityLoad(Entity entity, ServerWorld world) {
			if(!(entity instanceof ServerPlayerEntity player0)){
				return;
			}
			PlayerEntity playerEntity = new FabricPlayerEntity(player0);
			var player = new RealPlayer(playerEntity);
			player.setParticleGuiVisible(true);
			BuildVoxSystem.getRealPlayerRegistry().register(player);
		}

		private static void onEntityUnload(Entity entity, ServerWorld world) {
			if(!(entity instanceof ServerPlayerEntity player0)){
				return;
			}
			UUID playerId = player0.getUuid();
			Player player = BuildVoxSystem.getRealPlayerRegistry().unregister(playerId);
			player.setParticleGuiVisible(false);
		}
	}

	/** Initializer of commands event. */
	private static class CommandInitializer {
		private static final String SUBCOMMAND = "subcommand";
		private final Map<String, HandlerCompleter> cmdMap = new HashMap<>();

		/** Initialize */
		public void init() {
			registerCommand("bv", BuildVoxSystem::onBvCommand, BuildVoxSystem::onBvTabComplete);
			registerCommand("bvd", BuildVoxSystem::onBvdCommand, BuildVoxSystem::onBvdTabComplete);
			adaptEvents();
		}

		/** Registers command to this initializer. Calling adaptEvents() is needed to register events actually to mod. */
		private void registerCommand(String label, CommandHandler handler, TabCompleter completer) {
			cmdMap.put(label, new HandlerCompleter(handler, completer));
		}

		private interface CommandHandler {
			void handle(CommandSource source, String[] args);
		}

		private interface TabCompleter {
			List<String> create(String[] args);
		}

		private record HandlerCompleter (CommandHandler handler, TabCompleter completer) {
		}

		/** Registers events to mod. */
		private void adaptEvents() {
			CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
				for(var e : cmdMap.entrySet()) {
					dispatcher.register(
							CommandManager
									.literal(e.getKey())
									.then(argument(SUBCOMMAND, StringArgumentType.greedyString())
											.suggests((context, builder) -> onTabComplete(context, builder, e.getValue().completer()))
											.executes((context) -> onCommand(context, e.getValue().handler())))
					);
				}
			});
		}

		private int onCommand(CommandContext<ServerCommandSource> context, CommandHandler callback) {
			String subcommand = StringArgumentType.getString(context, SUBCOMMAND);
			String[] args = subcommand.split(" ", - 1);
			CommandSource source = getCommandSource(context.getSource());
			callback.handle(source, args);
			return 1;
		}

		private CommandSource getCommandSource(ServerCommandSource source) {
			try {
				ServerPlayerEntity player = source.getPlayer();
				UUID playerId = player.getUuid();
				return CommandSource.newInstance(playerId);
			} catch (CommandSyntaxException ex) {
				net.minecraft.world.World world = source.getWorld();
				NamespacedId worldId = getId(world);
				Vec3d p = source.getPosition();
				Vector3i pos = new Vector3i((int)Math.floor(p.getX()), (int)Math.floor(p.getY()), (int)Math.floor(p.getZ()));
				Messenger messenger = new Messenger() {
					@Override
					public void sendOutMessage(String msg) {
						source.sendFeedback(Text.of(msg), false);
					}
					@Override
					public void sendErrMessage(String msg) {
						source.sendFeedback(Text.of(msg), false);
					}
				};
				return new CommandSource(worldId, pos, messenger);
			}
		}

		private CompletableFuture<Suggestions> onTabComplete(CommandContext<ServerCommandSource> context,
															 SuggestionsBuilder builder, TabCompleter listCreator) {
			String subcommand;
			//when the subcommand is empty, it throws IllegalArgumentException
			try {
				subcommand = context.getArgument(SUBCOMMAND, String.class);
			}catch (IllegalArgumentException e){
				subcommand = "";
			}
			String[] args = subcommand.split(" ", - 1);
			//subcommand is one string. So modify the builder start to the last arg head point.
			int startOffset = 0;
			for(int i = 0; i < args.length - 1; ++i) {
				startOffset += args[i].length() + 1;
			}
			//new builder
			builder = new SuggestionsBuilder(builder.getInput(), builder.getStart() + startOffset);

			List<String> tabComplete = listCreator.create(args);

			for(String text : tabComplete){
				builder.suggest(text);
			}
			return builder.buildFuture();
		}

	}

	/** Initializer of click block events. */
	private static class ClickBlockEventInitializer {
		private final Map<Item, LeftRightClickBlockHandler> clickBlockMap = new HashMap<>();

		/** Initialize. */
		public void init() {
			registerEvent(POS_MARKER, BuildVoxSystem::onLeftClickBlockByPosMarker, BuildVoxSystem::onRightClickBlockByPosMarker);
			registerEvent(BRUSH, BuildVoxSystem::onLeftClickBlockByBrush, BuildVoxSystem::onRightClickBlockByBrush);
			adaptEventsForMod();
		}

		private void registerEvent(Item item, ClickBlockHandler left, ClickBlockHandler right) {
			clickBlockMap.put(item, new LeftRightClickBlockHandler() {
				@Override
				public void onLeftClickBlock(UUID playerId, Vector3i pos) {
					left.onClickBlock(playerId, pos);
				}
				@Override
				public void onRightClickBlock(UUID playerId, Vector3i pos) {
					right.onClickBlock(playerId, pos);
				}
			});
		}
		
		private interface ClickBlockHandler {
			void onClickBlock(UUID playerId, Vector3i pos);
		}

		private interface LeftRightClickBlockHandler {
			/** left click */
			void onLeftClickBlock(UUID playerId, Vector3i pos);
			/** right click*/
			void onRightClickBlock(UUID playerId, Vector3i pos);
		}
		
		/** Registers events for clicking block */
		private void adaptEventsForMod() {
			AttackBlockCallback.EVENT.register(this::onAttackBlock);
			UseBlockCallback.EVENT.register(this::onBlockUse);
		}

		private ActionResult onAttackBlock(net.minecraft.entity.player.PlayerEntity player0,
										   net.minecraft.world.World world0, Hand hand, BlockPos pos0, Direction direction) {
			//One event triggers this function with different combinations of arguments.
			//So filters the arguments by their super classes.
			//The following filter may have redundant parts. For example, when the player is a server one, the world
			//should be also a server one.
			if (!(player0 instanceof ServerPlayerEntity player1)) {
				return ActionResult.PASS;
			}
			if (!(world0 instanceof ServerWorld)) {
				return ActionResult.PASS;
			}
			if (hand != Hand.MAIN_HAND) {
				return ActionResult.PASS;
			}
			UUID playerId = player1.getUuid();
			ItemStack is = player0.getMainHandStack();
			Vector3i pos = new Vector3i(pos0.getX(), pos0.getY(), pos0.getZ());
			for (var e : clickBlockMap.entrySet()) {
				if (e.getKey().equals(is.getItem())) {
					e.getValue().onLeftClickBlock(playerId, pos);
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.PASS;
		}

		private ActionResult onBlockUse(net.minecraft.entity.player.PlayerEntity player0,
										net.minecraft.world.World world0, Hand hand, BlockHitResult hitResult) {
			//One event triggers this function with different combinations of arguments.
			//So filters the arguments by their super classes.
			//The following filter may have redundant parts. For example, when the player is a server one, the world
			//should be also a server one.
			if (!(player0 instanceof ServerPlayerEntity player1)) {
				return ActionResult.PASS;
			}
			if (!(world0 instanceof ServerWorld)) {
				return ActionResult.PASS;
			}
			if (hand != Hand.MAIN_HAND) {
				return ActionResult.PASS;
			}
			UUID playerId = player1.getUuid();
			ItemStack is = player0.getMainHandStack();
			BlockPos pos0 = hitResult.getBlockPos();
			Vector3i pos = new Vector3i(pos0.getX(), pos0.getY(), pos0.getZ());
			for (var e : clickBlockMap.entrySet()) {
				if (e.getKey().equals(is.getItem())) {
					e.getValue().onRightClickBlock(playerId, pos);
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.PASS;
		}
	}

}
