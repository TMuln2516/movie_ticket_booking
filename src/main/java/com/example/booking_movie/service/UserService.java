package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.dto.request.*;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.entity.Feedback;
import com.example.booking_movie.entity.Role;
import com.example.booking_movie.entity.TicketDetails;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import com.example.booking_movie.utils.DateUtils;
import com.example.booking_movie.utils.ValidUtils;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    RoleRepository roleRepository;
    UserRepository userRepository;
    OtpRepository otpRepository;
    FeedbackRepository feedbackRepository;
    TicketRepository ticketRepository;

    VerifyService verifyService;
    ImageService imageService;

    PasswordEncoder encoder = new BCryptPasswordEncoder(10);

    //    create new user
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {

//        verify otp
        verifyService.verifyOTP(createUserRequest.getOtp(), createUserRequest.getEmail());

//        check user existed
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new MyException(ErrorCode.USER_EXISTED);
        }

//       set roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findById(createUserRequest.getRole().toUpperCase()).orElseThrow());

//        set feedback
        Set<Feedback> feedbacks = new HashSet<>();

//        create new user
        User newUser = User.builder()
                .username(createUserRequest.getUsername())
                .password(encoder.encode(createUserRequest.getPassword()))
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .dateOfBirth(createUserRequest.getDateOfBirth())
                .gender(createUserRequest.getGender())
                .email(createUserRequest.getEmail())
                .avatar(createUserRequest.getAvatar())
                .status(true)
                .roles(roles)
                .feedbacks(feedbacks)
                .build();

//        log.info("User: " + newUser);
//        update db
        userRepository.save(newUser);

//        delete info otp
        var otpInfo = otpRepository.findByEmail(createUserRequest.getEmail()).orElseThrow();
        otpRepository.delete(otpInfo);

        return CreateUserResponse.builder()
                .id(newUser.getId())
                .username(newUser.getUsername())
                .password(encoder.encode(newUser.getPassword()))
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .dateOfBirth(newUser.getDateOfBirth() != null ? DateUtils.formatDate(newUser.getDateOfBirth()) : null)
                .gender(newUser.getGender())
                .email(newUser.getEmail())
                .avatar(newUser.getAvatar())
                .build();
    }

    //    create password when login with google
    public void createPassword(CreatePasswordRequest createPasswordRequest) {
        //        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        if (StringUtils.hasText(user.getPassword())) {
            throw new MyException(ErrorCode.PASSWORD_EXISTED);
        }

        user.setPassword(encoder.encode(createPasswordRequest.getPassword()));
        userRepository.save(user);
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
                .dateOfBirth(user.getDateOfBirth() != null ? DateUtils.formatDate(user.getDateOfBirth()) : null)
                .gender(user.getGender())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .hasPassword(StringUtils.hasText(user.getPassword()))
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
        ValidUtils.updateFieldIfNotEmpty(user::setFirstName, updateBioRequest.getFirstName());
        ValidUtils.updateFieldIfNotEmpty(user::setLastName, updateBioRequest.getLastName());
        ValidUtils.updateFieldIfNotEmpty(user::setDateOfBirth, updateBioRequest.getDateOfBirth());
        ValidUtils.updateFieldIfNotEmpty(user::setEmail, updateBioRequest.getEmail());
        ValidUtils.updateFieldIfNotEmpty(user::setAvatar, updateBioRequest.getAvatar());

        userRepository.save(user);
//        log.info("Update bio: " + user);

        return BioResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth() != null ? DateUtils.formatDate(user.getDateOfBirth()) : null)
                .gender(user.getGender())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    public List<TicketDetailResponse> myTicket() {
        //        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        return ticketRepository.findAllByUserId(user.getId()).stream()
                .filter(ticket -> ticket.getStatus() != null && ticket.getStatus())
                .map(ticket -> TicketDetailResponse.builder()
                        .id(ticket.getId())
                        .date(DateUtils.formatDate(ticket.getDate()))
                        .time(DateUtils.formatTime(ticket.getTime()))
                        .startTime(DateUtils.formatTime(ticket.getShowtime().getStartTime()))
                        .endTime(DateUtils.formatTime(ticket.getShowtime().getEndTime()))
                        .movieName(ticket.getShowtime().getMovie().getName())
                        .movieId(ticket.getShowtime().getMovie().getId())
                        .theaterName(ticket.getShowtime().getRoom().getTheater().getName())
                        .roomName(ticket.getShowtime().getRoom().getName())
                        .canComment(ticket.getFinished() != null && ticket.getFinished())
                        .totalPrice(ticket.getTicketDetails().stream()
                                .mapToDouble(TicketDetails::getPrice)
                                .sum())
                        .seats(ticket.getTicketDetails().stream().map(
                                ticketDetails -> SeatResponse.builder()
                                        .id(ticketDetails.getSeat().getId())
                                        .locateRow(ticketDetails.getSeat().getLocateRow())
                                        .locateColumn(ticketDetails.getSeat().getLocateColumn())
                                        .price(ticketDetails.getPrice())
                                        .build()
                        ).collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    public ImageResponse uploadAvatar(MultipartFile file) throws IOException {
        //        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        //        upload image
        var imageResponse = imageService.uploadImage(file, user.getUsername());

//        update avatar
        user.setAvatar(imageResponse.getImageUrl());
        user.setPublicId(imageResponse.getPublicId());
        userRepository.save(user);

        return ImageResponse.builder()
                .imageUrl(imageResponse.getImageUrl())
                .publicId(imageResponse.getPublicId())
                .build();
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        verifyService.verifyOTP(changePasswordRequest.getOtp(), changePasswordRequest.getEmail());

        if (!changePasswordRequest.getPassword().equals(changePasswordRequest.getPasswordConfirm())) {
            throw new MyException(ErrorCode.CONFIRM_PASS_NOT_MATCH);
        }

        var user = userRepository.findByEmail(changePasswordRequest.getEmail())
                .orElseThrow();

        user.setPassword(encoder.encode(changePasswordRequest.getPassword()));
        userRepository.save(user);

//        delete otp info
        var otpInfo = otpRepository.findByEmail(changePasswordRequest.getEmail()).orElseThrow();
        otpRepository.delete(otpInfo);
    }

    //    ban account -> role manager
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String toggleStatus(String id) {
//        get user
        User user = userRepository.findById(id).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        update status
        user.setStatus(!user.getStatus());
        userRepository.save(user);

        return user.getStatus() ? "Account has been unbanned" : "Account has been banned";
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<UserResponse> getAll() {
        //        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals(DefinedRole.ADMIN_ROLE));
//        boolean isManager = currentUser.getRoles().stream()
//                .anyMatch(role -> role.getName().equals(DefinedRole.MANAGER_ROLE));

        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(username))
                .toList();

        if (isAdmin) {
            return users.stream()
                    .map(user -> UserResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .dateOfBirth(user.getDateOfBirth() != null ? DateUtils.formatDate(user.getDateOfBirth()) : null)
                            .gender(user.getGender())
                            .email(user.getEmail())
                            .status(user.getStatus())
                            .avatar(user.getAvatar())
                            .roles(user.getRoles())
                            .build())
                    .collect(Collectors.toList());
        }

//        if (isManager) {
//            return users.stream()
//                    .filter(user -> user.getRoles().stream()
//                            .anyMatch(role -> role.getName().equals(DefinedRole.USER_ROLE)))
//                    .map(user -> UserResponse.builder()
//                            .id(user.getId())
//                            .username(user.getUsername())
//                            .firstName(user.getFirstName())
//                            .lastName(user.getLastName())
//                            .dateOfBirth(user.getDateOfBirth() != null ? DateUtils.formatDate(user.getDateOfBirth()) : null)
//                            .gender(user.getGender())
//                            .email(user.getEmail())
//                            .avatar(user.getAvatar())
//                            .roles(user.getRoles())
//                            .build())
//                    .collect(Collectors.toList());
//        }

        return Collections.emptyList();
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    public CreateManagerResponse createManager(CreateManagerRequest createManagerRequest) {
//        //        check user existed
//        if (userRepository.existsByUsername(createManagerRequest.getUsername())) {
//            throw new MyException(ErrorCode.MANAGER_EXISTED);
//        }
//
////       set roles
////        Set<Role> roles = new HashSet<>();
////        roles.add(roleRepository.findById(DefinedRole.MANAGER_ROLE).orElseThrow());
//
//        User newManager = User.builder()
//                .username(createManagerRequest.getUsername())
//                .password(encoder.encode(createManagerRequest.getPassword()))
//                .firstName(createManagerRequest.getFirstName())
//                .lastName(createManagerRequest.getLastName())
//                .dateOfBirth(LocalDate.of(2003, 1, 1))
//                .gender(true)
//                .email("manager@gmail.com")
//                .avatar("avatar")
//                .status(true)
//                .roles(roles)
//                .build();
//        userRepository.save(newManager);
//
//        return CreateManagerResponse.builder()
//                .id(newManager.getId())
//                .username(newManager.getUsername())
//                .password(encoder.encode(newManager.getPassword()))
//                .firstName(newManager.getFirstName())
//                .lastName(newManager.getLastName())
//                .dateOfBirth(newManager.getDateOfBirth() != null ? DateUtils.formatDate(newManager.getDateOfBirth()) : null)
//                .gender(newManager.getGender())
//                .email(newManager.getEmail())
//                .avatar(newManager.getAvatar())
//                .build();
//    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public void deleteAccount(String accountId) {
        User userInfo = userRepository.findById(accountId).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        xóa feedback giữ lại ticket
        feedbackRepository.deleteAllByUserId(userInfo.getId());

//        cập nhật user của ticket thành null
        ticketRepository.updateUserToNullByUserId(accountId);

        userRepository.delete(userInfo);
    }
}
