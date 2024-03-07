package com.ch.gateway.filter;

import com.ch.gateway.code.Errors;
import com.ch.gateway.common.response.GatewayErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Objects;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final Key key;

    public AuthorizationHeaderFilter(@Value("${token.secret}") String secretKey) {
        super(Config.class);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public static class Config {
        // Put configuration properties here
    }

    @Override
    public GatewayFilter apply(AuthorizationHeaderFilter.Config config) {

        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Authorization Header Pre Filter : Request Uri -> {}", request.getId());

            if ( !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION) ) {
                return onError(exchange, GatewayErrorResponse.of(Errors.EMPTY_JWT_TOKEN));
            }

            String authorizationHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
            String token = authorizationHeader.replace("Bearer", "");

            try {
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token);

            } catch ( io.jsonwebtoken.security.SignatureException | MalformedJwtException e ) {
                return onError(exchange, GatewayErrorResponse.of(Errors.INVALID_JWT_TOKEN));
            } catch ( ExpiredJwtException e ) {
                return onError(exchange, GatewayErrorResponse.of(Errors.EXPIRED_JWT_TOKEN));
            } catch ( UnsupportedJwtException e ) {
                return onError(exchange, GatewayErrorResponse.of(Errors.UNSUPPORTED_JWT_TOKEN));
            } catch ( IllegalArgumentException e ) {
                return onError(exchange, GatewayErrorResponse.of(Errors.EMPTY_JWT_TOKEN));
            } catch ( Exception e ) {
                return onError(exchange, GatewayErrorResponse.of(Errors.INTERNAL_SERVER_ERROR));
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    log.info("Authorization Header Post Filter : Response Code -> {}", response.getStatusCode())
            ));
        };
    }

    //에러 처리
    private Mono<Void> onError(ServerWebExchange exchange, GatewayErrorResponse response) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return serverHttpResponse.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                byte[] errorResponse = objectMapper.writeValueAsBytes(response);
                return bufferFactory.wrap(errorResponse);
            } catch ( Exception e ) {
                log.error("error", e);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
