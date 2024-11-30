package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedTitleEmail;
import com.example.booking_movie.dto.request.MailBody;
import com.example.booking_movie.entity.Otp;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.OtpRepository;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VerifyService {
    OtpRepository otpRepository;
    UserRepository userRepository;

    EmailService emailService;

    PasswordEncoder encoder = new BCryptPasswordEncoder(10);

    //    send otp
    public void sendOtp(String email, String title) {
        // Generate OTP
        String otp = SecurityUtils.generateRandomOtp();

        // Kiểm tra tồn tại OTP trong DB
        if (otpRepository.existsByEmail(email)) {
            // Cập nhật OTP
            var otpInfo = otpRepository.findByEmail(email).orElseThrow();
            otpInfo.setOtp(encoder.encode(otp));
            otpInfo.setExpiryTime(LocalDateTime.now().plusMinutes(30));
            otpRepository.save(otpInfo);
        } else {
            // Tạo mới OTP
            var newOtp = Otp.builder()
                    .otp(encoder.encode(otp))
                    .expiryTime(LocalDateTime.now().plusMinutes(30))
                    .email(email)
                    .build();
            otpRepository.save(newOtp);
        }

        // Xây dựng nội dung email
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text(otp)
                .build();

        // Gửi email dựa trên tiêu đề
        if (title.equals(DefinedTitleEmail.REGISTER)) {
            if (userRepository.existsByEmail(email)) {
                throw new MyException(ErrorCode.EMAIL_EXISTED);
            }
            emailService.sendRegistrationOtp(mailBody); // Gửi email xác nhận đăng ký
        } else if (title.equals(DefinedTitleEmail.FORGOT_PASSWORD)) {
            emailService.sendPasswordResetOtp(mailBody); // Gửi email khôi phục mật khẩu
        } else {
            throw new IllegalArgumentException("Invalid title: " + title);
        }
    }


    //    verify otp
    public boolean verifyOTP(String otp, String email) {
//        otp info
        var otpInfo = otpRepository.findByEmail(email).orElseThrow(() -> new MyException(ErrorCode.MAIL_NOT_EXISTED));

        if (!encoder.matches(otp, otpInfo.getOtp())) {
            throw new MyException(ErrorCode.OTP_NOT_MATCH);
        }

        if (otpInfo.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new MyException(ErrorCode.OTP_EXPIRED);
        }

        return true;
    }
}
