package tokyo.nakanaka.buildvox.core.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class ShortTag implements Tag<Short>{
    private short value;

    public ShortTag(short value) {
        this.value = value;
    }

    @Override
    public Short getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeShort(this.value);
    }

}