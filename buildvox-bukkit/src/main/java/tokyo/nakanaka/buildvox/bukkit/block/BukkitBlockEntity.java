package tokyo.nakanaka.buildvox.bukkit.block;

import org.bukkit.inventory.Inventory;
import tokyo.nakanaka.buildvox.core.block.Block;

import java.util.Objects;
import java.util.Set;

public class BukkitBlockEntity implements Block.Entity {
    private Object obj;

    public BukkitBlockEntity(Iterable<BlockUtils.BlockEntityData> blockEntityDatum, Inventory inventory) {
        this.obj = new BukkitBlockEntity(blockEntityDatum, inventory);
    }

    private BlockEntityContent getObj() {
        return (BlockEntityContent) obj;
    }

    public Iterable<BlockUtils.BlockEntityData> getBlockEntityDatum() {
        return getObj().blockEntityDataSet;
    }

    public Inventory getInventory() {
        return getObj().inventory;
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

    private static record BlockEntityContent(Set<BlockUtils.BlockEntityData> blockEntityDataSet, Inventory inventory) {
    }
}
