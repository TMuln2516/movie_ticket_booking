package com.example.booking_movie.service;

import com.example.booking_movie.dto.response.ToggleStatusNotificationResponse;
import com.example.booking_movie.dto.response.UserResponse;
import com.example.booking_movie.entity.Notification;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.NotificationRepository;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.utils.DateUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    public ToggleStatusNotificationResponse toggleStatusNotification(String notifyId) {
//        get notify info
        Notification notificationInfo = notificationRepository.findById(notifyId).orElseThrow(() -> new MyException(ErrorCode.NOTIFY_NOT_EXISTED));

//        get user info
        User userInfo = userRepository.findById(notificationInfo.getUserId()).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        toggle isRead
        notificationInfo.setIsRead(!notificationInfo.getIsRead());
        notificationRepository.save(notificationInfo);

        return ToggleStatusNotificationResponse.builder()
                .id(notificationInfo.getId())
                .data(notificationInfo.getData())
                .isRead(notificationInfo.getIsRead())
                .message(notificationInfo.getMessage())
                .user(UserResponse.builder()
                        .id(userInfo.getId())
                        .username(userInfo.getUsername())
                        .firstName(userInfo.getFirstName())
                        .lastName(userInfo.getLastName())
                        .dateOfBirth(userInfo.getDateOfBirth() != null ? DateUtils.formatDate(userInfo.getDateOfBirth()) : null)
                        .gender(userInfo.getGender())
                        .email(userInfo.getEmail())
                        .avatar(userInfo.getAvatar())
                        .roles(userInfo.getRoles())
                        .build())
                .build();
    }

    @Transactional
    public void deleteAllByToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User userInfo = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        delete
        notificationRepository.deleteAllByUserId(userInfo.getId());
    }

}
