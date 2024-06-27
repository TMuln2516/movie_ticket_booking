package com.example.booking_movie.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

//    User
    PASSWORD_OR_USERNAME_INCORRECT(HttpStatus.BAD_REQUEST.value(), "Username or Password incorrect"),
    USER_EXISTED(HttpStatus.BAD_REQUEST.value(), "User already existed"),

//    Token
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "Category existed"),
    UNAUTHENTICATED(HttpStatus.BAD_REQUEST.value(), "Unauthenticated"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST.value(), "Token Invalid");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
