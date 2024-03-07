package com.ch.user.config;

import com.ch.user.security.jwt.JwtAccessDeniedHandler;
import com.ch.user.security.jwt.JwtAuthenticationEntryPoint;
import com.ch.user.security.jwt.JwtAuthenticationFilter;
import com.ch.user.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.util.function.Supplier;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // JwtAuthentication - 인증
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    // JwtAccessDeniedHandler - 인가
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    // JwtTokenProvider
    private final JwtTokenProvider jwtTokenProvider;
    // RedisTemplate
    private final RedisTemplate<String, Object> redisTemplate;

    public static final String ALLOWED_IP_ADDRESS = "127.0.0.1";
    public static final String SUBNET = "/32";
    public static final IpAddressMatcher ALLOWED_IP_ADDRESS_MATCHER = new IpAddressMatcher(ALLOWED_IP_ADDRESS + SUBNET);

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("============================== SECURITY ================================");

        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers("/actuator", "/actuator/*").permitAll()
                        .requestMatchers("/healthCheck").permitAll()
                        .requestMatchers("/h2-console", "/h2-console/**").permitAll()
                        .requestMatchers("/api/v3/*", "/health", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources/*",
                                "/swagger-ui.html", "/swagger-ui/*", "/api*", "/api-docs/**", "/swagger-ui/**",
                                "/swagger/*", "/swagger*/*", "/webjars/*").permitAll()
                        .requestMatchers("/**").access(
                                new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasIpAddress('125.191.160.43')")
                        )
                        .anyRequest().authenticated()
                )
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .cors(
                        AbstractHttpConfigurer::disable
                )
                .headers(
                        httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
                        )
                )
                .formLogin(
                        AbstractHttpConfigurer::disable
                )
                .exceptionHandling(
                        handling -> handling.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    private AuthorizationDecision hasIpAddress(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        return new AuthorizationDecision(ALLOWED_IP_ADDRESS_MATCHER.matches(object.getRequest()));
    }
}