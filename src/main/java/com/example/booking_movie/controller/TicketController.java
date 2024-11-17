package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateTicketRequest;
import com.example.booking_movie.dto.request.SetSeatSessionRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreateTicketResponse;
import com.example.booking_movie.service.TicketService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketController {
    TicketService ticketService;

//    @PostMapping("/")
//    public ApiResponse<CreateTicketResponse> create(HttpSession httpSession) {
//        return ApiResponse.<CreateTicketResponse>builder()
//                .message("Tạo hóa đơn thành công")
//                .result(ticketService.create(httpSession))
//                .build();
//    }

    @PostMapping("/")
    public ApiResponse<CreateTicketResponse> create(@RequestBody @Valid CreateTicketRequest createTicketRequest) {
        return ApiResponse.<CreateTicketResponse>builder()
                .message("Tạo hóa đơn thành công")
                .result(ticketService.create(createTicketRequest))
                .build();
    }

    @PostMapping("/saveSeat")
    public ApiResponse<Void> saveSeatSession(@RequestBody @Valid SetSeatSessionRequest setSeatSessionRequest, HttpSession httpSession) {
        ticketService.saveSeatsToSession(httpSession, setSeatSessionRequest);
        return ApiResponse.<Void>builder()
                .message("Lưu thông tin ghế thành công")
                .build();
    }
}
