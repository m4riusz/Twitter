package com.twitter.model;

import com.twitter.config.DatabaseConfig;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @Size(
            min = 1, max = DatabaseConfig.MAX_AVATAR_SIZE_BYTES,
            message = "Avatar size should be smaller than {max}! bytes!"
    )
    private byte[] bytes;

    public Avatar() {
        super();
        this.fileName = DatabaseConfig.DEFAULT_AVATAR_FILE_NAME;
        this.bytes = new byte[DatabaseConfig.MAX_AVATAR_SIZE_BYTES];
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
