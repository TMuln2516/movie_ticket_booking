package com.example.booking_movie.controller;

import com.example.booking_movie.constant.DefinedTitleEmail;
import com.example.booking_movie.dto.request.EmailRequest;
import com.example.booking_movie.dto.request.VerifyOtpRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.service.VerifyService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/verify")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VerifyController {
    VerifyService verifyService;

//    gửi otp xác nhận khi đăng ký
    @PostMapping("/registration")
    public ApiResponse<String> sendOtpRegistration(@RequestBody @Valid EmailRequest emailRequest) throws MessagingException, UnsupportedEncodingException {
        verifyService.sendOtp(emailRequest.getEmail(), DefinedTitleEmail.REGISTER);
        return ApiResponse.<String>builder()
                .result("Gửi OTP thành công")
                .build();
    }

    @PostMapping("/forgotPassword")
    public ApiResponse<String> sendOtpForgotPassword(@RequestBody @Valid EmailRequest emailRequest) throws MessagingException, UnsupportedEncodingException {
        verifyService.sendOtp(emailRequest.getEmail(), DefinedTitleEmail.FORGOT_PASSWORD);
        return ApiResponse.<String>builder()
                .result("Gửi OTP thành công")
                .build();
    }

//    verify otp
    @PostMapping("/verifyOtp")
    public ApiResponse<Boolean> verifyOtp(@RequestBody @Valid VerifyOtpRequest verifyOtpRequest) {
        return ApiResponse.<Boolean>builder()
                .result(verifyService.verifyOTP(verifyOtpRequest.getOtp(), verifyOtpRequest.getEmail()))
                .build();
    }
}
