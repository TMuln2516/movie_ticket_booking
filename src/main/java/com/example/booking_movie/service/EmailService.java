package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    JavaMailSender javaMailSender;

    // Hàm Gửi OTP để quên mật khẩu
    public void sendPasswordResetOtp(MailBody mailBody) throws MessagingException, UnsupportedEncodingException {
        String subject = "Khôi phục mật khẩu";
        String htmlContent = buildOtpEmailContent(mailBody.text(), "Đây là mã OTP để khôi phục mật khẩu của bạn. " +
                "Vui lòng không chia sẻ mã này với bất kỳ ai.");

        sendEmail(mailBody.to(), subject, htmlContent);
    }

    // Hàm Gửi OTP để xác nhận email khi đăng ký
    public void sendRegistrationOtp(MailBody mailBody) throws MessagingException, UnsupportedEncodingException {
        String subject = "Xác nhận email";
        String htmlContent = buildOtpEmailContent(mailBody.text(), "Đây là mã OTP để xác nhận email của bạn. " +
                "Vui lòng không chia sẻ mã này với bất kỳ ai.");

        sendEmail(mailBody.to(), subject, htmlContent);
    }

    // gửi mail
    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setFrom("utebookingmovie@gmail.com", "LH MOVIE BOOKING");
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    // Nội dung mail
    private String buildOtpEmailContent(String otp, String message) {
        return "<html>"
                + "<body>"
                + "<p>Xin chào,</p>"
                + "<p>" + message + "</p>"
                + "<div style='background-color: lightgray; padding: 10px; display: inline-block;'>"
                + "<h2 style='color: black; font-size: 24px; margin: 0; font-weight: bold; letter-spacing: 2px;'>"
                + otp + "</h2>"
                + "</div>"
                + "<p>From LH MOVIE BOOKING with luv <3</p>"
                + "</body>"
                + "</html>";
    }
}
