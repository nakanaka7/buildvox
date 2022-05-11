package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import tokyo.nakanaka.buildvox.core.commandSender.AbstractCommandBlock;
import tokyo.nakanaka.buildvox.core.math.vector.Vector3i;

public class BukkitCommandBlock extends AbstractCommandBlock {
    private BlockCommandSender blockSender;

    public static BukkitCommandBlock newInstance(BlockCommandSender blockSender) {
        var block = blockSender.getBlock();
        World world = block.getWorld();
        tokyo.nakanaka.buildvox.core.world.World world1 = BuildVoxPlugin.convertBukkitWorldToBvWorld(world);
        Vector3i blockPos = new Vector3i(block.getX(), block.getY(), block.getZ());
        return new BukkitCommandBlock(world1, blockPos, blockSender);
    }

    private BukkitCommandBlock(tokyo.nakanaka.buildvox.core.world.World world, Vector3i blockPos, BlockCommandSender blockSender) {
        super(world, blockPos);
        this.blockSender = blockSender;
    }

    @Override
    public void sendMessage(String msg) {
        blockSender.sendMessage(msg);
    }

}
