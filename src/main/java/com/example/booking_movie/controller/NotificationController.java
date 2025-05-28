package com.example.booking_movie.controller;

import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.ToggleStatusNotificationResponse;
import com.example.booking_movie.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/notifies")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @PutMapping("/{notifyId}")
    public ApiResponse<ToggleStatusNotificationResponse> toggleStatusNotification(@PathVariable String notifyId) {
        return ApiResponse.<ToggleStatusNotificationResponse>builder()
                .message("Cập nhật trạng thái đã đọc thành công")
                .result(notificationService.toggleStatusNotification(notifyId))
                .build();
    }
}
