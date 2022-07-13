package tokyo.nakanaka.buildvox.bukkit.block;

import org.bukkit.inventory.Inventory;
import tokyo.nakanaka.buildvox.core.block.Block;

import java.util.Objects;
import java.util.Set;

public class BukkitBlockEntity implements Block.Entity {
    private Object obj;

    @Deprecated
    public BukkitBlockEntity(BlockEntityContent obj) {
        this.obj = obj;
    }

    public BukkitBlockEntity(Iterable<BlockUtils.BlockEntityData> blockEntityDatum, Inventory inventory) {
        this.obj = new BukkitBlockEntity(blockEntityDatum, inventory);
    }

    public BlockEntityContent getObj() {
        return (BlockEntityContent) obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitBlockEntity entity = (BukkitBlockEntity) o;
        return obj.equals(entity.obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj);
    }

    public static record BlockEntityContent(Set<BlockUtils.BlockEntityData> blockEntityDataSet, Inventory inventory) {
    }
}
