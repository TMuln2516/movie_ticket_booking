package com.example.booking_movie.exception;

import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.AuthenticationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
//        ApiResponse apiResponse = new ApiResponse();
//
//        apiResponse.setCode(400);
//        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .code(400)
                .message(exception.getMessage())
                .build());
    }

    @ExceptionHandler(value = MyException.class)
    ResponseEntity<ApiResponse<AuthenticationResponse>> handlingMyException(MyException exception) {
//        ErrorCode errorCode = exception.getErrorCode();
//        ApiResponse apiResponse = new ApiResponse();
//
//        apiResponse.setCode(errorCode.getCode());
//        apiResponse.setMessage(errorCode.getMessage());
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
}
