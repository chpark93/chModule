package com.ch.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "회원가입 Response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse {

    @Schema(description = "아이디", example = "test1234@naver.com")
    private String userId;
    @Schema(description = "이름", example = "마징가")
    private String name;
    @Schema(description = "Role", example = "ROLE_CLIENT")
    private List<String> roles;
}
