package com.ch.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private UUID userCode;
    private String email;
    private String userName;
    private List<String> roles;

    private List<OrderResponse> orders;

}
