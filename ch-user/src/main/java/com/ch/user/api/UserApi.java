package com.ch.user.api;

import com.ch.core.common.response.ErrorResponse;
import com.ch.core.common.response.Response;
import com.ch.core.utils.HelperUtil;
import com.ch.user.application.UserService;
import com.ch.user.dto.request.LoginRequest;
import com.ch.user.dto.request.SignUpRequest;
import com.ch.user.dto.request.UserRequest;
import com.ch.user.dto.response.LoginResponse;
import com.ch.user.dto.response.SignUpResponse;
import com.ch.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "User 로그인, 회원가입 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;
    private final Response response;

    @Operation(summary = "Login", description = "User Login API.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoginResponse.TokenInfo.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @Parameter(name = "userId", description = "아이디", example = "test1234@naver.com")
    @Parameter(name = "password", description = "비밀번호", example = "Test1234@@")
    @PostMapping(value = "/users/public/login")
    public ResponseEntity<Response.Body> login(@RequestBody @Validated LoginRequest.Login loginRequest, Errors errors) {
        // validation check
        if ( errors.hasErrors() ) {
            return response.invalidFields(HelperUtil.refineErrors(errors));
        }

        return response.success(userService.login(loginRequest), HttpStatus.OK);
    }

    @Operation(summary = "SignUp", description = "User SignUp API.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = SignUpResponse.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @Parameter(name = "userId", description = "아이디", example = "test1234@naver.com")
    @Parameter(name = "password", description = "비밀번호", example = "Test1234@@")
    @Parameter(name = "name", description = "이름", example = "테스트")
    @PostMapping(value = "/users/public/signup")
    public ResponseEntity<Response.Body> signup(@RequestBody @Validated SignUpRequest.SignUp signUp, Errors errors ) {
        // validation check
        if ( errors.hasErrors() ) {
            return response.invalidFields(HelperUtil.refineErrors(errors));
        }

        return response.success(userService.signUp(signUp), HttpStatus.OK);
    }

    @Operation(summary = "RefreshToken", description = "User Token Refresh API.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoginResponse.TokenInfo.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/users/public/refreshToken")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<Response.Body> refreshToken(HttpServletRequest request, @RequestBody @Validated UserRequest.ReIssue refreshToken, Errors errors) {
        // validation check
        if ( errors.hasErrors() ) {
            return response.invalidFields(HelperUtil.refineErrors(errors));
        }

        return response.success(userService.refreshToken(request, refreshToken), HttpStatus.OK);
    }

    @Operation(summary = "Logout", description = "User Logout API.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = LoginResponse.Logout.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/users/logout")
    public ResponseEntity<Response.Body> logout(HttpServletRequest request, @RequestBody @Validated LoginRequest.Logout logout, Errors errors) {
        // validation check
        if ( errors.hasErrors() ) {
            return response.invalidFields(HelperUtil.refineErrors(errors));
        }

        return response.success(userService.logout(request, logout), HttpStatus.OK);
    }

    @Operation(summary = "getProfileFromUserId", description = "User getProfileFromUserId API.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/users/{userId}")
    public ResponseEntity<Response.Body> getProfileFromUserId(@PathVariable String userId) {
        UserResponse userInfo = userService.getProfileFromUserId(userId);

        return response.success(userInfo, HttpStatus.OK);
    }

}
