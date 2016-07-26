package com.twitter.model;

/**
 * Created by mariusz on 26.07.16.
 */

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface TokenManager {

    TokenInfo createNewToken(UserDetails userDetails);

    void removeUserDetails(UserDetails userDetails);

    UserDetails removeToken(String token);

    UserDetails getUserDetails(String token);

    Map<String, UserDetails> getValidUsers();
}