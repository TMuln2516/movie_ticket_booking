package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateUserRequest;
import com.example.booking_movie.dto.request.UpdateBioRequest;
import com.example.booking_movie.dto.response.BioResponse;
import com.example.booking_movie.dto.response.CreateUserResponse;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

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
                .date_of_birth(DateUtils.formatDateTime(newUser.getDate_of_birth()))
                .gender(newUser.getGender())
                .email(newUser.getEmail())
                .avatar(newUser.getAvatar())
                .build();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public BioResponse getMyBio() {
//        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        return BioResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .first_name(user.getFirst_name())
                .last_name(user.getLast_name())
                .date_of_birth(DateUtils.formatDateTime(user.getDate_of_birth()))
                .gender(user.getGender())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public BioResponse updateBio(UpdateBioRequest updateBioRequest) {
//        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        log.info(String.valueOf(updateBioRequest));

//        update with not null field
        updateFieldIfNotEmpty(user::setFirst_name, updateBioRequest.getFirst_name());
        updateFieldIfNotEmpty(user::setLast_name, updateBioRequest.getLast_name());
        updateFieldIfNotEmpty(user::setDate_of_birth, updateBioRequest.getDate_of_birth());
        updateFieldIfNotEmpty(user::setEmail, updateBioRequest.getEmail());
        updateFieldIfNotEmpty(user::setAvatar, updateBioRequest.getAvatar());

        userRepository.save(user);
        log.info("Update bio: " + user);

        return BioResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .first_name(user.getFirst_name())
                .last_name(user.getLast_name())
                .date_of_birth(DateUtils.formatDateTime(user.getDate_of_birth()))
                .gender(user.getGender())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }

    private <T> void updateFieldIfNotEmpty(Consumer<T> setter, T value) {
//        check value not null + value is String and value not empty
        if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
            setter.accept(value);
        }
    }

    public boolean existedByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
