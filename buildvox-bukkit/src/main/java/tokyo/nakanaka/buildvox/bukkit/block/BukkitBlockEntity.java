package tokyo.nakanaka.buildvox.bukkit.block;

import tokyo.nakanaka.buildvox.core.block.Block;

import java.util.Objects;

public class BukkitBlockEntity implements Block.Entity {
    private Object obj;

    public BukkitBlockEntity(BlockUtils.BlockEntityContent obj) {
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
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

}
