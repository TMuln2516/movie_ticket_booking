package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedTitleEmail;
import com.example.booking_movie.dto.request.CreateSeatRequest;
import com.example.booking_movie.dto.request.MailBody;
import com.example.booking_movie.entity.Otp;
import com.example.booking_movie.entity.Room;
import com.example.booking_movie.entity.Seat;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.OtpRepository;
import com.example.booking_movie.repository.RoomRepository;
import com.example.booking_movie.repository.SeatRepository;
import com.example.booking_movie.utils.SecurityUtils;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VerifyService {
    OtpRepository otpRepository;
    EmailService emailService;

    PasswordEncoder encoder = new BCryptPasswordEncoder(10);

    //    send otp
    public void sendOtp(String email, String title) throws MessagingException, UnsupportedEncodingException {
        //        generate otp
        String otp = SecurityUtils.generateRandomOtp();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .text(otp)
                .build();

//        kiểm tra tồn tại otp
        if (otpRepository.existsByEmail(email)) {
//            update
            var otpInfo = otpRepository.findByEmail(email).orElseThrow();
            otpInfo.setOtp(encoder.encode(otp));
            otpInfo.setExpiryTime(LocalDateTime.now().plusMinutes(30));
            otpRepository.save(otpInfo);
        } else {
            var newOtp = Otp.builder()
                    .otp(encoder.encode(otp))
                    .expiryTime(LocalDateTime.now().plusMinutes(30))
                    .email(email)
                    .build();
            otpRepository.save(newOtp);
        }
        //            gửi mail
        if (title.equals(DefinedTitleEmail.REGISTER)) {
            emailService.sendRegistrationOtp(mailBody);
        }
        if (title.equals(DefinedTitleEmail.FORGOT_PASSWORD)) {
            emailService.sendPasswordResetOtp(mailBody);
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
