package tokyo.nakanaka.buildVoxCore.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleTag implements Tag<Double>{
    private double value;

    public DoubleTag(double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeDouble(this.value);
    }

}
