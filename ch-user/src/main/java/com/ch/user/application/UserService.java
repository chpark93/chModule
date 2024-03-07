package com.ch.user.application;

import com.ch.user.dto.request.LoginRequest;
import com.ch.user.dto.request.SignUpRequest;
import com.ch.user.dto.request.UserRequest;
import com.ch.user.dto.response.LoginResponse;
import com.ch.user.dto.response.SignUpResponse;
import com.ch.user.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {
    LoginResponse.TokenInfo login(LoginRequest.Login loginRequest);
    SignUpResponse signUp(SignUpRequest.SignUp signUp);
    LoginResponse.Logout logout(HttpServletRequest request, LoginRequest.Logout logout);
    LoginResponse.TokenInfo refreshToken(HttpServletRequest request, UserRequest.ReIssue refreshToken);
    UserResponse getProfileFromUserId(String userId);

}
