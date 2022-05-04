package tokyo.nakanaka.buildvox.core.nbt;

import java.util.Map;

public class NamedCompoundTag extends CompoundTag{
    private String name;

    public NamedCompoundTag(String name, Map<String, Tag<?>> map) {
        super(map);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
