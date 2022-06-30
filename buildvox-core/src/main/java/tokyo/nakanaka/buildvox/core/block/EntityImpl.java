package tokyo.nakanaka.buildvox.core.block;

import java.util.Objects;

/* temporary */
@Deprecated
public class EntityImpl implements Block.Entity {
    private Object obj;

    public EntityImpl(Object obj) {
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityImpl entity = (EntityImpl) o;
        return obj.equals(entity.obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj);
    }

}
