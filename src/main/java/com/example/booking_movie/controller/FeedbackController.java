package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateFeedbackRequest;
import com.example.booking_movie.dto.request.CreateRoomRequest;
import com.example.booking_movie.dto.request.UpdateFeedbackRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.FeedbackService;
import com.example.booking_movie.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {
    FeedbackService feedbackService;

    @GetMapping("/")
    public ApiResponse<List<FeedbackResponse>> getAll() {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .message("Lấy tất cả bình luận thành công")
                .result(feedbackService.getAll())
                .build();
    }

    @GetMapping("/{movieId}/all")
    public ApiResponse<List<FeedbackResponse>> getAllByMovie(@PathVariable String movieId) {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .message("Lấy tất cả bình luận theo Phim thành công")
                .result(feedbackService.getAllByMovie(movieId))
                .build();
    }

    @PostMapping("/")
    public ApiResponse<CreateFeedbackResponse> create(@RequestBody @Valid CreateFeedbackRequest createFeedbackRequest) {
        return ApiResponse.<CreateFeedbackResponse>builder()
                .message("Bình luận thành công")
                .result(feedbackService.create(createFeedbackRequest))
                .build();
    }

    @PutMapping("/{feedbackId}")
    public ApiResponse<UpdateFeedbackResponse> update(@PathVariable String feedbackId, @RequestBody @Valid UpdateFeedbackRequest updateFeedbackRequest) {
        return ApiResponse.<UpdateFeedbackResponse>builder()
                .message("Cập nhật bình luận thành công")
                .result(feedbackService.update(feedbackId, updateFeedbackRequest))
                .build();
    }

    @DeleteMapping("/{feedbackId}")
    public ApiResponse<Void> delete(@PathVariable String feedbackId) {
        feedbackService.delete(feedbackId);
        return ApiResponse.<Void>builder()
                .message("Xóa bình luận thành công")
                .build();
    }

    @PutMapping("/toggle/{feedbackId}")
    public ApiResponse<Void> toggleStatus(@PathVariable String feedbackId) {
        feedbackService.toggleStatus(feedbackId);
        return ApiResponse.<Void>builder()
                .message("Cập nhật trạng trái thành công")
                .build();
    }
}
