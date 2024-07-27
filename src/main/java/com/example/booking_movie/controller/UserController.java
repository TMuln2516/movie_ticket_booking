package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateUserRequest;
import com.example.booking_movie.dto.request.UpdateBioRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.BioResponse;
import com.example.booking_movie.dto.response.CreateUserResponse;
import com.example.booking_movie.dto.response.UserResponse;
import com.example.booking_movie.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

//    ROLE USER

    //    get bio
    @GetMapping("/bio")
    public ApiResponse<BioResponse> myBio() {
        return ApiResponse.<BioResponse>builder()
                .message("Get Bio Success")
                .result(userService.getMyBio())
                .build();
    }

    //    create user
    @PostMapping("/")
    public ApiResponse<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        return ApiResponse.<CreateUserResponse>builder()
                .message("Create User Success")
                .result(userService.createUser(createUserRequest))
                .build();
    }

    //    update user
    @PutMapping("/bio")
    public ApiResponse<BioResponse> updateBio(@RequestBody @Valid UpdateBioRequest updateBioRequest) {
        return ApiResponse.<BioResponse>builder()
                .message("Update Bio Success")
                .result(userService.updateBio(updateBioRequest))
                .build();
    }

//    ROLE MANAGER

    //    get all user
    @GetMapping("/")
    public ApiResponse<List<UserResponse>> getAll() {
        return ApiResponse.<List<UserResponse>>builder()
                .message("Get All User Success")
                .result(userService.getAll())
                .build();
    }

    //    ban account
    @PutMapping("/ban/{userId}")
    public ApiResponse<String> updateStatus(@PathVariable String userId) {
        return ApiResponse.<String>builder()
                .message(userService.toggleStatus(userId))
                .build();
    }
}
