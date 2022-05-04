package tokyo.nakanaka.buildvox.core.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class ByteTag implements Tag<Byte>{
    private byte value;

    public ByteTag(int value) {
        this.value = (byte) value;
    }

    @Override
    public Byte getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeByte(this.value);
    }

}
