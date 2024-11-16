package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.AuthenticationRequest;
import com.example.booking_movie.dto.request.IntrospectRequest;
import com.example.booking_movie.dto.request.LogoutRequest;
import com.example.booking_movie.dto.request.RefreshRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.AuthenticationResponse;
import com.example.booking_movie.dto.response.IntrospectResponse;
import com.example.booking_movie.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    //    login
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login Success")
                .result(result)
                .build();
    }

    //    logout
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder()
                .message("Logout Success")
                .build();
    }

    //    refresh token
    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        return ApiResponse.<IntrospectResponse>builder()
                .message("Thành công")
                .result(authenticationService.introspect(introspectRequest))
                .build();
    }

    @PostMapping("/outbound/authentication")
    public ApiResponse<AuthenticationResponse> outboundAuthenticate(@RequestParam String code) {
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Thành công")
                .result(authenticationService.outboundAuthenticate(code))
                .build();
    }
}
