package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tokyo.nakanaka.buildvox.core.MessageReceiver;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.PlayerEntity;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.system.PlayerRepository;
import tokyo.nakanaka.buildvox.core.world.World;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * The entry point of BuildVox Bukkit
 */
public class BuildVoxPlugin extends JavaPlugin implements Listener {
    private static Logger LOGGER = LoggerFactory.getLogger(BuildVoxPlugin.class);
    private Map<org.bukkit.World, NamespacedId> worldIdMap = new HashMap<>();
    public static String POS_MARKER_LOCALIZED_NAME = "BuildVoxBukkit";

    @Override
    public void onLoad(){
    }

    @Override
    public void onEnable(){
        Server server = this.getServer();
        BuildVoxSystem.environment = new BuildVoxSystem.Environment(new BukkitBlockValidator(server),
                new BukkitBlockStateTransformer(server), new BukkitScheduler(this));
        for(var material : Material.values()){
            if(material.isBlock()) {
                NamespacedKey key = material.getKey();
                BuildVoxSystem.BLOCK_REGISTRY.register(
                        new NamespacedId(key.getNamespace().toLowerCase(), key.getKey().toLowerCase()));
            }
        }
        for(var world0 : server.getWorlds()) {
            addWorld(world0);
        }
        server.getPluginManager().registerEvents(this, this);
    }

    private record CommandSource(UUID playerId, PlayerEntity playerEntity, NamespacedId worldId, World world, int x, int y, int z) {
    }

    private CommandSource createCommandSource(org.bukkit.command.CommandSender cmdSender) {
        UUID playerId = null;
        PlayerEntity playerEntity = null;
        org.bukkit.World world;
        int x;
        int y;
        int z;
        if(cmdSender instanceof Player player) {
            playerId = player.getUniqueId();
            playerEntity = new BukkitPlayerEntity(player);
            world = player.getWorld();
            Location loc = player.getLocation();
            x = loc.getBlockX();
            y = loc.getBlockY();
            z = loc.getBlockZ();
        }else if(cmdSender instanceof BlockCommandSender blockCmdSender) {
            org.bukkit.block.Block block = blockCmdSender.getBlock();
            world = block.getWorld();
            x = block.getX();
            y = block.getY();
            z = block.getZ();
        }else if(cmdSender instanceof ConsoleCommandSender consoleCmdSender){
            Server server = consoleCmdSender.getServer();
            var world0 = server.getWorld("world");
            if(world0 == null)throw new IllegalArgumentException();
            world = world0;
            x = 0;
            y = 0;
            z = 0;
        }else{
            throw new IllegalArgumentException();
        }
        NamespacedId worldId = worldIdMap.get(world);
        if(worldId == null) {
            throw new IllegalArgumentException();
        }
        return new CommandSource(playerId, playerEntity, worldId, convertBukkitWorldToBvWorld(world), x, y, z);
    }

    /** convert bukkit World to bv World*/
    private static World convertBukkitWorldToBvWorld(org.bukkit.World world) {
        NamespacedId worldId = new NamespacedId(world.getName());
        return BuildVoxSystem.getWorldRegistry().get(worldId);
    }

    @Override
    public boolean onCommand(@NotNull org.bukkit.command.CommandSender cmdSender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args){
        CommandSource cmdSource;
        try {
            cmdSource = createCommandSource(cmdSender);
        }catch (IllegalArgumentException e){
            return true;
        }
        UUID playerId = cmdSource.playerId();
        PlayerRepository repo = BuildVoxSystem.PLAYER_REPOSITORY;
        tokyo.nakanaka.buildvox.core.player.Player player = repo.get(playerId);
        if(player == null) {
            repo.create(playerId, cmdSource.playerEntity());
        }
        MessageReceiver msgReceiver = new BukkitMessageReceiver(cmdSender);
        Vector3i pos = new Vector3i(cmdSource.x(), cmdSource.y(), cmdSource.z());
        switch (label) {
            case "bv" ->
                BuildVoxSystem.onBvCommand(args,
                        cmdSource.world(), pos, msgReceiver, cmdSource.playerId());
            case  "bvd" ->
                BuildVoxSystem.onBvdCommand(args,
                        cmdSource.worldId(), cmdSource.x(), cmdSource.y(), cmdSource.z(), msgReceiver, cmdSource.playerId());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull org.bukkit.command.CommandSender cmdSender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args){
        CommandSource cmdSource;
        try {
            cmdSource = createCommandSource(cmdSender);
        }catch (IllegalArgumentException e){
            return new ArrayList<>();
        }
        UUID playerId = cmdSource.playerId();
        PlayerRepository repo = BuildVoxSystem.PLAYER_REPOSITORY;
        tokyo.nakanaka.buildvox.core.player.Player player = repo.get(playerId);
        if(player == null) {
            repo.create(playerId, cmdSource.playerEntity());
        }
        return switch (label) {
            case "bv" -> BuildVoxSystem.onBvTabComplete(args);
            case "bvd" -> BuildVoxSystem.onBvdTabComplete(args);
            default -> new ArrayList<>();
        };
    }
    
    private void addWorld(org.bukkit.World world0){
        NamespacedId worldId = NamespacedId.valueOf(world0.getName());
        worldIdMap.put(world0, worldId);
        BuildVoxSystem.WORLD_REGISTRY.register(worldId, new BukkitWorld(getServer(), world0));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent evt) {
        org.bukkit.World world0 = evt.getWorld();
        addWorld(world0);
    }

    @EventHandler
    public void onWorldUnLoad(WorldUnloadEvent evt) {
        org.bukkit.World world0 = evt.getWorld();
        NamespacedId worldId = worldIdMap.get(world0);
        BuildVoxSystem.WORLD_REGISTRY.unregister(worldId);
        worldIdMap.remove(world0);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        PlayerRepository repo = BuildVoxSystem.PLAYER_REPOSITORY;
        repo.delete(evt.getPlayer().getUniqueId());
    }

    private enum ToolType {
        POS;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt){
        //check the action
        Action action = evt.getAction();
        if(action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        ToolType toolType;
        ItemStack itemStack = evt.getItem();
        if(itemStack == null){
            return;
        }
        Material type = itemStack.getType();
        //check the item is a tool
        if(type == Material.STICK) {//check the item is stick
            if (!itemStack.hasItemMeta()) {//check the item localized name
                return;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (!itemMeta.hasLocalizedName()) {
                return;
            }
            if (!itemMeta.getLocalizedName().equals(BuildVoxPlugin.POS_MARKER_LOCALIZED_NAME)) {
                return;
            }
            toolType = ToolType.POS;
        }else{
            return;
        }
        //checked all for the item being a tool, so cancel the event
        evt.setCancelled(true);
        Player player = evt.getPlayer();
        UUID playerId = player.getUniqueId();
        PlayerEntity playerEntity = new BukkitPlayerEntity(player);
        PlayerRepository repo = BuildVoxSystem.PLAYER_REPOSITORY;
        tokyo.nakanaka.buildvox.core.player.Player bvPlayer = repo.get(playerId);
        if(bvPlayer == null) {
            repo.create(playerId, playerEntity);
        }
        MessageReceiver commandOut = new BukkitMessageReceiver(player);
        Block block = evt.getClickedBlock();
        org.bukkit.World world0 = block.getWorld();
        NamespacedId worldId = worldIdMap.get(world0);
        if(worldId == null) {
            throw new IllegalArgumentException();
        }
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        switch (toolType) {
            case POS -> {
                switch (action) {
                    case LEFT_CLICK_BLOCK -> BuildVoxSystem.onLeftClickBlockByPosMarker(playerId, worldId, x, y, z, commandOut);
                    case RIGHT_CLICK_BLOCK -> BuildVoxSystem.onRightClickBlockByPosMarker(playerId, worldId, x, y, z, commandOut);
                }
            }
        }
    }

}
