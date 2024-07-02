package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateUserRequest;
import com.example.booking_movie.dto.request.UpdateBioRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.BioResponse;
import com.example.booking_movie.dto.response.CreateUserResponse;
import com.example.booking_movie.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/bio")
    public ApiResponse<BioResponse> myBio() {
        return ApiResponse.<BioResponse>builder()
                .message("Get Bio Success")
                .result(userService.getMyBio())
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<CreateUserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return ApiResponse.<CreateUserResponse>builder()
                .message("Create User Success")
                .result(userService.createUser(createUserRequest))
                .build();
    }

    @PutMapping("/update")
    public ApiResponse<BioResponse> updateBio(@RequestBody UpdateBioRequest updateBioRequest) {
        return ApiResponse.<BioResponse>builder()
                .message("Update Bio Success")
                .result(userService.updateBio(updateBioRequest))
                .build();
    }

}
