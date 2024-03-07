package com.ch.user.security.jwt;

import com.ch.core.code.Errors;
import com.ch.core.exception.BusinessException;
import com.ch.core.exception.CommonAccessException;
import com.ch.user.dto.response.LoginResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JwtTokenProvider
 */
@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
    private static final String COMMON_EXCEPTION = "exception";

    private final Key key;

    public JwtTokenProvider(@Value("${token.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public LoginResponse.TokenInfo generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return LoginResponse.TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if ( claims.get(AUTHORITIES_KEY) == null ) {
            throw new CommonAccessException(Errors.INVALID_JWT_TOKEN);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(HttpServletRequest request, String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch ( io.jsonwebtoken.security.SignatureException | MalformedJwtException e ) {
            request.setAttribute(COMMON_EXCEPTION, Errors.INVALID_JWT_TOKEN.getMessage());
            throw new BusinessException(Errors.INVALID_JWT_TOKEN.getMessage(), Errors.INVALID_JWT_TOKEN);
        } catch ( ExpiredJwtException e ) {
            request.setAttribute(COMMON_EXCEPTION, Errors.EXPIRED_JWT_TOKEN.getMessage());
            throw new BusinessException(Errors.EXPIRED_JWT_TOKEN.getMessage(), Errors.EXPIRED_JWT_TOKEN);
        } catch ( UnsupportedJwtException e ) {
            request.setAttribute(COMMON_EXCEPTION, Errors.UNSUPPORTED_JWT_TOKEN.getMessage());
            throw new BusinessException(Errors.UNSUPPORTED_JWT_TOKEN.getMessage(), Errors.UNSUPPORTED_JWT_TOKEN);
        } catch ( IllegalArgumentException e ) {
            request.setAttribute(COMMON_EXCEPTION, Errors.EMPTY_JWT_TOKEN.getMessage());
            throw new BusinessException(Errors.EMPTY_JWT_TOKEN.getMessage(), Errors.EMPTY_JWT_TOKEN);
        } catch ( Exception e ) {
            request.setAttribute(COMMON_EXCEPTION, Errors.FORBIDDEN.getMessage());
            throw new BusinessException(Errors.FORBIDDEN.getMessage(), Errors.FORBIDDEN);
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if ( bearerToken != null && bearerToken.startsWith(BEARER_TYPE) ) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch ( ExpiredJwtException e ) {
            return e.getClaims();
        }
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
