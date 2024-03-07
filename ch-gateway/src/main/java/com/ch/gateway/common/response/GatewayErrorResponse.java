package com.ch.gateway.common.response;

import com.ch.gateway.code.Errors;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GatewayErrorResponse {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    private int state;
    private String result;
    private String message;
    private Object data;
    private List<FieldError> error;

    private GatewayErrorResponse(final Errors code) {
        this.timestamp = LocalDateTime.now();
        this.state = code.getState();
        this.result = "fail";
        this.message = code.getMessage();
        this.data = Collections.emptyList();
        this.error = Collections.emptyList();
    }

    public static GatewayErrorResponse of(final Errors code) {
        return new GatewayErrorResponse(code);
    }

}
