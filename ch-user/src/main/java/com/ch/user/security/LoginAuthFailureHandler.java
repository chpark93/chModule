package com.ch.user.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * LoginAuthFailureHandler
 */
@Slf4j
@Component
public class LoginAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /**
     * onAuthenticationFailure
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param exception AuthenticationException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        /** 로그 출력 */
        log.info("onAuthenticationFailure");
        /** 설정 : Content-Type -> application/json */
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        /** 설정 : Character-Encoding -> UTF8 */
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }
}