package com.peter.humble.cache.data;

/**
 * Created by ylkang on 3/28/16.
 */
public class ValueWrapper {

    private byte[] bytes;

    private String fileName;

    private int offset;

    public ValueWrapper(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
