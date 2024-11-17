package com.example.booking_movie.controller;

import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.service.PaymentService;
import com.example.booking_movie.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;
    @PostMapping("/")
    public ApiResponse<String> createPayment(HttpServletRequest req) {
        return ApiResponse.<String>builder()
                .message("Success")
                .result(paymentService.payment(req))
                .build();
    }
    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam Map<String, String> params) {
        // Lấy mã phản hồi và thông tin ticketId từ tham số
        String responseCode = params.get("vnp_ResponseCode");
        String ticketId = params.get("vnp_TxnRef");

        // Xử lý thanh toán
        String message;
        if ("00".equals(responseCode)) {
            message = "Thanh toán thành công";
            paymentService.callBackVNPay(responseCode, ticketId); // Gọi dịch vụ để xử lý thanh toán thành công
        } else {
            message = "Thanh toán thất bại";
        }

        // URL của frontend, kèm theo các tham số động
        String frontendUrl = "http://localhost:3000/payment-status";
        String redirectUrl = frontendUrl + "?";

        // Thêm tất cả các tham số vào URL redirect
        for (Map.Entry<String, String> entry : params.entrySet()) {
            redirectUrl += entry.getKey() + "=" + entry.getValue() + "&";
        }

        // Loại bỏ dấu "&" thừa cuối cùng
        redirectUrl = redirectUrl.substring(0, redirectUrl.length() - 1);

        // Redirect người dùng đến frontend với các tham số
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }

}
