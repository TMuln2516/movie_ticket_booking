package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.dto.request.CreateUserRequest;
import com.example.booking_movie.dto.request.UpdateBioRequest;
import com.example.booking_movie.dto.response.BioResponse;
import com.example.booking_movie.dto.response.CreateUserResponse;
import com.example.booking_movie.dto.response.UserResponse;
import com.example.booking_movie.entity.Role;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.RoleRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    RoleRepository roleRepository;
    UserRepository userRepository;

    //    create new user
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

//        check user existed
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new MyException(ErrorCode.USER_EXISTED);
        }

//       set roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(DefinedRole.USER_ROLE).orElseThrow());

//        create new user
        User newUser = User.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .dateOfBirth(createUserRequest.getDateOfBirth())
                .gender(createUserRequest.isGender())
                .email(createUserRequest.getEmail())
                .avatar(createUserRequest.getAvatar())
                .status(true)
                .roles(roles)
                .build();

//        log.info("User: " + newUser);
//        update db
        userRepository.save(newUser);

        return CreateUserResponse.builder()
                .id(newUser.getId())
                .username(newUser.getUsername())
                .password(newUser.getPassword())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .gender(newUser.getGender())
                .email(newUser.getEmail())
                .avatar(newUser.getAvatar())
                .build();
    }

    //    get my bio
    @PostAuthorize("returnObject.username == authentication.name")
    public BioResponse getMyBio() {
//        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        return BioResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth() != null ? DateUtils.formatDateTime(user.getDateOfBirth()) : null)
                .gender(user.getGender())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }

    //    update my bio
    @PostAuthorize("returnObject.username == authentication.name")
    public BioResponse updateBio(UpdateBioRequest updateBioRequest) {
//        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        log.info(String.valueOf(updateBioRequest));

//        update with not null field
        updateFieldIfNotEmpty(user::setFirstName, updateBioRequest.getFirstName());
        updateFieldIfNotEmpty(user::setLastName, updateBioRequest.getLastName());
        updateFieldIfNotEmpty(user::setDateOfBirth, updateBioRequest.getDateOfBirth());
        updateFieldIfNotEmpty(user::setEmail, updateBioRequest.getEmail());
        updateFieldIfNotEmpty(user::setAvatar, updateBioRequest.getAvatar());

        userRepository.save(user);
//        log.info("Update bio: " + user);

        return BioResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth() != null ? DateUtils.formatDateTime(user.getDateOfBirth()) : null)
                .gender(user.getGender())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }

    //    function check null field before update
    private <T> void updateFieldIfNotEmpty(Consumer<T> setter, T value) {
//        check value not null + value is String and value not empty
        if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
            setter.accept(value);
        }
    }


    //    ban account -> role manager
    @PreAuthorize("hasRole('MANAGER')")
    public String toggleStatus(String id) {
//        get user
        User user = userRepository.findById(id).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        update status
        user.setStatus(!user.getStatus());
        userRepository.save(user);

        return user.getStatus() ? "Account has been unbanned" : "Account has been banned";
    }

    @PreAuthorize("hasRole('MANAGER')")
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dateOfBirth(user.getDateOfBirth() != null ? DateUtils.formatDateTime(user.getDateOfBirth()) : null)
                        .gender(user.getGender())
                        .email(user.getEmail())
                        .avatar(user.getAvatar())
                        .roles(user.getRoles())
                        .build())
                .collect(Collectors.toList());
    }
}
