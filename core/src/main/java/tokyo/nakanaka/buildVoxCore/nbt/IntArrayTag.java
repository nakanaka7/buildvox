package tokyo.nakanaka.buildVoxCore.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class IntArrayTag implements Tag<int[]>{
    private int[] value;

    public IntArrayTag(int[] value) {
        super();
        this.value = value;
    }

    @Override
    public int[] getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt(this.value.length);
        for(int i = 0; i < this.value.length; i++) {
            dos.writeInt(this.value[i]);
        }
    }

}