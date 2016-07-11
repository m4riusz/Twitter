package com.twitter.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */

@Entity
public class Avatar {
    @Id
    @GeneratedValue
    private int id;
    @NotNull
    private int fileName;
    @Lob
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
