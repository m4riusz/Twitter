package com.twitter.model;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by mariusz on 26.07.16.
 */

@Component
public class TokenManagerSingle implements TokenManager {

    private Map<String, UserDetails> validUsers = new HashMap<>();

    private Map<UserDetails, TokenInfo> tokens = new HashMap<>();

    @Override
    public TokenInfo createNewToken(UserDetails userDetails) {
        String token;
        do {
            token = generateToken();
        } while (validUsers.containsKey(token));

        TokenInfo tokenInfo = new TokenInfo(token, userDetails);
        removeUserDetails(userDetails);
        UserDetails previous = validUsers.put(token, userDetails);
        if (previous != null) {
            return null;
        }
        tokens.put(userDetails, tokenInfo);

        return tokenInfo;
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        new SecureRandom().nextBytes(tokenBytes);
        return new String(Base64.encode(tokenBytes), StandardCharsets.UTF_8);
    }

    @Override
    public void removeUserDetails(UserDetails userDetails) {
        TokenInfo token = tokens.remove(userDetails);
        if (token != null) {
            validUsers.remove(token.getToken());
        }
    }

    @Override
    public UserDetails removeToken(String token) {
        UserDetails userDetails = validUsers.remove(token);
        if (userDetails != null) {
            tokens.remove(userDetails);
        }
        return userDetails;
    }

    @Override
    public UserDetails getUserDetails(String token) {
        return validUsers.get(token);
    }

    @Override
    public Map<String, UserDetails> getValidUsers() {
        return Collections.unmodifiableMap(validUsers);
    }
}
