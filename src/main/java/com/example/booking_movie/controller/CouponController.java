package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.*;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.CouponService;
import com.example.booking_movie.service.GenreService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponController {
    CouponService couponService;

    @GetMapping("/")
    public ApiResponse<List<CouponResponse>> getAll() {
        return ApiResponse.<List<CouponResponse>>builder()
                .message("Lấy tất cả phiếu giảm giá thành công")
                .result(couponService.getAll())
                .build();
    }

    @GetMapping("/{couponId}")
    public ApiResponse<CouponResponse> getDetail(@PathVariable String couponId) {
        return ApiResponse.<CouponResponse>builder()
                .message("Lấy thông tin chi tiết phiếu giảm giá thành công")
                .result(couponService.getDetails(couponId))
                .build();
    }

    @PostMapping("/")
    public ApiResponse<CreateCouponResponse> create(
            @RequestPart("createCouponRequest") @Valid CreateCouponRequest createCouponRequest,
            @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<CreateCouponResponse>builder()
                .message("Tạo phiếu giảm giá thành công")
                .result(couponService.create(createCouponRequest, file))
                .build();
    }

    @PutMapping("/{couponId}")
    public ApiResponse<UpdateCouponResponse> update(@PathVariable String couponId, @RequestBody UpdateCouponRequest updateCouponRequest) {
        return ApiResponse.<UpdateCouponResponse>builder()
                .message("Cập nhật phiếu giảm giá thành công")
                .result(couponService.update(couponId, updateCouponRequest))
                .build();
    }

    @PutMapping("/toggle/{couponId}")
    public ApiResponse<Void> toggleStatus(@PathVariable String couponId) {
        couponService.toggleStatus(couponId);
        return ApiResponse.<Void>builder()
                .message("Cập nhật trạng thái thành công")
                .build();
    }

    @DeleteMapping("/{couponId}")
    public ApiResponse<Void> delete(@PathVariable String couponId) {
        couponService.delete(couponId);
        return ApiResponse.<Void>builder()
                .message("Xoá phiếu giảm giá thành công")
                .build();
    }
}
