package com.example.booking_movie.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    //    Genre
    GENRE_EXISTED(HttpStatus.BAD_REQUEST.value(), "Genre already existed"),

    //    Invalid password
    MISSING_UPPERCASE(HttpStatus.BAD_REQUEST.value(), "Password must contain 1 or more uppercase characters."),
    MISSING_LOWERCASE(HttpStatus.BAD_REQUEST.value(), "Password must contain 1 or more lowercase characters."),
    MISSING_SPECIAL_CHARACTERS(HttpStatus.BAD_REQUEST.value(), "Password must contain 1 or more special characters."),
    ILLEGAL_NUMERICAL_SEQUENCE(HttpStatus.BAD_REQUEST.value(), "Password contains the illegal numerical sequence '123456'."),
    PASSWORD_LENGTH(HttpStatus.BAD_REQUEST.value(), "Password must be 8 or more characters in length."),

    //    User
    PASSWORD_OR_USERNAME_INCORRECT(HttpStatus.BAD_REQUEST.value(), "Username or Password incorrect"),
    USER_EXISTED(HttpStatus.BAD_REQUEST.value(), "User already existed"),
    USER_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "User not exist"),

    //    Token
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "You do not have permission"),
    UNAUTHENTICATED(HttpStatus.BAD_REQUEST.value(), "Unauthenticated"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST.value(), "Token Invalid"),

    //    Invalid
    INVALID(HttpStatus.BAD_REQUEST.value(), "Invalid Parameter");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
