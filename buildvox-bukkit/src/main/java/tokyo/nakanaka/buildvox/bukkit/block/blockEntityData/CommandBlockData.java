package tokyo.nakanaka.buildvox.bukkit.block.blockEntityData;

import org.bukkit.block.CommandBlock;

public record CommandBlockData(String command, String name) implements BlockEntityData {
    public void merge(org.bukkit.block.BlockState blockState) {
        if (blockState instanceof CommandBlock commandBlock) {
            commandBlock.setCommand(command);
            commandBlock.setName(name);
        }
    }
}
