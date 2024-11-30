package com.example.booking_movie.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    VNPayService vnPayService;

//    VNPAY
     public String payment(HttpServletRequest request) {
         String method = request.getParameter("method");

         return vnPayService.createPaymentVNPay(request);
     }

    public void callBackVNPay(String responseCode, String ticketId) {
        vnPayService.callBack(responseCode, ticketId);
    }
}
