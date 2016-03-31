package com.peter.humble.cache.data;

/**
 * Created by ylkang on 3/29/16.
 */
public class IndexWrapper {

    private long timeStamp;

    private byte[] key;

    private long indexOffset;

    private long dataOffset;

    private int block;

    public IndexWrapper(){
    }

    public IndexWrapper(byte[] bytes) {
        this.key = bytes;
    }

    public long getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(long dataOffset) {
        this.dataOffset = dataOffset;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public long getIndexOffset() {
        return indexOffset;
    }

    public void setIndexOffset(long indexOffset) {
        this.indexOffset = indexOffset;
    }
}
