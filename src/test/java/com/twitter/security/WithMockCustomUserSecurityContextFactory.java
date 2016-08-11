package com.twitter.security;

import com.twitter.model.Password;
import com.twitter.model.Role;
import com.twitter.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.twitter.builders.UserBuilder.user;
import static com.twitter.util.Util.a;

/**
 * Created by mariusz on 11.08.16.
 */
@Component
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser withCustomMockUser) {
        List<GrantedAuthority> authorities = getAuthorities(withCustomMockUser);
        Role role = Role.valueOf(withCustomMockUser.role());
        User principal = a(user()
                .withId(withCustomMockUser.id())
                .withUsername(withCustomMockUser.username())
                .withRole(role)
                .withPassword(new Password(withCustomMockUser.password()))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), authorities);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        return securityContext;
    }

    private List<GrantedAuthority> getAuthorities(WithCustomMockUser withCustomMockUser) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (withCustomMockUser.role().startsWith("ROLE_")) {
            throw new IllegalArgumentException("roles cannot start with ROLE_ Got " + withCustomMockUser.role());
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_" + withCustomMockUser.role()));
        return authorities;
    }
}
