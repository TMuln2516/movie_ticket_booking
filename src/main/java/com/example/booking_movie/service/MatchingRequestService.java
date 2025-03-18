package com.example.booking_movie.service;

import com.example.booking_movie.config.MatchingWebSocketHandler;
import com.example.booking_movie.dto.request.CreateMatchingRequest;
import com.example.booking_movie.dto.response.MatchingInfo;
import com.example.booking_movie.entity.MatchingRequest;
import com.example.booking_movie.entity.User;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.MatchingRequestRepository;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MatchingRequestService {
    MatchingRequestRepository matchingRequestRepository;
    UserRepository userRepository;
    MatchingWebSocketHandler matchingWebSocketHandler;

    @PreAuthorize("hasRole('USER')")
    public void create(CreateMatchingRequest createMatchingRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        if (matchingRequestRepository.existMatchingRequests(
                createMatchingRequest.getMovieName(),
                createMatchingRequest.getShowtime(),
                createMatchingRequest.getTheaterName(),
                currentUser.getId())) {
            matchingWebSocketHandler.notifyUser(currentUser.getId(), "Bạn đã có lịch hẹn. Xin vui lòng kiểm tra lại", null);
            return;
        }

//        lấy ra danh sách các request matching phù hợp với yêu cầu của người gửi
        List<MatchingRequest> matchingRequests = matchingRequestRepository.findMatchingRequests(
                createMatchingRequest.getMovieName(),
                createMatchingRequest.getShowtime(),
                createMatchingRequest.getTheaterName(),
                currentUser.getId());

//        System.out.println("Matching request: " + matchingRequests);

        if (!matchingRequests.isEmpty()) {
//            lấy giá trị đầu tiên
            MatchingRequest matchingRequest = matchingRequests.get(0);
            User matchedUser = userRepository.findById(matchingRequest.getUserId())
                    .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//            cập nhật lại trạng thái isMatched cho request vừa tìm được
            matchingRequest.setIsMatched(true);
            matchingRequestRepository.save(matchingRequest);

//            tạo thêm record cho user hiện tại vừa mới gửi request
            MatchingRequest newMatchingRequest = MatchingRequest.builder()
                    .userId(currentUser.getId())
                    .movieName(createMatchingRequest.getMovieName())
                    .showtime(createMatchingRequest.getShowtime())
                    .theaterName(createMatchingRequest.getTheaterName())
                    .isMatched(true)
                    .createAt(LocalDateTime.now())
                    .build();
            matchingRequestRepository.save(newMatchingRequest);

//            tạo json cho phản hồi từ server cho websocket
            MatchingInfo matchingInfo = MatchingInfo.builder()
                    .name(matchedUser.getFirstName() + " " + matchedUser.getLastName())
                    .dateOfBirth(DateUtils.formatDate(matchedUser.getDateOfBirth()))
                    .gender(matchedUser.getGender())
                    .build();

            // Gửi thông báo WebSocket đến cả hai người
            matchingWebSocketHandler.notifyUser(currentUser.getId(), "Ghép đôi thành công", matchingInfo);
            matchingWebSocketHandler.notifyUser(matchedUser.getId(), "Ghép đôi thành công", matchingInfo);

        } else {
//            nếu người dùng chưa gửi request thì tạo record mới
            if (!matchingRequestRepository.existMatchingRequests(
                    createMatchingRequest.getMovieName(),
                    createMatchingRequest.getShowtime(),
                    createMatchingRequest.getTheaterName(),
                    currentUser.getId())) {

                MatchingRequest newMatchingRequest = MatchingRequest.builder()
                        .userId(currentUser.getId())
                        .movieName(createMatchingRequest.getMovieName())
                        .showtime(createMatchingRequest.getShowtime())
                        .theaterName(createMatchingRequest.getTheaterName())
                        .isMatched(false)
                        .createAt(LocalDateTime.now())
                        .build();
                matchingRequestRepository.save(newMatchingRequest);
            }
//            // Gửi thông báo WebSocket khi không tìm được người phù hợp
            matchingWebSocketHandler.notifyUser(currentUser.getId(), "Hệ thống vẫn chưa tìm được người phù hợp", null);
        }
    }
}
