package tokyo.nakanaka.buildvox.bukkit;

import org.bukkit.Server;
import tokyo.nakanaka.buildvox.core.BlockValidator;
import tokyo.nakanaka.buildvox.core.world.BlockState;

/**
 * The class which implements {@link BlockValidator} for Bukkit Platform
 */
public class BukkitBlockValidator implements BlockValidator {
    private Server server;

    /**
     * Constructs the instance from a Server
     * @param server a server
     */
    public BukkitBlockValidator(Server server) {
        this.server = server;
    }

    @Override
    public boolean validate(BlockState block) {
        if(block instanceof BukkitBlockState)return true;
        String blockStr = block.toString();
        try{
            server.createBlockData(blockStr);
        }catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }

}
