package com.twitter.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */

@Entity
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private String fileName;
    @Lob
    private byte[] bytes;

    public Avatar() {

    }

    public Avatar(String fileName, byte[] bytes) {
        this();
        this.fileName = fileName;
        this.bytes = bytes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
