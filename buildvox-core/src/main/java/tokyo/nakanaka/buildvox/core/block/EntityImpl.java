package tokyo.nakanaka.buildvox.core.block;

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

}
