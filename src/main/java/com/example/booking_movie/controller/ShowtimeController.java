package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateShowtimeRequest;
import com.example.booking_movie.dto.request.GetAllShowTimeRequest;
import com.example.booking_movie.dto.request.ToggleStatusSeatInShowtimeRequest;
import com.example.booking_movie.dto.request.UpdateShowtimeRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.ScheduleSeatService;
import com.example.booking_movie.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeController {
    ShowtimeService showtimeService;
    ScheduleSeatService scheduleSeatService;

    //    get all
    @PostMapping("/all")
    public ApiResponse<List<GetAllShowtimeResponse>> getAll(@RequestBody @Valid GetAllShowTimeRequest getAllShowTimeRequest) {
        return ApiResponse.<List<GetAllShowtimeResponse>>builder()
                .message("Lấy tất cả suất chiếu thành công")
                .result(showtimeService.getAll(getAllShowTimeRequest))
                .build();
    }

    //    get one
    @GetMapping("/info/{showtimeId}")
    public ApiResponse<GetOneShowtimeResponses> getAll(@PathVariable String showtimeId) {
        return ApiResponse.<GetOneShowtimeResponses>builder()
                .message("Lấy tất cả suất chiếu thành công")
                .result(showtimeService.getOneShowtime(showtimeId))
                .build();
    }

    @GetMapping("/check/{showtimeId}")
    public ApiResponse<CheckSeatInShowtimeResponse> checkSeatInShowtime(@PathVariable String showtimeId) {
        return ApiResponse.<CheckSeatInShowtimeResponse>builder()
                .message("Lấy thông tin về suất chiếu thành công")
                .result(showtimeService.checkSeatInShowtime(showtimeId))
                .build();
    }

    @GetMapping("/")
    public ApiResponse<List<GetAllShowtimeResponses>> get() {
        return ApiResponse.<List<GetAllShowtimeResponses>>builder()
                .message("Lấy tất cả suất chiếu thành công")
                .result(showtimeService.getAllShowtimes())
                .build();
    }

    //    get all by movie
    @GetMapping("{movieId}/all")
    public ApiResponse<List<GetAllShowtimeResponse>> getAllByMovie(@PathVariable String movieId) {
        return ApiResponse.<List<GetAllShowtimeResponse>>builder()
                .message("Lấy danh sách suất chiếu theo Phim thành công")
                .result(showtimeService.getAllShowtimeByMovie(movieId))
                .build();
    }

    //    get all couple showtime by movie
    @GetMapping("/matching/{movieId}/all")
    public ApiResponse<List<GetAllShowtimeResponse>> getAllCoupleShowtimeByMovie(@PathVariable String movieId) {
        return ApiResponse.<List<GetAllShowtimeResponse>>builder()
                .message("Lấy danh sách suất chiếu theo Phim thành công")
                .result(showtimeService.getAllShowtimeCoupleByMovie(movieId))
                .build();
    }

    //    create
    @PostMapping("/")
    public ApiResponse<CreateShowtimeResponse> create(@RequestBody @Valid CreateShowtimeRequest createShowtimeRequest) {
        return ApiResponse.<CreateShowtimeResponse>builder()
                .message("Create Showtime Success")
                .result(showtimeService.create(createShowtimeRequest))
                .build();
    }

    //    update
    @PutMapping("/{showtimeId}")
    public ApiResponse<UpdateShowtimeResponse> update(@PathVariable String showtimeId, @RequestBody @Valid UpdateShowtimeRequest updateShowtimeRequest) {
        return ApiResponse.<UpdateShowtimeResponse>builder()
                .message("Cập nhật suất chiếu thành công")
                .result(showtimeService.update(showtimeId, updateShowtimeRequest))
                .build();
    }

    //    update status
    @PutMapping("{showtimeId}/updateStatus")
    public ApiResponse<List<ToggleStatusSeatInShowtimeResponse>> toggleStatusSeatInShowtime(
            @PathVariable String showtimeId,
            @RequestBody @Valid ToggleStatusSeatInShowtimeRequest toggleStatusSeatInShowtimeRequest) {
        return ApiResponse.<List<ToggleStatusSeatInShowtimeResponse>>builder()
                .message("Đổi trạng thái ghế trong suất chiếu thành công")
                .result(showtimeService.toggleStatusSeatInShowtime(toggleStatusSeatInShowtimeRequest, showtimeId))
                .build();
    }

    //    get seat
    @GetMapping("/{showtimeId}")
    public ApiResponse<List<ScheduleSeatResponse>> getAllSeat(@PathVariable String showtimeId) {
        return ApiResponse.<List<ScheduleSeatResponse>>builder()
                .message("Lấy tất cả các ghế thành công")
                .result(scheduleSeatService.getAllSeatByShowtimeId(showtimeId))
                .build();
    }

    @DeleteMapping("/{showtimeId}")
    public ApiResponse<Void> delete(@PathVariable String showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ApiResponse.<Void>builder()
                .message("Xóa Suất chiếu thành công")
                .build();
    }
}
