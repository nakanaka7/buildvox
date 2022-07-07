package tokyo.nakanaka.buildvox.fabric.block;

import tokyo.nakanaka.buildvox.core.block.Block;

import java.util.Objects;

/* internal */
public class FabricBlockEntity implements Block.Entity {
    private Object obj;

    public FabricBlockEntity(Object obj) {
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FabricBlockEntity entity = (FabricBlockEntity) o;
        return obj.equals(entity.obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj);
    }

}
