package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.player.PlayerEntity;
import tokyo.nakanaka.buildvox.core.commandSender.PlainCommandSender;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.system.Registry;
import tokyo.nakanaka.buildvox.core.world.World;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * The entry point of BuildVox Bukkit
 */
public class BuildVoxPlugin extends JavaPlugin implements Listener {
    private static Logger LOGGER = LoggerFactory.getLogger(BuildVoxPlugin.class);
    private Map<org.bukkit.World, NamespacedId> worldIdMap = new HashMap<>();
    private static BukkitConsole console = new BukkitConsole(Bukkit.getConsoleSender());
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

    private static tokyo.nakanaka.buildvox.core.commandSender.CommandSender getBvCommandSender(CommandSender sender) {
        if(sender instanceof Player player) {
            UUID id = player.getUniqueId();
            var player1 = BuildVoxSystem.getPlayerRegistry().get(id);
            if(player1 != null)return player1;
            var player2 = new tokyo.nakanaka.buildvox.core.player.RealPlayer(new BukkitPlayerEntity(player));
            player2.setParticleGuiVisible(true);
            BuildVoxSystem.getPlayerRegistry().register(player2);
            return BuildVoxSystem.getPlayerRegistry().get(id);
        }else if(sender instanceof BlockCommandSender blockSender) {
            return BukkitCommandBlock.newInstance(blockSender);
        }else if(sender instanceof ConsoleCommandSender) {
            return console;
        }else {
            return new PlainCommandSender(BuildVoxSystem.getWorldRegistry().get(new NamespacedId("world")),
                    new Vector3i(0, 0, 0)) {
                @Override
                public void sendMessage(String msg) {
                    sender.sendMessage(msg);
                }
            };
        }
    }

    /** convert bukkit World to bv World*/
    public static World convertBukkitWorldToBvWorld(org.bukkit.World world) {
        NamespacedId worldId = new NamespacedId(world.getName());
        return BuildVoxSystem.getWorldRegistry().get(worldId);
    }

    @Override
    public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        tokyo.nakanaka.buildvox.core.commandSender.CommandSender sender1 = getBvCommandSender(sender);
        switch (label) {
            case "bv" -> BuildVoxSystem.onBvCommand(sender1, args);
            case "bvd" -> BuildVoxSystem.onBvdCommand(sender1, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull org.bukkit.command.CommandSender cmdSender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {
        return switch (label) {
            case "bv" -> BuildVoxSystem.onBvTabComplete(args);
            case "bvd" -> BuildVoxSystem.onBvdTabComplete(args);
            default -> new ArrayList<>();
        };
    }
    
    private void addWorld(org.bukkit.World world0){
        NamespacedId worldId = NamespacedId.valueOf(world0.getName());
        worldIdMap.put(world0, worldId);
        BuildVoxSystem.WORLD_REGISTRY.register(new BukkitWorld(getServer(), world0));
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
        var regi = BuildVoxSystem.getPlayerRegistry();
        var player = regi.unregister(evt.getPlayer().getUniqueId());
        player.setParticleGuiVisible(false);
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
        Registry<tokyo.nakanaka.buildvox.core.player.Player, UUID> regi = BuildVoxSystem.getPlayerRegistry();
        tokyo.nakanaka.buildvox.core.player.Player bvPlayer = regi.get(playerId);
        if(bvPlayer == null) {
            bvPlayer = new tokyo.nakanaka.buildvox.core.player.RealPlayer(playerEntity);
            regi.register(bvPlayer);
        }
        Block block = evt.getClickedBlock();
        org.bukkit.World world0 = block.getWorld();
        World world = convertBukkitWorldToBvWorld(world0);
        Vector3i pos = new Vector3i(block.getX(), block.getY(), block.getZ());
        switch (toolType) {
            case POS -> {
                switch (action) {
                    case LEFT_CLICK_BLOCK -> BuildVoxSystem.onLeftClickBlockByPosMarker(bvPlayer, world, pos);
                    case RIGHT_CLICK_BLOCK -> BuildVoxSystem.onRightClickBlockByPosMarker(bvPlayer, world, pos);
                }
            }
        }
    }

}
