package com.ch.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Schema(description = "사용자 로그인 Request")
public class LoginRequest {

    private LoginRequest() {}

    @Getter
    @Setter
    public static class Login {

        @NotEmpty(message = "아이디는 필수 입력값입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
        @Schema(description = "아이디", nullable = false, example = "test1234@naver.com")
        private String userId;

        @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
        @Schema(description = "비밀번호", nullable = false, example = "Test1234@@")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(userId, password);
        }
    }

    @Getter
    @Setter
    public static class Logout {
        @NotEmpty(message = "잘못된 요청입니다.")
        private String accessToken;

        @NotEmpty(message = "잘못된 요청입니다.")
        private String refreshToken;
    }
}
