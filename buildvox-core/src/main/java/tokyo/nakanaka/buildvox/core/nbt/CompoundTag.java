package tokyo.nakanaka.buildvox.core.nbt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class CompoundTag implements Tag<Map<String, Tag<?>>>{
    private Map<String, Tag<?>> map;

    public CompoundTag(Map<String, Tag<?>> map) {
        this.map = map;
    }

    @Override
    public Map<String, Tag<?>> getValue() {
        return this.map;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        for(Map.Entry<String, Tag<?>> e : this.map.entrySet()) {
            Tag<?> tag = e.getValue();
            byte i = new TypeFinder(tag).find();
            dos.writeByte(i);
            String name = e.getKey();
            dos.writeUTF(name);
            tag.write(dos);
        }
        dos.writeByte(TagID.END_ID);
    }
}