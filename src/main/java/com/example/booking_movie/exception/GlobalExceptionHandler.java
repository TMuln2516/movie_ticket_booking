package com.example.booking_movie.exception;

import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.AuthenticationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(400)
                .message(exception.getMessage())
                .build());
    }

    @ExceptionHandler(value = MyException.class)
    ResponseEntity<ApiResponse<AuthenticationResponse>> handlingMyException(MyException exception) {
        return ResponseEntity.status(exception.getErrorCode().getCode()).body(
                ApiResponse.<AuthenticationResponse>builder()
                        .code(exception.getErrorCode().getCode())
                        .message(exception.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getCode()).body(
                ApiResponse.builder()
                        .code(ErrorCode.UNAUTHORIZED.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse> handlingHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity.status(exception.getStatusCode().value()).body(
                ApiResponse.builder()
                        .code(exception.getStatusCode().value())
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            ErrorCode errorCode = determineErrorCode(error.getField());
            if (errorCode != null) {
                errors.put(error.getField(), errorCode.getMessage());
            } else {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        });
        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .code(400)
                        .message(ErrorCode.INVALID.getMessage())
                        .result(errors)
                        .build());
    }

    private ErrorCode determineErrorCode(String fieldName) {
        if ("length".equals(fieldName)) {
            return ErrorCode.PASSWORD_LENGTH;
        } else if ("uppercase".equals(fieldName)) {
            return ErrorCode.MISSING_UPPERCASE;
        } else if ("lowercase".equals(fieldName)) {
            return ErrorCode.MISSING_UPPERCASE;
        } else if ("special".equals(fieldName)) {
            return ErrorCode.MISSING_SPECIAL_CHARACTERS;
        } else if ("numerical".equals(fieldName)) {
            return ErrorCode.ILLEGAL_NUMERICAL_SEQUENCE;
        }
        return null;
    }
}
