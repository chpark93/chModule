package com.ch.user.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * LoginAuthSuccessHandler
 */
@Slf4j
@Component
public class LoginAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /**
     * onAuthenticationSuccess
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param auth     Authentication
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        log.info("onAuthenticationSuccess");
    }
}