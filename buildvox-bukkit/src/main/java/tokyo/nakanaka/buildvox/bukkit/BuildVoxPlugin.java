package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Material;
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
import tokyo.nakanaka.buildvox.bukkit.block.BlockUtils;
import tokyo.nakanaka.buildvox.bukkit.block.BukkitBlockValidator;
import tokyo.nakanaka.buildvox.core.system.BuildVoxSystem;
import tokyo.nakanaka.buildvox.core.system.CommandSource;
import tokyo.nakanaka.buildvox.core.system.Messenger;
import tokyo.nakanaka.buildvox.core.NamespacedId;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;
import tokyo.nakanaka.buildvox.core.player.RealPlayer;
import tokyo.nakanaka.buildvox.core.World;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * The entry point of BuildVox Bukkit
 */
public class BuildVoxPlugin extends JavaPlugin implements Listener {
    private static Logger LOGGER = LoggerFactory.getLogger(BuildVoxPlugin.class);
    private static BuildVoxPlugin instance;
    private Map<org.bukkit.World, NamespacedId> worldIdMap = new HashMap<>();
    private UUID consoleId;
    public static String POS_MARKER_LOCALIZED_NAME = "BuildVoxBukkit";
    public static String BRUSH_LOCALIZED_NAME = "buildvox_brush";

    public static BuildVoxPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad(){
    }

    @Override
    public void onEnable(){
        instance = this;
        Server server = getServer();
        BuildVoxSystem.setScheduler(new BukkitScheduler(this));
        BuildVoxSystem.setBlockValidator(new BukkitBlockValidator(server));
        BlockUtils.registerBlocks();
        registerWorlds();
        server.getPluginManager().registerEvents(this, this);
    }

    private void registerWorlds() {
        for(var world0 : getServer().getWorlds()) {
            addWorld(world0);
        }
    }

    public Map<org.bukkit.World, NamespacedId> getWorldIdMap() {
        return worldIdMap;
    }

    private CommandSource getCommandSource(org.bukkit.command.CommandSender sender) {
        if(sender instanceof Player player) {
            UUID playerId = player.getUniqueId();
            var rp = BuildVoxSystem.getRealPlayerRegistry().get(playerId);
            if(rp == null) {
                var rp1 = createPlayer(player);
                BuildVoxSystem.getRealPlayerRegistry().register(rp1);
            }
            return CommandSource.newInstance(playerId);
        }else if(sender instanceof ConsoleCommandSender console) {
            if(consoleId == null) {
                ConsolePlayer consolePlayer = ConsolePlayer.newInstance(console);
                consoleId = consolePlayer.getId();
                BuildVoxSystem.getRealPlayerRegistry().register(consolePlayer);
            }
            return CommandSource.newInstance(consoleId);
        }else {
            Messenger messenger = new Messenger() {
                @Override
                public void sendOutMessage(String msg) {
                    sender.sendMessage(msg);
                }
                @Override
                public void sendErrMessage(String msg) {
                    sender.sendMessage(msg);
                }
            };
            if(sender instanceof BlockCommandSender blockSender) {
                var block = blockSender.getBlock();
                org.bukkit.World world = block.getWorld();
                NamespacedId worldId = NamespacedId.valueOf(world.getName());
                Vector3i pos = new Vector3i(block.getX(), block.getY(), block.getZ());
                return new CommandSource(worldId, pos, messenger);
            }else {
                return new CommandSource(NamespacedId.valueOf("world"), Vector3i.ZERO, messenger);
            }
        }
    }

    /** Gets the {@link RealPlayer} of the {@link Player}. */
    private static RealPlayer getRealPlayer(Player player) {
        UUID id = player.getUniqueId();
        var player1 = BuildVoxSystem.getRealPlayerRegistry().get(id);
        if(player1 != null)return player1;
        var player2 = createPlayer(player);
        BuildVoxSystem.getRealPlayerRegistry().register(player2);
        return player2;
    }

    private static RealPlayer createPlayer(Player player) {
        var p = new RealPlayer(new BukkitPlayerEntity(player));
        p.setParticleGuiVisible(true);
        return p;
    }

    /* Gets the {@link World} of the {@link org.bukkit.World} */
    public static World getWorld(org.bukkit.World world) {
        NamespacedId worldId = new NamespacedId(world.getName());
        return BuildVoxSystem.getWorldRegistry().get(worldId);
    }

    @Override
    public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        CommandSource source = getCommandSource(sender);
        switch (label) {
            case "bv" -> BuildVoxSystem.onBvCommand(source, args);
            case "bvd" -> BuildVoxSystem.onBvdCommand(source, args);
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
        BuildVoxSystem.getWorldRegistry().register(new BukkitWorld(world0));
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
        BuildVoxSystem.getWorldRegistry().unregister(worldId);
        worldIdMap.remove(world0);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        var regi = BuildVoxSystem.getRealPlayerRegistry();
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
        tokyo.nakanaka.buildvox.core.player.RealPlayer bvPlayer = getRealPlayer(evt.getPlayer());
        UUID playerId = bvPlayer.getId();
        Block block = evt.getClickedBlock();
        org.bukkit.World world0 = block.getWorld();
        Vector3i pos = new Vector3i(block.getX(), block.getY(), block.getZ());
        switch (toolType) {
            case POS -> {
                switch (action) {
                    case LEFT_CLICK_BLOCK -> BuildVoxSystem.onLeftClickBlockByPosMarker(playerId, pos);
                    case RIGHT_CLICK_BLOCK -> BuildVoxSystem.onRightClickBlockByPosMarker(playerId, pos);
                }
            }
        }
    }

}
