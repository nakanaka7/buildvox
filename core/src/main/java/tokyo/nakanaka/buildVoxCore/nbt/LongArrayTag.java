package tokyo.nakanaka.buildVoxCore.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class LongArrayTag implements Tag<long[]>{
    private long[] value;

    public LongArrayTag(long[] value) {
        this.value = value;
    }

    @Override
    public long[] getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt(this.value.length);
        for(int i = 0; i < this.value.length; i++) {
            dos.writeLong(this.value[i]);
        }
    }

}