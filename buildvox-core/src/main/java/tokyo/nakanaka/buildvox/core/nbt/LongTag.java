package tokyo.nakanaka.buildvox.core.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class LongTag implements Tag<Long>{
    private long value;

    public LongTag(long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeLong(this.value);
    }

}
