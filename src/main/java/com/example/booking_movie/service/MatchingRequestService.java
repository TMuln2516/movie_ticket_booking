package com.example.booking_movie.service;

import com.example.booking_movie.config.MatchingWebSocketHandler;
import com.example.booking_movie.dto.request.CreateCouponRequest;
import com.example.booking_movie.dto.request.CreateMatchingRequest;
import com.example.booking_movie.dto.request.UpdateCouponRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.entity.Coupon;
import com.example.booking_movie.entity.MatchingRequest;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.CouponRepository;
import com.example.booking_movie.repository.MatchingRequestRepository;
import com.example.booking_movie.repository.TicketRepository;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MatchingRequestService {
    MatchingRequestRepository matchingRequestRepository;
    UserRepository userRepository;
    MatchingWebSocketHandler matchingWebSocketHandler;

    @PreAuthorize("hasRole('USER')")
    public MatchingResponse create(CreateMatchingRequest createMatchingRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        List<MatchingRequest> matchingRequests = matchingRequestRepository.findMatchingRequests(
                createMatchingRequest.getMovieName(),
                createMatchingRequest.getShowtime(),
                createMatchingRequest.getTheaterName(),
                currentUser.getId());

        if (!matchingRequests.isEmpty()) {
            MatchingRequest matchingRequest = matchingRequests.get(0);
            User matchedUser = userRepository.findById(matchingRequest.getUserId())
                    .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

            MatchingInfo matchingInfo = MatchingInfo.builder()
                    .name(matchedUser.getFirstName() + " " + matchedUser.getLastName())
                    .dateOfBirth(DateUtils.formatDate(matchedUser.getDateOfBirth()))
                    .gender(matchedUser.getGender())
                    .build();

            // Gửi thông báo WebSocket đến cả hai người
//            matchingWebSocketHandler.notifyUserMatched(currentUser.getId(),
//                    "Bạn đã ghép đôi với " + matchingInfo);
//            matchingWebSocketHandler.notifyUserMatched(matchedUser.getId(),
//                    "Bạn đã ghép đôi với " + currentUser.getFirstName() + " " + currentUser.getLastName());

            matchingWebSocketHandler.notifyUserMatched(currentUser.getId(),
                    matchingInfo);
            matchingWebSocketHandler.notifyUserMatched(matchedUser.getId(),
                    matchingInfo);

            return MatchingResponse.builder()
                    .status("Đã tìm thấy người dùng")
                    .matchingInfo(matchingInfo)
                    .build();
        } else {
            MatchingRequest newMatchingRequest = MatchingRequest.builder()
                    .userId(currentUser.getId())
                    .movieName(createMatchingRequest.getMovieName())
                    .showtime(createMatchingRequest.getShowtime())
                    .theaterName(createMatchingRequest.getTheaterName())
                    .isMatched(false)
                    .createAt(LocalDateTime.now())
                    .build();
            matchingRequestRepository.save(newMatchingRequest);

            return MatchingResponse.builder()
                    .status("Hiện tại chưa tìm thấy người dùng nào")
                    .build();
        }
    }
}