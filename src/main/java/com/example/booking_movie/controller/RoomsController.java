package com.example.booking_movie.controller;

import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.RoomResponse;
import com.example.booking_movie.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomsController {
    RoomService roomService;

    @GetMapping("/getAll")
    public ApiResponse<List<RoomResponse>> getAll() {
        return ApiResponse.<List<RoomResponse>>builder()
                .message("Lấy danh sách phòng thành công")
                .result(roomService.getAll())
                .build();
    }

    @GetMapping("/getAll/{theaterId}")
    public ApiResponse<List<RoomResponse>> getAllByTheater(@PathVariable String theaterId) {
        return ApiResponse.<List<RoomResponse>>builder()
                .message("Lấy danh sách phòng thành công")
                .result(roomService.getAllRoomByTheater(theaterId))
                .build();
    }
}
