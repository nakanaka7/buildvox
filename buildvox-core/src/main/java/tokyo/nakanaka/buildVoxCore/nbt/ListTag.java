package tokyo.nakanaka.buildVoxCore.nbt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ListTag implements Tag<List<? extends Tag<?>>>{
    private List<? extends Tag<?>> value;

    public ListTag(List<? extends Tag<?>> value) {
        this.value = value;
    }

    @Override
    public List<? extends Tag<?>> getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        if(this.value.size() == 0) {
            throw new IllegalArgumentException("list size must be larger than 0. Use ZeroListTag instead.");
        }
        Tag<?> tag = value.get(0);
        byte id = new TypeFinder(tag).find();
        dos.writeByte(id);
        int size = this.value.size();
        dos.writeInt(size);
        for(int i = 0; i < size; i++) {
            this.value.get(i).write(dos);
        }
    }

}