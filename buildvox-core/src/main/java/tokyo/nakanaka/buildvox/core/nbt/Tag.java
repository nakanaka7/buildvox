package tokyo.nakanaka.buildvox.core.nbt;

import java.io.DataOutputStream;
import java.io.IOException;

/*
 * Represents unnamed tag
 */
public interface Tag<V> {
    V getValue();
    /**
     * Write payload
     */
    void write(DataOutputStream dos) throws IOException;
}
