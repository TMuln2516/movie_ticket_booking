package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedPaymentMethod;
import com.example.booking_movie.dto.request.CreateRoomRequest;
import com.example.booking_movie.dto.request.CreateSeatRequest;
import com.example.booking_movie.dto.response.CreateRoomResponse;
import com.example.booking_movie.entity.*;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    VNPayService vnPayService;
    TicketRepository ticketRepository;
    ScheduleSeatRepository scheduleSeatRepository;
    ShowtimeRepository showtimeRepository;

//    VNPAY
     public String payment(HttpServletRequest request) {
         String method = request.getParameter("method");

         return vnPayService.createPaymentVNPay(request);
     }

    public void callBackVNPay(String responseCode, String ticketId) {
        vnPayService.callBack(responseCode, ticketId);
    }
}
