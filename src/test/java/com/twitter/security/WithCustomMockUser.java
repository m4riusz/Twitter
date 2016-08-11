package com.twitter.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by mariusz on 11.08.16.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithCustomMockUser {

    String username() default "user";

    String role() default "USER";

    String[] authorities() default {};

    String password() default "password";

    long id() default 0L;
}
