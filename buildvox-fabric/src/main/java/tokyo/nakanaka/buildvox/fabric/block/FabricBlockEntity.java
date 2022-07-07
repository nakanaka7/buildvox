package tokyo.nakanaka.buildvox.fabric.block;

import net.minecraft.nbt.NbtCompound;
import tokyo.nakanaka.buildvox.core.block.Block;

import java.util.Objects;

/* internal */
public class FabricBlockEntity implements Block.Entity {
    private NbtCompound nbt;

    public FabricBlockEntity(NbtCompound nbt) {
        this.nbt = nbt;
    }

    public NbtCompound getNbt() {
        return nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FabricBlockEntity entity = (FabricBlockEntity) o;
        return nbt.equals(entity.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nbt);
    }

}
