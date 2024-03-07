package com.ch.user.application.impl;

import com.ch.core.code.Errors;
import com.ch.user.code.Role;
import com.ch.core.exception.BusinessException;
import com.ch.core.vo.Token;
import com.ch.user.application.UserService;
import com.ch.user.client.OrderServiceClient;
import com.ch.user.domain.User;
import com.ch.user.dto.request.LoginRequest;
import com.ch.user.dto.request.SignUpRequest;
import com.ch.user.dto.request.UserRequest;
import com.ch.user.dto.response.LoginResponse;
import com.ch.user.dto.response.OrderResponse;
import com.ch.user.dto.response.SignUpResponse;
import com.ch.user.dto.response.UserResponse;
import com.ch.user.repository.UserRepository;
import com.ch.user.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory; // CircuitBreaker

    /**
     * Login 
     * @param loginRequest 로그인 요청 데이터
     * @return LoginResponse.TokenInfo - 토큰 정보 
     */
    @Override
    public LoginResponse.TokenInfo login(LoginRequest.Login loginRequest) {
        Optional<User> getUserId = userRepository.findByUserId(loginRequest.getUserId());
        if ( getUserId.orElse(null) == null ) {
            throw new BusinessException(Errors.NONE_USER_INFO.getMessage(), Errors.NONE_USER_INFO);
        }

        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();
        // 실제 검증
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 인증 정보를 기반으로 JWT 토큰 생성
        LoginResponse.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        // RefreshToken Redis 저장
        Token token = Token.builder()
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .build();

        redisTemplate.opsForValue()
                .set(getUserId.get().getUserId(), token, tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return tokenInfo;
    }

    /**
     * Sign Up - Create User
     * @param signUp SignUpRequest.SignUp
     * @return SignUpResponse 회원 가입 정보
     */
    @Override
    public SignUpResponse signUp(SignUpRequest.SignUp signUp) {

        if ( userRepository.existsByUserId(signUp.getUserId()) ) {
            throw new BusinessException(Errors.DUPLICATED_USER_ID.getMessage(), Errors.DUPLICATED_USER_ID);
        }

        User user = User.builder()
                .userId(signUp.getUserId())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .name(signUp.getName())
                .roles(Collections.singletonList(Role.ROLE_CLIENT.name()))
                .build();

        userRepository.save(user);

        return SignUpResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public LoginResponse.TokenInfo refreshToken(HttpServletRequest request, UserRequest.ReIssue reissue) {

        // 1. Refresh Token 검증
        if ( !jwtTokenProvider.validateToken(request, reissue.getRefreshToken()) ) {
            throw new BusinessException(Errors.NONE_REFRESH_TOKEN.getMessage(), Errors.NONE_REFRESH_TOKEN);
        }

        // 2. Access Token 에서 UserId 취득.
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        // 3. Redis 에서 UserId -> 저장된 Refresh Token 값 취득.
        String refreshToken = (String) redisTemplate.opsForValue().get(authentication.getName());

        // 로그아웃되어 Redis에 RefreshToken이 존재하지 않는 경우 처리
        if ( ObjectUtils.isEmpty(refreshToken) ) {
            throw new BusinessException(Errors.BAD_REQUEST.getMessage(), Errors.BAD_REQUEST);
        }
        if ( !refreshToken.equals(reissue.getRefreshToken()) ) {
            throw new BusinessException(Errors.NOT_MATCH_REFRESH_TOKEN.getMessage(), Errors.NOT_MATCH_REFRESH_TOKEN);
        }

        // 4. 새로운 토큰 생성
        LoginResponse.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set(authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return tokenInfo;
    }

    @Override
    public LoginResponse.Logout logout(HttpServletRequest request, LoginRequest.Logout logout) {
        // 1. Access Token 검증
        if ( !jwtTokenProvider.validateToken(request, logout.getAccessToken()) ) {
            throw new BusinessException(Errors.BAD_REQUEST.getMessage(), Errors.BAD_REQUEST);
        }

        // 2. Access Token -> UserId 취득
        Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());

        // 3. Redis 에서 해당 UserId -> Refresh Token 여부 확인 -> 존재시 삭제
        if ( redisTemplate.opsForValue().get(authentication.getName()) != null ) {
            // Refresh Token 삭제
            redisTemplate.delete(authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 체크 후 BlackList에 저장
        Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
        redisTemplate.opsForValue()
                .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

        return LoginResponse.Logout.builder().userId(authentication.getName()).build();
    }

    @Override
    public UserResponse getProfileFromUserId(String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("No Authentication Information."));

        UserResponse userResponse = new ModelMapper().map(user, UserResponse.class);

        // Get Orders Info - CircuitBreakerFactory
        log.info("Before Call Orders Microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        List<OrderResponse> orderList = circuitBreaker.run(() -> orderServiceClient.getOrder(userId),
                throwable -> new ArrayList<>());
        log.info("After Call Orders Microservice");

        userResponse.setOrders(orderList);

        return userResponse;
    }

}
