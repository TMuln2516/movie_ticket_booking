package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateFoodRequest;
import com.example.booking_movie.dto.request.UpdateFoodRequest;
import com.example.booking_movie.dto.response.CreateFoodResponse;
import com.example.booking_movie.dto.response.FoodResponse;
import com.example.booking_movie.dto.response.UpdateFoodResponse;
import com.example.booking_movie.entity.Food;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.FoodRepository;
import com.example.booking_movie.repository.TicketFoodRepository;
import com.example.booking_movie.utils.ValidUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FoodService {
    FoodRepository foodRepository;
    TicketFoodRepository ticketFoodRepository;

    ImageService imageService;

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public CreateFoodResponse create(CreateFoodRequest createFoodRequest, MultipartFile file) throws IOException {
//        check image
        if (file.isEmpty()) {
            throw new MyException(ErrorCode.FOOD_IMAGE_NOT_NULL);
        }

//        check exist
        if (foodRepository.existsByName(createFoodRequest.getName())) {
            throw new MyException(ErrorCode.FOOD_EXISTED);
        }

//        upload image
        var imageResponse = imageService.uploadImage(file, "food");

//        builder
        Food newFood = Food.builder()
                .name(createFoodRequest.getName())
                .price(createFoodRequest.getPrice())
                .image(imageResponse.getImageUrl())
                .publicId(imageResponse.getPublicId())
                .build();
//        save
        foodRepository.save(newFood);

        return CreateFoodResponse.builder()
                .id(newFood.getId())
                .name(newFood.getName())
                .price(newFood.getPrice())
                .image(newFood.getImage())
                .publicId(newFood.getPublicId())
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'USER')")
    public List<FoodResponse> getAll() {
        return foodRepository.findAll().stream()
                .map(food -> FoodResponse.builder()
                        .id(food.getId())
                        .name(food.getName())
                        .price(food.getPrice())
                        .image(food.getImage())
                        .publicId(food.getPublicId())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public UpdateFoodResponse update(String foodId, UpdateFoodRequest updateFoodRequest, MultipartFile file) throws IOException {
//        get food
        Food foodInfo = foodRepository.findById(foodId).orElseThrow(() -> new MyException(ErrorCode.FOOD_NOT_EXISTED));

//        update image
        if (file != null && !file.isEmpty()) {
//            delete image
            imageService.deleteImage(foodInfo.getPublicId());

//            upload image
            var imageResponse = imageService.uploadImage(file, "food");
            foodInfo.setImage(imageResponse.getImageUrl());
            foodInfo.setPublicId(imageResponse.getPublicId());
            foodRepository.save(foodInfo);
        }

//        update khi các giá trị của request không phải là null hoặc ""
        ValidUtils.updateFieldIfNotEmpty(foodInfo::setName, updateFoodRequest.getName());
        ValidUtils.updateFieldIfNotEmpty(foodInfo::setPrice, updateFoodRequest.getPrice());
        foodRepository.save(foodInfo);

        return UpdateFoodResponse.builder()
                .id(foodInfo.getId())
                .name(foodInfo.getName())
                .price(foodInfo.getPrice())
                .image(foodInfo.getImage())
                .publicId(foodInfo.getPublicId())
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public void delete(String foodId) {
        //        get food
        Food foodInfo = foodRepository.findById(foodId).orElseThrow(() -> new MyException(ErrorCode.FOOD_NOT_EXISTED));

        // Xóa tất cả các ticket_food liên quan tới food_id trong bảng ticket_food
        ticketFoodRepository.deleteAllByFoodId(foodId);

//        delete
        foodRepository.delete(foodInfo);
    }
}
