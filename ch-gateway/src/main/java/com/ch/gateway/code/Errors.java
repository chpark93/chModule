package com.ch.gateway.code;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Errors {

    // Auth
    EXPIRED_JWT_TOKEN(401, "A001", "Expired Jwt Token"),
    INVALID_JWT_TOKEN(401, "A002", "Invalid Jwt Token"),
    UNSUPPORTED_JWT_TOKEN(401, "A003", "Unsupported Jwt Token"),
    EMPTY_JWT_TOKEN(401, "A004", "Empty Jwt Token"),
    BAD_CREDENTIAL(400, "A005", "Bad Credential"),
    NONE_REFRESH_TOKEN(400, "A006", "None Refresh Token"),
    NOT_MATCH_REFRESH_TOKEN(400, "A007", "Not Match Refresh Token"),
    INTERNAL_SERVER_ERROR(500, "A008", "Internal Server Error"),

    ;

    private final String message;
    private final String code;
    private final int state;

    Errors(final int state, final String code, final String message) {
        this.state = state;
        this.code = code;
        this.message = message;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(Errors::getCode, Errors::getMessage))
    );

    public static Errors of(final String code) {
        return Errors.valueOf(CODE_MAP.get(code));
    }


    public int getState() {
        return state;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
