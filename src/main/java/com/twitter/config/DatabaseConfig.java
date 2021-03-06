package com.twitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

/**
 * Created by mariusz on 13.07.16.
 */
@Configuration
public class DatabaseConfig {

    public static final int AVATAR_WIDTH = 64;
    public static final int AVATAR_HEIGHT = 64;
    public static final int MAX_AVATAR_SIZE_BYTES = 1024 * 1024;
    public static final String DEFAULT_AVATAR_FILE_NAME = "avatar.jpg";

    public static final String USERNAME_PATTERN = "^\\w+$";

    @Bean
    public HibernateJpaSessionFactoryBean sessionFactory() {
        return new HibernateJpaSessionFactoryBean();
    }
}
