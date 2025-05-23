package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.*;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
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

    //    get bio
    @GetMapping("/{userId}")
    public ApiResponse<BioResponse> getOneUser(@PathVariable String userId) {
        return ApiResponse.<BioResponse>builder()
                .message("Get One User Success")
                .result(userService.getOneUser(userId))
                .build();
    }

//    get ticket
    @GetMapping("/ticket")
    public ApiResponse<List<TicketDetailResponse>> myTicket() {
        return ApiResponse.<List<TicketDetailResponse>>builder()
                .message("Lấy thông tin vé thành công")
                .result(userService.myTicket())
                .build();
    }

    //    create user
    @PostMapping("/")
    public ApiResponse<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws MessagingException {
        return ApiResponse.<CreateUserResponse>builder()
                .message("Create User Success")
                .result(userService.createUser(createUserRequest))
                .build();
    }

    @PostMapping("/createPassword")
    public ApiResponse<Void> createPassword(@RequestBody @Valid CreatePasswordRequest createPasswordRequest) {
        userService.createPassword(createPasswordRequest);
        return ApiResponse.<Void>builder()
                .message("Tạo mật khẩu mới thành công")
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

    @PutMapping("/avatar")
    public ApiResponse<ImageResponse> uploadAvatar(@RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<ImageResponse>builder()
                .message("Tải lên Avatar thành công")
                .result(userService.uploadAvatar(file))
                .build();
    }

    @PutMapping("/changePassword")
    public ApiResponse<Void> forgotPassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest);
        return ApiResponse.<Void>builder()
                .message("Đổi mật khẩu thành công")
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

//    ROLE ADMIN
//    @PostMapping("/createManager")
//    public ApiResponse<CreateManagerResponse> createManager(@RequestBody @Valid CreateManagerRequest createManagerRequest) {
//        return ApiResponse.<CreateManagerResponse>builder()
//                .message("Tạo tài khoản Manager thành công")
//                .result(userService.createManager(createManagerRequest))
//                .build();
//    }

    @DeleteMapping("/{accountId}")
    public ApiResponse<Void> deleteAccount(@PathVariable String accountId) {
        userService.deleteAccount(accountId);
        return ApiResponse.<Void>builder()
                .message("Xóa tài khoản thành công")
                .build();
    }
}
