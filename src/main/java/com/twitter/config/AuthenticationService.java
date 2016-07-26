package com.twitter.config;


import com.twitter.model.TokenInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Created by mariusz on 26.07.16.
 */
@Component
public interface AuthenticationService {

    TokenInfo authenticate(String login, String password);

    boolean checkToken(String token);

    void logout(String token);

    UserDetails currentUser();
}