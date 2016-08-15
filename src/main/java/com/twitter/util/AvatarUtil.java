package com.twitter.util;

import com.twitter.model.Avatar;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by mariusz on 15.08.16.
 */
@Component
public interface AvatarUtil {

    Avatar getDefaultAvatar() throws IOException;

    Avatar resizeToStandardSize(Avatar avatar) throws IOException;
}
