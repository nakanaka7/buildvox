package tokyo.nakanaka.buildVoxCore.nbt;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TagReader {
    private byte type;

    public TagReader(byte type) {
        this.type = type;
    }

    public Tag<?> read(DataInputStream dis) throws IOException {
        if(type == TagID.BYTE_ID) {
            return new ByteTag(dis.readByte());
        }else if(type == TagID.SHORT_ID) {
            return new ShortTag(dis.readShort());
        }else if(type == TagID.INT_ID) {
            return new IntTag(dis.readInt());
        }else if(type == TagID.LONG_ID) {
            return new LongTag(dis.readLong());
        }else if(type == TagID.FLOAT_ID) {
            return new FloatTag(dis.readFloat());
        }else if(type == TagID.DOUBLE_ID) {
            return new DoubleTag(dis.readDouble());
        }else if(type == TagID.BYTE_ARRAY_ID) {
            byte[] b = new byte[dis.readInt()];
            dis.read(b);
            return new ByteArrayTag(b);
        }else if(type == TagID.STRING_ID) {
            return new StringTag(dis.readUTF());
        }else if(type == TagID.LIST_ID) {
            byte subType = dis.readByte();
            int length = dis.readInt();
            List<Tag<?>> value = new ArrayList<>();
            for(int i = 0; i < length; i++) {
                new TagReader(subType).read(dis);
            }
            return new ListTag(value);
        }else if(type == TagID.COMPOUND_ID) {
            Map<String, Tag<?>> map = new HashMap<>();
            while(true) {
                byte tagID = dis.readByte();
                if(tagID == TagID.END_ID) {
                    break;
                }
                String name = dis.readUTF();
                Tag<?> tag = new TagReader(tagID).read(dis);
                map.put(name, tag);
            }
            return new CompoundTag(map);
        }else if(type == TagID.INT_ARRAY_ID) {
            int length = dis.readInt();
            int[] array = new int[length];
            for(int i = 0; i < length; i++) {
                array[i] = dis.readInt();
            }
            return new IntArrayTag(array);
        }else if(type == TagID.LONG_ARRAY_ID) {
            int length = dis.readInt();
            long[] array = new long[length];
            for(int i = 0; i < length; i++) {
                array[i] = dis.readLong();
            }
            return new LongArrayTag(array);
        }else {
            throw new IllegalArgumentException();
        }
    }
}
