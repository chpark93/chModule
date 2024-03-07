package com.ch.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "사용자 회원가입 Request")
public class SignUpRequest {

    private SignUpRequest() {}

    @Getter @Setter
    public static class SignUp {

        @NotEmpty(message = "아이디는 필수 입력값입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
        @Schema(description = "아이디", nullable = false, example = "test1234@naver.com")
        private String userId;

        @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(description = "비밀번호", nullable = false, example = "Test1234@@")
        private String password;

        @NotEmpty(message = "이름은 필수 입력값입니다.")
        @Schema(description = "이름", nullable = false, example = "테스트")
        private String name;

    }

}
