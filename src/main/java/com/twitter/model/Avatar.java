package com.twitter.model;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by mariusz on 11.07.16.
 */

@Entity
public class Avatar extends AbstractEntity{
    @NotNull
    @Length(
            min = 1, max = 100,
            message = "Avatar file name length should be between {min} and {max}!"
    )
    private String fileName;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] bytes;

    public Avatar() {
        super();
    }

    public Avatar(String fileName, byte[] bytes) {
        this();
        this.fileName = fileName;
        this.bytes = bytes;
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
