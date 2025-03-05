package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateCouponRequest;
import com.example.booking_movie.dto.request.CreateFoodRequest;
import com.example.booking_movie.dto.request.UpdateCouponRequest;
import com.example.booking_movie.dto.request.UpdateFoodRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.CouponService;
import com.example.booking_movie.service.FoodService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodController {
    FoodService foodService;

    //   get all
    @GetMapping("/")
    public ApiResponse<List<FoodResponse>> getAll() {
        return ApiResponse.<List<FoodResponse>>builder()
                .message("Lấy tất cả món ăn thành công")
                .result(foodService.getAll())
                .build();
    }

    //   create
    @PostMapping("/")
    public ApiResponse<CreateFoodResponse> create(
            @RequestPart("createFoodRequest") @Valid CreateFoodRequest createFoodRequest,
            @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<CreateFoodResponse>builder()
                .message("Tạo món ăn thành công")
                .result(foodService.create(createFoodRequest, file))
                .build();
    }

    //   update
    @PutMapping("/{foodId}")
    public ApiResponse<UpdateFoodResponse> update(
            @PathVariable("foodId") String foodId,
            @RequestPart("updateFoodRequest") @Valid UpdateFoodRequest updateFoodRequest,
            @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<UpdateFoodResponse>builder()
                .message("Cập nhật món ăn thành công")
                .result(foodService.update(foodId, updateFoodRequest, file))
                .build();
    }

    //   delete
    @DeleteMapping("/{foodId}")
    public ApiResponse<Void> delete(@PathVariable("foodId") String foodId) {
        foodService.delete(foodId);
        return ApiResponse.<Void>builder()
                .message("Xóa món ăn thành công")
                .build();
    }
}
