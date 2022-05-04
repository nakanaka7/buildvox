package tokyo.nakanaka.buildVoxCore.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class FloatTag implements Tag<Float>{
    private float value;

    public FloatTag(float value) {
        this.value = value;
    }

    @Override
    public Float getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeFloat(this.value);
    }

}
