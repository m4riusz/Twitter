package com.twitter.config;


import com.twitter.model.TokenInfo;
import com.twitter.route.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mariusz on 26.07.16.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class TokenAuthenticationFilter extends GenericFilterBean {

    private static final String HEADER_TOKEN = "Auth-Token";
    private static final String HEADER_USERNAME = "username";
    private static final String HEADER_PASSWORD = "password";
    private static final String REQUEST_ATTR_DO_NOT_CONTINUE = "MyAuthenticationFilter-doNotContinue";
    private static final String POST = "POST";

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        boolean authenticated = checkToken(httpRequest, httpResponse);

        if (canRequestProcessingContinue(httpRequest) && isLoginOrLogoutRequest(httpRequest)) {
            if (authenticated) {
                checkLogout(httpRequest);
            }
            checkLogin(httpRequest, httpResponse);
        }

        if (canRequestProcessingContinue(httpRequest)) {
            chain.doFilter(request, response);
        }
    }

    private boolean isLoginOrLogoutRequest(HttpServletRequest httpRequest) {
        return httpRequest.getMethod().equals(POST) && (httpRequest.getRequestURI().equals(Route.LOGIN_URL) || httpRequest.getRequestURI().equals(Route.LOGOUT_URL));
    }

    private void checkLogin(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String username = httpRequest.getHeader(HEADER_USERNAME);
        String password = httpRequest.getHeader(HEADER_PASSWORD);

        if (username != null && password != null) {
            checkUsernameAndPassword(username, password, httpResponse);
            doNotContinueWithRequestProcessing(httpRequest);
        }
    }

    private void checkUsernameAndPassword(String username, String password, HttpServletResponse httpResponse) throws IOException {
        TokenInfo tokenInfo = authenticationService.authenticate(username, password);
        if (tokenInfo != null) {
            httpResponse.setHeader(HEADER_TOKEN, tokenInfo.getToken());
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean checkToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        String token = httpRequest.getHeader(HEADER_TOKEN);
        if (token == null) {
            return false;
        }

        if (authenticationService.checkToken(token)) {
            return true;
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            doNotContinueWithRequestProcessing(httpRequest);
        }
        return false;
    }

    private void checkLogout(HttpServletRequest httpRequest) {
        if (currentLink(httpRequest).equals(Route.LOGOUT_URL)) {
            String token = httpRequest.getHeader(HEADER_TOKEN);
            authenticationService.logout(token);
            doNotContinueWithRequestProcessing(httpRequest);
        }
    }

    private String currentLink(HttpServletRequest httpRequest) {
        if (httpRequest.getPathInfo() == null) {
            return httpRequest.getServletPath();
        }
        return httpRequest.getServletPath() + httpRequest.getPathInfo();
    }

    private void doNotContinueWithRequestProcessing(HttpServletRequest httpRequest) {
        httpRequest.setAttribute(REQUEST_ATTR_DO_NOT_CONTINUE, "");
    }

    private boolean canRequestProcessingContinue(HttpServletRequest httpRequest) {
        return httpRequest.getAttribute(REQUEST_ATTR_DO_NOT_CONTINUE) == null;
    }
}