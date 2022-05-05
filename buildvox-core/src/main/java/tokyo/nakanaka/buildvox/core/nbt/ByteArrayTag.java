package tokyo.nakanaka.buildvox.core.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class ByteArrayTag implements Tag<byte[]>{
    private byte[] value;

    public ByteArrayTag(byte[] value) {
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt(this.value.length);
        dos.write(this.value);
    }
}
