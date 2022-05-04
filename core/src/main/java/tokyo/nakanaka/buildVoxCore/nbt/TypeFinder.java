package tokyo.nakanaka.buildVoxCore.nbt;

class TypeFinder {
    private Tag<?> tag;

    public TypeFinder(Tag<?> tag) {
        this.tag = tag;
    }

    public byte find() {
        if(this.tag instanceof ByteTag) {
            return TagID.BYTE_ID;
        }else if(this.tag instanceof ShortTag) {
            return TagID.SHORT_ID;
        }else if(this.tag instanceof IntTag) {
            return TagID.INT_ID;
        }else if(this.tag instanceof LongTag) {
            return TagID.LONG_ID;
        }else if(this.tag instanceof FloatTag) {
            return TagID.FLOAT_ID;
        }else if(this.tag instanceof DoubleTag) {
            return TagID.DOUBLE_ID;
        }else if(this.tag instanceof ByteArrayTag) {
            return TagID.BYTE_ARRAY_ID;
        }else if(this.tag instanceof StringTag) {
            return TagID.STRING_ID;
        }else if(this.tag instanceof ListTag) {
            return TagID.LIST_ID;
        }else if(this.tag instanceof CompoundTag) {
            return TagID.COMPOUND_ID;
        }else if(this.tag instanceof IntArrayTag) {
            return TagID.INT_ARRAY_ID;
        }else if(this.tag instanceof LongArrayTag) {
            return TagID.LONG_ARRAY_ID;
        }else {
            throw new IllegalArgumentException();
        }
    }

}
