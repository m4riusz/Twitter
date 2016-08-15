package com.twitter.util;

import com.twitter.config.DatabaseConfig;
import com.twitter.model.Avatar;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * Created by mariusz on 15.08.16.
 */
@Component
public class AvatarUtilImpl implements AvatarUtil {

    @Override
    public Avatar getDefaultAvatar() throws IOException {
        Avatar avatar = new Avatar();
        avatar.setFileName(DatabaseConfig.DEFAULT_AVATAR_FILE_NAME);
        avatar.setBytes(getDefaultAvatarImageFileBytes());
        return avatar;
    }

    @Override
    public Avatar resizeToStandardSize(Avatar avatar) throws IOException {
        BufferedImage image = convertAvatarBytesToBufferedImage(avatar);
        BufferedImage scaledImage = scaleToStandardSize(image);
        avatar.setBytes(convertToBytes(scaledImage, getExtension(avatar.getFileName())));
        return avatar;
    }

    private byte[] getDefaultAvatarImageFileBytes() throws IOException {
        return Files.readAllBytes(new File(getDefaultAvatarFileDestination()).toPath());
    }

    private String getDefaultAvatarFileDestination() {
        return Paths.get("").toAbsolutePath().toString() + File.separator + DatabaseConfig.DEFAULT_AVATAR_FILE_NAME;
    }

    private byte[] convertToBytes(BufferedImage scaleImage, String extension) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(scaleImage, extension, baos);
        baos.flush();
        return baos.toByteArray();
    }

    private BufferedImage scaleToStandardSize(BufferedImage image) {
        Image imgData = image.getScaledInstance(DatabaseConfig.AVATAR_WIDTH, DatabaseConfig.AVATAR_HEIGHT, Image.SCALE_SMOOTH);
        return convertImageToBufferedImage(imgData);
    }

    private BufferedImage convertImageToBufferedImage(Image imgData) {
        BufferedImage bufferedImage = new BufferedImage(imgData.getWidth(null), imgData.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(imgData, 0, 0, null);
        return bufferedImage;
    }

    private BufferedImage convertAvatarBytesToBufferedImage(Avatar avatar) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(avatar.getBytes()));
    }
}
