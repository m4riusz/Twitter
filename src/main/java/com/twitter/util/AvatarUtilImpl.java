package com.twitter.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by mariusz on 15.08.16.
 */
public class AvatarUtilImpl {

    public static final String DEFAULT_AVATAR_FILE_NAME = "avatar.jpg";
    public static final int MAX_AVATAR_SIZE_BYTES = 1024 * 1024 * 2;

    public static byte[] getDefaultImageBytes() {
        String currentPath = Paths.get("").toAbsolutePath().toString();
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(new File(currentPath + File.separator + DEFAULT_AVATAR_FILE_NAME).toPath());
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[100];
    }
}
