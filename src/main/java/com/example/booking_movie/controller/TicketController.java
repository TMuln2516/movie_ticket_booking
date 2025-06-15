package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateTicketRequest;
import com.example.booking_movie.dto.request.SetSeatSessionRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.TicketService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketController {
    TicketService ticketService;

    @GetMapping("/ticket")
    public ApiResponse<List<GetAllTicketResponse>> getAll() {
        return ApiResponse.<List<GetAllTicketResponse>>builder()
                .message("Lấy tất cả hóa đơn thành công")
                .result(ticketService.getAllTicket())
                .build();
    }

    @GetMapping("/ticket/{ticketId}")
    public ApiResponse<List<GetTicketDetailResponse>> getDetailByTicketId(@PathVariable String ticketId) {
        return ApiResponse.<List<GetTicketDetailResponse>>builder()
                .message("Lấy chi tiết thông tin hóa đơn thành công")
                .result(ticketService.getTicketById(ticketId))
                .build();
    }

    @PostMapping("/")
    public ApiResponse<CreateTicketResponse> create(@RequestBody @Valid CreateTicketRequest createTicketRequest) {
        return ApiResponse.<CreateTicketResponse>builder()
                .message("Tạo hóa đơn thành công")
                .result(ticketService.create(createTicketRequest))
                .build();
    }

}
