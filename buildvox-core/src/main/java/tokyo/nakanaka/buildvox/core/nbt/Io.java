package tokyo.nakanaka.buildvox.core.nbt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Io {
    private Io(){
    }

    public static NamedCompoundTag read(Path path) throws IOException {
        InputStream is = Files.newInputStream(path);
        GZIPInputStream gzis = new GZIPInputStream(is);
        DataInputStream dis = new DataInputStream(gzis);
        byte rootTagID = dis.readByte();
        if(rootTagID != TagID.COMPOUND_ID) {
            throw new IllegalArgumentException();
        }
        String rootName = dis.readUTF();
        CompoundTag tag = (CompoundTag)new TagReader(rootTagID).read(dis);
        return new NamedCompoundTag(rootName, tag.getValue());
    }

    public static void write(Path path, NamedCompoundTag root) throws IOException {
        OutputStream os = Files.newOutputStream(path);
        GZIPOutputStream gzos = new GZIPOutputStream(os);
        DataOutputStream dos = new DataOutputStream(gzos);
        dos.writeByte(TagID.COMPOUND_ID);
        dos.writeUTF(root.getName());
        root.write(dos);
    }

}