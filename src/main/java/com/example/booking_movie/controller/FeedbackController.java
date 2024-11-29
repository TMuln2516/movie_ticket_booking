package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateFeedbackRequest;
import com.example.booking_movie.dto.request.CreateRoomRequest;
import com.example.booking_movie.dto.request.UpdateFeedbackRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreateFeedbackResponse;
import com.example.booking_movie.dto.response.CreateRoomResponse;
import com.example.booking_movie.dto.response.UpdateFeedbackResponse;
import com.example.booking_movie.service.FeedbackService;
import com.example.booking_movie.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {
    FeedbackService feedbackService;

    @PostMapping("/")
    public ApiResponse<CreateFeedbackResponse> create(@RequestBody @Valid CreateFeedbackRequest createFeedbackRequest) {
        return ApiResponse.<CreateFeedbackResponse>builder()
                .message("Bình luận thành công")
                .result(feedbackService.create(createFeedbackRequest))
                .build();
    }

    @PutMapping("/{movieId}")
    public ApiResponse<UpdateFeedbackResponse> update(@PathVariable String movieId, @RequestBody @Valid UpdateFeedbackRequest updateFeedbackRequest) {
        return ApiResponse.<UpdateFeedbackResponse>builder()
                .message("Cập nhật bình luận thành công")
                .result(feedbackService.update(movieId, updateFeedbackRequest))
                .build();
    }
}
