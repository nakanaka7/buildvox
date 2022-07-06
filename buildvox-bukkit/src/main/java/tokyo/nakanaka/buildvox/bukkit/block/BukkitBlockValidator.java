package tokyo.nakanaka.buildvox.bukkit.block;

import org.bukkit.Server;
import tokyo.nakanaka.buildvox.core.block.BlockValidator;
import tokyo.nakanaka.buildvox.core.block.VoxelBlock;

/* internal */
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
    public boolean validate(VoxelBlock block) {
        String blockStr = block.withoutEntity().toString();
        try{
            server.createBlockData(blockStr);
        }catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }

}
