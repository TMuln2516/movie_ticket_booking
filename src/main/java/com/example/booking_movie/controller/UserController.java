package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateUserRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreateUserResponse;
import com.example.booking_movie.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/create")
    ApiResponse<CreateUserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return ApiResponse.<CreateUserResponse>builder()
                .message("Create user success")
                .result(userService.createUser(createUserRequest))
                .build();
    }
}
