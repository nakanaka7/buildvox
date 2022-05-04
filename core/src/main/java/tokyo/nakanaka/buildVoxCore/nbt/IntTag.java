package tokyo.nakanaka.buildVoxCore.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class IntTag implements Tag<Integer> {
    private int value;

    public IntTag(int value) {
        super();
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt(this.value);
    }

}
