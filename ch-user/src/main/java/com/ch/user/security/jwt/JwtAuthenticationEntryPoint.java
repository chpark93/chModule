package com.ch.user.security.jwt;

import com.ch.core.code.Errors;
import com.ch.core.utils.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JwtAuthenticationEntryPoint
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        String exception = (String) request.getAttribute("exception");

        if ( exception == null ) {
            setResponse(response, Errors.INTERNAL_SERVER_ERROR);
        }
        // Invalid JWT Token
        else if ( exception.equals(Errors.INVALID_JWT_TOKEN.getMessage()) ) {
            setResponse(response, Errors.INVALID_JWT_TOKEN);
        }
        // Expired JWT Token
        else if ( exception.equals(Errors.EXPIRED_JWT_TOKEN.getMessage()) ) {
            setResponse(response, Errors.EXPIRED_JWT_TOKEN);
        }
        // Unsupported JWT Token
        else if ( exception.equals(Errors.UNSUPPORTED_JWT_TOKEN.getMessage()) ) {
            setResponse(response, Errors.UNSUPPORTED_JWT_TOKEN);
        }
        // JWT claims string is empty
        else if ( exception.equals(Errors.EMPTY_JWT_TOKEN.getMessage()) ) {
            setResponse(response, Errors.EMPTY_JWT_TOKEN);
        }
        // Etc
        else {
            setResponse(response, Errors.FORBIDDEN);
        }

    }

    private void setResponse(HttpServletResponse response, Errors errorConstants) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("timestamp", LocalDateTime.now());
        responseMap.put("state", errorConstants.getState());
        responseMap.put("result", "fail");
        responseMap.put("message", errorConstants.getMessage());
        responseMap.put("data", Collections.emptyList());
        responseMap.put("error", Collections.emptyList());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        String result = gson.toJson(responseMap);

        response.getWriter().print(result);
    }
}
