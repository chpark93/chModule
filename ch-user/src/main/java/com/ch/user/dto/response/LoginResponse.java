package com.ch.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 이후 토큰 Response")
public class LoginResponse {

    private LoginResponse() {}

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenInfo {
        @Schema(description = "액세스 토큰", example = "accessToken")
        private String accessToken;
        @Schema(description = "리프레시 토큰", example = "refreshToken")
        private String refreshToken;
        @JsonIgnore
        private Long refreshTokenExpirationTime;
        @JsonIgnore
        private String grantType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenFlag {
        private Boolean successFlag;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Logout {
        private String userId;
    }

}
