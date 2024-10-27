package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateRoomRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreateRoomResponse;
import com.example.booking_movie.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;

//    ROLE MANAGER
//    create room
    @PostMapping("/{theaterId}/rooms")
    public ApiResponse<CreateRoomResponse> create(@PathVariable String theaterId, @RequestBody CreateRoomRequest createRoomRequest) {
        return ApiResponse.<CreateRoomResponse>builder()
                .message("Create Room Success")
                .result(roomService.create(theaterId, createRoomRequest))
                .build();
    }

    @DeleteMapping("/{theaterId}/rooms/{roomId}")
    public ApiResponse<Void> delete(@PathVariable String roomId, @PathVariable String theaterId) {
        roomService.delete(roomId, theaterId);
        return ApiResponse.<Void>builder()
                .message("Delete Room Success")
                .build();
    }
}
