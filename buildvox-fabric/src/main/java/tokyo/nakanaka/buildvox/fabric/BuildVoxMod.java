package tokyo.nakanaka.buildvox.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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
import net.minecraft.util.registry.RegistryKey;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.PlayerEntity;
import tokyo.nakanaka.buildvox.core.commandSender.CommandSender;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.Player;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.world.World;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;

/**
 * The entry point of BuildVox Fabric
 */
public class BuildVoxMod implements ModInitializer {
	public static final Item POS_MARKER = new Item(new FabricItemSettings().group(ItemGroup.TOOLS));
	private Map<net.minecraft.world.World, NamespacedId> worldIdMap = new HashMap<>();
	private static final String SUBCOMMAND = "subcommand";

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("buildvox", "pos_marker"), POS_MARKER);
		ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
		ServerWorldEvents.LOAD.register(this::onWorldLoad);
		ServerWorldEvents.UNLOAD.register(this::onWorldUnLoad);
		ServerEntityEvents.ENTITY_LOAD.register(this::onEntityLoad);
		ServerEntityEvents.ENTITY_UNLOAD.register(this::onEntityUnload);
		CommandRegistrationCallback.EVENT.register(this::onCommandRegistration);
		AttackBlockCallback.EVENT.register(this::onAttackBlock);
		UseBlockCallback.EVENT.register(this::onBlockUse);
	}

	private void onServerStarting(MinecraftServer server) {
		FabricScheduler.initialize();
		BuildVoxSystem.environment = new BuildVoxSystem.Environment(new FabricBlockValidator(),
				new FabricBlockStateTransformer(), FabricScheduler.getInstance());
		for(Identifier blockId0 : Registry.BLOCK.getIds()) {
			BuildVoxSystem.BLOCK_REGISTRY.register(new NamespacedId(blockId0.getNamespace(), blockId0.getPath()));
		}
	}

	private void onWorldLoad(MinecraftServer server, ServerWorld world){
		Set<RegistryKey<net.minecraft.world.World>> worldRegistryKeys = server.getWorldRegistryKeys();
		for (var key : worldRegistryKeys) {
			if (world == server.getWorld(key)) {//have to find the registry key of worldId
				Identifier worldId0 = key.getValue();
				NamespacedId worldId = new NamespacedId(worldId0.getNamespace(), worldId0.getPath());
				worldIdMap.put(world, worldId);
				World world1 = new FabricWorld(world);
				BuildVoxSystem.WORLD_REGISTRY.register(world1);
				break;
			}
		}
	}

	private void onWorldUnLoad(MinecraftServer server, ServerWorld world) {
		Set<RegistryKey<net.minecraft.world.World>> worldRegistryKeys = server.getWorldRegistryKeys();
		for(var key : worldRegistryKeys) {
			if(world == server.getWorld(key)) {//have to find the registry key of worldId
				Identifier worldId0 = key.getValue();
				NamespacedId worldId = new NamespacedId(worldId0.getNamespace(), worldId0.getPath());
				worldIdMap.remove(world);
				BuildVoxSystem.WORLD_REGISTRY.unregister(worldId);
				break;
			}
		}
	}

	private void onEntityLoad(Entity entity, ServerWorld world) {
		if(!(entity instanceof ServerPlayerEntity player0)){
			return;
		}
		PlayerEntity playerEntity = new FabricPlayerEntity(player0);
		Player player = new Player(playerEntity);
		player.setParticleGuiVisible(true);
		BuildVoxSystem.PLAYER_REPOSITORY.register(player);
	}

	private void onEntityUnload(Entity entity, ServerWorld world) {
		if(!(entity instanceof ServerPlayerEntity player0)){
			return;
		}
		UUID playerId = player0.getUuid();
		BuildVoxSystem.PLAYER_REPOSITORY.unregister(playerId);
	}

	private void onCommandRegistration(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(
				CommandManager
						.literal("bv")
						.then(argument(SUBCOMMAND, StringArgumentType.greedyString())
								.suggests(this::onBvTabComplete)
								.executes(this::onBvCommand))
		);
		dispatcher.register(
				CommandManager
						.literal("bvd")
						.then(argument(SUBCOMMAND, StringArgumentType.greedyString())
								.suggests(this::onBvdTabComplete)
								.executes(this::onBvdCommand))
		);
	}

	/** convert ServerWorld to {@link World} */
	public static World convertServerWorldToBvWorld(ServerWorld original) {
		RegistryKey<net.minecraft.world.World> key = original.getRegistryKey();
		Identifier worldId0 = key.getValue();
		NamespacedId worldId = new NamespacedId(worldId0.getNamespace(), worldId0.getPath());
		return BuildVoxSystem.getWorldRegistry().get(worldId);
	}

	private static CommandSender getCommandSender(ServerCommandSource source) {
		try {
			ServerPlayerEntity spe = source.getPlayer();
			return BuildVoxSystem.getPlayerRepository().get(spe.getUuid());
		}catch (CommandSyntaxException e) {
			return new CommandSender() {
				@Override
				public void sendOutMessage(String msg) {
					source.sendFeedback(Text.of(msg), false);
				}

				@Override
				public void sendErrMessage(String msg) {
					source.sendFeedback(Text.of(msg), false);
				}

				@Override
				public World getWorld() {
					return convertServerWorldToBvWorld(source.getWorld());
				}

				@Override
				public Vector3i getBlockPos() {
					Vec3d p = source.getPosition();
					return new Vector3i((int)Math.floor(p.getX()), (int)Math.floor(p.getY()), (int)Math.floor(p.getZ()));
				}
			};
		}
	}

	private int onBvCommand(CommandContext<ServerCommandSource> context) {
		String subcommand = StringArgumentType.getString(context, SUBCOMMAND);
		String[] args = subcommand.split(" ", - 1);
		CommandSender sender = getCommandSender(context.getSource());
		BuildVoxSystem.onBvCommand(sender, args);
		return 1;
	}

	private int onBvdCommand(CommandContext<ServerCommandSource> context) {
		String subcommand = StringArgumentType.getString(context, SUBCOMMAND);
		String[] args = subcommand.split(" ", - 1);
		CommandSender sender = getCommandSender(context.getSource());
		BuildVoxSystem.onBvdCommand(sender, args);
		return 1;
	}

	private CompletableFuture<Suggestions> onBvTabComplete(CommandContext<ServerCommandSource> context,
														   SuggestionsBuilder builder) {
		TabCompleteListCreator listCreator = BuildVoxSystem::onBvTabComplete;
		return onTabComplete(context, builder, listCreator);
	}

	private CompletableFuture<Suggestions> onBvdTabComplete(CommandContext<ServerCommandSource> context,
														   SuggestionsBuilder builder) {
		TabCompleteListCreator listCreator = BuildVoxSystem::onBvdTabComplete;
		return onTabComplete(context, builder, listCreator);
	}

	private interface TabCompleteListCreator {
		List<String> create(String[] args);
	}

	private CompletableFuture<Suggestions> onTabComplete(CommandContext<ServerCommandSource> context,
														 SuggestionsBuilder builder, TabCompleteListCreator listCreator) {
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

	private ActionResult onAttackBlock(net.minecraft.entity.player.PlayerEntity player0,
											  net.minecraft.world.World world0, Hand hand, BlockPos pos0, Direction direction) {
		//One event triggers this function with different combinations of arguments.
		//So filters the arguments by their super classes.
		//The following filter may have redundant parts. For example, when the player is a server one, the world
		//should be also a server one.
		if(!(player0 instanceof ServerPlayerEntity player1)) {
			return ActionResult.PASS;
		}
		if(!(world0 instanceof ServerWorld world1)) {
			return ActionResult.PASS;
		}
		if(hand != Hand.MAIN_HAND) {
			return ActionResult.PASS;
		}
		UUID playerId = player1.getUuid();
		Player player = BuildVoxSystem.getPlayerRepository().get(playerId);
		World world = convertServerWorldToBvWorld(world1);
		ItemStack is = player0.getMainHandStack();
		Vector3i pos = new Vector3i(pos0.getX(), pos0.getY(), pos0.getZ());
		if(is.getItem().equals(POS_MARKER)){
			BuildVoxSystem.onLeftClickBlockByPosMarker(player, world, pos);
			return ActionResult.SUCCESS;
		}else {
			return ActionResult.PASS;
		}
	}

	private ActionResult onBlockUse(net.minecraft.entity.player.PlayerEntity player0,
										   net.minecraft.world.World world0, Hand hand, BlockHitResult hitResult){
		//One event triggers this function with different combinations of arguments.
		//So filters the arguments by their super classes.
		//The following filter may have redundant parts. For example, when the player is a server one, the world
		//should be also a server one.
		if(!(player0 instanceof ServerPlayerEntity player1)) {
			return ActionResult.PASS;
		}
		if(!(world0 instanceof ServerWorld world1)) {
			return ActionResult.PASS;
		}
		if(hand != Hand.MAIN_HAND) {
			return ActionResult.PASS;
		}
		UUID playerId = player1.getUuid();
		Player player = BuildVoxSystem.getPlayerRepository().get(playerId);
		World world = convertServerWorldToBvWorld(world1);
		ItemStack is = player0.getMainHandStack();
		BlockPos pos0 = hitResult.getBlockPos();
		Vector3i pos = new Vector3i(pos0.getX(), pos0.getY(), pos0.getZ());
		if(is.getItem().equals(POS_MARKER)){
			BuildVoxSystem.onRightClickBlockByPosMarker(player, world, pos);
			return ActionResult.SUCCESS;
		}else {
			return ActionResult.PASS;
		}
	}

}
