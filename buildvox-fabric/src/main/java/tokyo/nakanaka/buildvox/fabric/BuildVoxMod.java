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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.PlayerEntity;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
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
				BuildVoxSystem.WORLD_REGISTRY.register(worldId, world1);
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
		UUID playerId = player0.getUuid();
		PlayerEntity playerEntity = new FabricPlayerEntity(player0);
		BuildVoxSystem.PLAYER_REPOSITORY.create(playerId, playerEntity);
	}

	private void onEntityUnload(Entity entity, ServerWorld world) {
		if(!(entity instanceof ServerPlayerEntity player0)){
			return;
		}
		UUID playerId = player0.getUuid();
		BuildVoxSystem.PLAYER_REPOSITORY.delete(playerId);
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

	private record CommandSource(UUID playerId, PlayerEntity playerEntity,
								 NamespacedId worldId, World world, int x, int y, int z) {
	}

	private CommandSource createCommandSource(ServerCommandSource cmdSource0){
		UUID playerId = null;
		PlayerEntity playerEntity = null;
		ServerPlayerEntity player0 = null;
		try {
			player0 = cmdSource0.getPlayer();
		}catch (CommandSyntaxException e){
		}
		if(player0 != null){
			playerId = player0.getUuid();
			playerEntity = new FabricPlayerEntity(player0);
		}
		NamespacedId worldId = worldIdMap.get(cmdSource0.getWorld());
		World world = convertServerWorldToBvWorld(cmdSource0.getWorld());
		int x = (int)Math.floor(cmdSource0.getPosition().getX());
		int y = (int)Math.floor(cmdSource0.getPosition().getY());
		int z = (int)Math.floor(cmdSource0.getPosition().getZ());
		return new CommandSource(playerId, playerEntity, worldId, world, x, y, z);
	}

	/** convert ServerWorld to {@link World} */
	private static World convertServerWorldToBvWorld(ServerWorld original) {
		RegistryKey<net.minecraft.world.World> key = original.getRegistryKey();
		Identifier worldId0 = key.getValue();
		NamespacedId worldId = new NamespacedId(worldId0.getNamespace(), worldId0.getPath());
		return BuildVoxSystem.getWorldRegistry().get(worldId);
	}

	private interface CommandRunner {
		void run(ServerCommandSource cmdSource0, String[] args, MessageReceiver msgReceiver);
	}

	private int onCommand(CommandContext<ServerCommandSource> context, CommandRunner runner) {
		String subcommand = StringArgumentType.getString(context, SUBCOMMAND);
		String[] args = subcommand.split(" ", - 1);
		ServerCommandSource cmdSource0 = context.getSource();
		FabricMessageReceiver commandOut = new FabricMessageReceiver(cmdSource0);
		runner.run(cmdSource0, args, commandOut);
		return 1;
	}

	private int onBvCommand(CommandContext<ServerCommandSource> context) {
		CommandRunner runner = (cmdSource0, args, commandOut) -> {
			CommandSource cmdSource = createCommandSource(cmdSource0);
			Vector3i pos = new Vector3i(cmdSource.x(), cmdSource.y(), cmdSource.z());
			BuildVoxSystem.onBvCommand(args, cmdSource.world(), pos,
					commandOut, cmdSource.playerId());
		};
		return onCommand(context, runner);
	}

	private int onBvdCommand(CommandContext<ServerCommandSource> context) {
		CommandRunner runner = (cmdSource0, args, commandOut) -> {
			BuildVoxSystem.onBvdCommand(args, commandOut);
		};
		return onCommand(context, runner);
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
											  net.minecraft.world.World world0, Hand hand, BlockPos pos, Direction direction) {
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
		NamespacedId worldId = worldIdMap.get(world1);
		if(worldId == null) {
			throw new IllegalArgumentException();
		}
		MessageReceiver msgReceiver = new FabricMessageReceiver(player1.getCommandSource());
		ItemStack is = player0.getMainHandStack();
		if(is.getItem().equals(POS_MARKER)){
			BuildVoxSystem.onLeftClickBlockByPosMarker(playerId, worldId, pos.getX(), pos.getY(), pos.getZ(), msgReceiver);
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
		NamespacedId worldId = worldIdMap.get(world1);
		if(worldId == null) {
			throw new IllegalArgumentException();
		}
		MessageReceiver msgReceiver = new FabricMessageReceiver(player1.getCommandSource());
		ItemStack is = player0.getMainHandStack();
		if(is.getItem().equals(POS_MARKER)){
			BlockPos pos = hitResult.getBlockPos();
			BuildVoxSystem.onRightClickBlockByPosMarker(playerId, worldId, pos.getX(), pos.getY(), pos.getZ(), msgReceiver);
			return ActionResult.SUCCESS;
		}else {
			return ActionResult.PASS;
		}
	}

}
