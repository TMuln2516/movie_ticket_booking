package com.example.booking_movie.controller;

import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.service.PaymentService;
import com.example.booking_movie.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<Void> callback(@RequestParam(value = "vnp_ResponseCode") String responseCode,
                                      @RequestParam(value = "vnp_TxnRef") String ticketId) {
        String message;
        if (responseCode.equals("00")){
            message = "Thanh toán thành công";
        } else {
            message = "Thanh toán thất bại";
        }
        paymentService.callBackVNPay(responseCode, ticketId);
        return ApiResponse.<Void>builder()
                .message(message)
                .build();
    }
}
