package com.twitter.model;

/**
 * Created by mariusz on 11.07.16.
 */
public class Avatar {
    private int id;
    private int fileName;
    private byte[] bytes;

    public Avatar(int fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileName() {
        return fileName;
    }

    public void setFileName(int fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
