package tokyo.nakanaka.buildvox.bukkit.block.blockEntityData;

import org.bukkit.block.Sign;

public record SignData(String[] lines, boolean glowing) implements BlockEntityData {
    @Override
    public void merge(org.bukkit.block.BlockState blockState) {
        if (blockState instanceof Sign sign) {
            for (int index = 0; index < lines.length; ++index) {
                sign.setLine(index, lines[index]);
            }
            sign.setGlowingText(glowing);
        }
    }
}
