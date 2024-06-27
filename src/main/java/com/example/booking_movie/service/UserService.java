package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateUserRequest;
import com.example.booking_movie.dto.response.CreateUserResponse;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

//        check user existed
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new MyException(ErrorCode.USER_EXISTED);
        }

//        create new user
        User newUser = User.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .first_name(createUserRequest.getFirst_name())
                .last_name(createUserRequest.getLast_name())
                .date_of_birth(createUserRequest.getDate_of_birth())
                .gender(createUserRequest.isGender())
                .email(createUserRequest.getEmail())
                .avatar(createUserRequest.getAvatar())
                .build();

//        init role

        log.info("User: " + newUser);
//        update db
        userRepository.save(newUser);

        return CreateUserResponse.builder()
                .id(newUser.getId())
                .username(newUser.getUsername())
                .password(newUser.getPassword())
                .first_name(newUser.getFirst_name())
                .last_name(newUser.getLast_name())
                .date_of_birth(newUser.getDate_of_birth())
                .gender(newUser.isGender())
                .email(newUser.getEmail())
                .avatar(newUser.getAvatar())
                .build();

    }
}
