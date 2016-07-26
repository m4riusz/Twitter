package com.twitter.config;

import com.twitter.model.TokenInfo;
import com.twitter.model.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Created by mariusz on 26.07.16.
 */
@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenManager tokenManager;

    @Override
    public TokenInfo authenticate(String login, String password) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);
        try {
            authentication = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.getPrincipal() != null) {
                UserDetails userContext = (UserDetails) authentication.getPrincipal();
                TokenInfo newToken = tokenManager.createNewToken(userContext);
                if (newToken == null) {
                    return null;
                }
                return newToken;
            }
        } catch (AuthenticationException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    @Override
    public boolean checkToken(String token) {

        UserDetails userDetails = tokenManager.getUserDetails(token);
        if (userDetails == null) {
            return false;
        }

        Authentication securityToken = new PreAuthenticatedAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(securityToken);

        return true;
    }

    @Override
    public void logout(String token) {
        tokenManager.removeToken(token);
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return (UserDetails) authentication.getPrincipal();
    }
}