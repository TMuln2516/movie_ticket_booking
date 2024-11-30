package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    JavaMailSender javaMailSender;
    BlockingQueue<MailBody> queue = new LinkedBlockingQueue<>();

    // Khởi chạy thread xử lý queue
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
        startQueueProcessor();
    }

    private void startQueueProcessor() {
        Thread mailSenderThread = new Thread(() -> {
            while (true) {
                try {
                    MailBody mailBody = queue.take();
                    sendHtmlMail(mailBody); // Gửi email từ queue
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    // Xử lý lỗi khi gửi email
                }
            }
        });
        mailSenderThread.setName("html-mail-sender-thread");
        mailSenderThread.start();
    }

    // Thêm email vào queue
    public void sendMailToQueue(MailBody mailBody) {
        queue.add(mailBody);
    }

    // Gửi email HTML
    private void sendHtmlMail(MailBody mailBody) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(mailBody.to());
        helper.setFrom("utebookingmovie@gmail.com", "LH MOVIE BOOKING");
        helper.setSubject(mailBody.subject());
        helper.setText(mailBody.text(), true);

        javaMailSender.send(message);
    }

    // Gửi OTP để quên mật khẩu
    public void sendPasswordResetOtp(MailBody mailBody) {
        String subject = "Khôi phục mật khẩu";
        String htmlContent = buildOtpEmailContent(mailBody.text(),
                "Đây là mã OTP để khôi phục mật khẩu của bạn. Vui lòng không chia sẻ mã này với bất kỳ ai.");

        // Tạo MailBody mới với nội dung đã định dạng
        MailBody formattedMailBody = MailBody.builder()
                .to(mailBody.to())
                .subject(subject)
                .text(htmlContent)
                .build();

        sendMailToQueue(formattedMailBody);
    }

    public void sendRegistrationOtp(MailBody mailBody) {
        String subject = "Xác nhận email";
        String htmlContent = buildOtpEmailContent(mailBody.text(),
                "Đây là mã OTP để xác nhận email của bạn. Vui lòng không chia sẻ mã này với bất kỳ ai.");

        // Tạo MailBody mới với nội dung đã định dạng
        MailBody formattedMailBody = MailBody.builder()
                .to(mailBody.to())
                .subject(subject)
                .text(htmlContent)
                .build();

        sendMailToQueue(formattedMailBody);
    }


    // Nội dung mail HTML
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
