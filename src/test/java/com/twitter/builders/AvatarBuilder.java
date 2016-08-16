package com.twitter.builders;

import com.twitter.config.DatabaseConfig;
import com.twitter.model.Avatar;
import com.twitter.util.Builder;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mariusz on 16.08.16.
 */
public final class AvatarBuilder implements Builder<Avatar> {
    private Date createDate = Calendar.getInstance().getTime();
    private String fileName = DatabaseConfig.DEFAULT_AVATAR_FILE_NAME;
    private byte[] bytes = new byte[DatabaseConfig.MAX_AVATAR_SIZE_BYTES];
    private long id;

    public static AvatarBuilder avatar() {
        return new AvatarBuilder();
    }

    public AvatarBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public AvatarBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public AvatarBuilder withBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public AvatarBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public Avatar build() {
        Avatar avatar = new Avatar();
        avatar.setCreateDate(createDate);
        avatar.setFileName(fileName);
        avatar.setBytes(bytes);
        avatar.setId(id);
        return avatar;
    }
}
