package com.twitter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Lob;
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
    @JsonIgnore
    private String fileName;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] bytes;

    public Avatar() {
        super();
        this.fileName = "avatar.jpg";
        this.bytes = new byte[100]; //// TODO: 14.08.16 fix? 
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
