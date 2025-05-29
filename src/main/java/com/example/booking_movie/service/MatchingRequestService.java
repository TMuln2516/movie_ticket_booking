package com.example.booking_movie.service;

import com.example.booking_movie.config.MatchingWebSocketHandler;
import com.example.booking_movie.constant.DefinedDiscountType;
import com.example.booking_movie.dto.request.CreateMatchingRequest;
import com.example.booking_movie.dto.request.CreateTicketRequest;
import com.example.booking_movie.dto.response.CheckUserSendMatchingResponse;
import com.example.booking_movie.dto.response.CreateTicketResponse;
import com.example.booking_movie.dto.response.MatchingInfo;
import com.example.booking_movie.entity.*;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import com.example.booking_movie.utils.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MatchingRequestService {
    MatchingRequestRepository matchingRequestRepository;
    UserRepository userRepository;
    ShowtimeRepository showtimeRepository;
    ScheduleSeatRepository scheduleSeatRepository;
    TicketRepository ticketRepository;
    SeatRepository seatRepository;
    TicketDetailsRepository ticketDetailsRepository;

    MatchingWebSocketHandler matchingWebSocketHandler;

    TicketService ticketService;

    @PreAuthorize("hasRole('USER')")
    public void create(CreateMatchingRequest createMatchingRequest) throws JsonProcessingException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        if (matchingRequestRepository.existMatchingRequests(
//                createMatchingRequest.getMovieName(),
//                createMatchingRequest.getShowtimeId(),
//                createMatchingRequest.getTheaterName(),
//                createMatchingRequest.getMinAge(),
//                createMatchingRequest.getMaxAge(),
//                createMatchingRequest.getGender(),
//                currentUser.getId())) {
//            matchingWebSocketHandler.notifyUser(currentUser.getId(), "Bạn đã có lịch hẹn. Xin vui lòng kiểm tra lại", null);
//            return;
//        }

//        lấy ra danh sách các request matching phù hợp với yêu cầu của người gửi
        List<MatchingRequest> matchingRequests = matchingRequestRepository.findMatchingRequests(
                createMatchingRequest.getMovieName(),
                createMatchingRequest.getShowtimeId(),
                createMatchingRequest.getTheaterName(),
                createMatchingRequest.getMinAge(),
                createMatchingRequest.getMaxAge(),
                currentUser.getGender(),
                currentUser.getId());

        System.out.println("Matching request: " + matchingRequests);

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
                    .showtimeId(createMatchingRequest.getShowtimeId())
                    .theaterName(createMatchingRequest.getTheaterName())
                    .maxAge(createMatchingRequest.getMaxAge())
                    .minAge(createMatchingRequest.getMinAge())
                    .genderMatch(createMatchingRequest.getGender())
                    .isMatched(true)
                    .createAt(LocalDateTime.now())
                    .build();
            matchingRequestRepository.save(newMatchingRequest);

//            tạo json cho phản hồi từ server cho websocket
            MatchingInfo matchingUserInfo = MatchingInfo.builder()
                    .name(matchedUser.getFirstName() + " " + matchedUser.getLastName())
                    .dateOfBirth(DateUtils.formatDate(matchedUser.getDateOfBirth()))
                    .gender(matchedUser.getGender())
                    .build();

            MatchingInfo currentUserInfo = MatchingInfo.builder()
                    .name(currentUser.getFirstName() + " " + currentUser.getLastName())
                    .dateOfBirth(DateUtils.formatDate(currentUser.getDateOfBirth()))
                    .gender(currentUser.getGender())
                    .build();

            // Gửi thông báo WebSocket đến cả hai người
            matchingWebSocketHandler.notifyUser(currentUser.getId(), 200, "Ghép đôi thành công", matchingUserInfo, true);
            matchingWebSocketHandler.notifyUser(matchedUser.getId(), 200, "Ghép đôi thành công", currentUserInfo, true);

//            lấy danh sách ghế đôi chưa được đặt
            List<Seat> availableCoupleSeats = scheduleSeatRepository.findAvailableCoupleSeats(createMatchingRequest.getShowtimeId());
//            System.out.println("Available Couple Seat: " + availableCoupleSeats);

            // Sắp xếp danh sách ghế theo hàng (locateRow) trước, sau đó theo cột (locateColumn)
            availableCoupleSeats.sort(Comparator.comparing(Seat::getLocateRow).thenComparing(Seat::getLocateColumn));

            List<AbstractMap.SimpleEntry<Seat, Seat>> couplePairs = new ArrayList<>();

            for (int i = 0; i < availableCoupleSeats.size() - 1; i++) {
                Seat seat1 = availableCoupleSeats.get(i);
                Seat seat2 = availableCoupleSeats.get(i + 1);

                // Kiểm tra nếu seat2 kế bên seat1
                if (seat1.getLocateRow().equals(seat2.getLocateRow()) &&
                        seat1.getLocateColumn() + 1 == seat2.getLocateColumn()) {
                    couplePairs.add(new AbstractMap.SimpleEntry<>(seat1, seat2));
                }
            }

            if (!couplePairs.isEmpty()) {
                Random random = new Random();
                AbstractMap.SimpleEntry<Seat, Seat> selectedPair = couplePairs.get(random.nextInt(couplePairs.size()));

                // Đặt vé cho user đang đăng nhập
                matchingWebSocketHandler.notifyUser(currentUser.getId(), 201, "Tạo vé thành công",
                        createTicketForUser(currentUser.getId(), createMatchingRequest.getShowtimeId(),
                                selectedPair.getKey().getId()), true);

                // Đặt vé cho user được ghép đôi
                matchingWebSocketHandler.notifyUser(matchedUser.getId(), 201, "Tạo vé thành công",
                        createTicketForUser(matchedUser.getId(), createMatchingRequest.getShowtimeId(),
                                selectedPair.getValue().getId()), true);
            } else {
                // Trường hợp không có ghế đôi khả dụng
                matchingWebSocketHandler.notifyUser(currentUser.getId(), 400,
                        "Không còn ghế đôi khả dụng cho suất chiếu này", null, true);
                matchingWebSocketHandler.notifyUser(matchedUser.getId(), 400,
                        "Không còn ghế đôi khả dụng cho suất chiếu này", null, true);
            }


        } else {
//            nếu người dùng chưa gửi request thì tạo record mới
            if (!matchingRequestRepository.existMatchingRequests(
                    createMatchingRequest.getMovieName(),
                    createMatchingRequest.getShowtimeId(),
                    createMatchingRequest.getTheaterName(),
                    createMatchingRequest.getMinAge(),
                    createMatchingRequest.getMaxAge(),
                    createMatchingRequest.getGender(),
                    currentUser.getId())) {

                MatchingRequest newMatchingRequest = MatchingRequest.builder()
                        .userId(currentUser.getId())
                        .movieName(createMatchingRequest.getMovieName())
                        .showtimeId(createMatchingRequest.getShowtimeId())
                        .theaterName(createMatchingRequest.getTheaterName())
                        .maxAge(createMatchingRequest.getMaxAge())
                        .minAge(createMatchingRequest.getMinAge())
                        .genderMatch(createMatchingRequest.getGender())
                        .isMatched(false)
                        .createAt(LocalDateTime.now())
                        .build();
                matchingRequestRepository.save(newMatchingRequest);
            }
//            // Gửi thông báo WebSocket khi không tìm được người phù hợp
            matchingWebSocketHandler.notifyUser(currentUser.getId(), 400,
                    "Hệ thống vẫn chưa tìm được người phù hợp", null, true);
        }
    }

    public CreateTicketResponse createTicketForUser(String userId, String showtimeId, String seatId) {
//      lấy showtime
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));

//        user info
        User userInfo = userRepository.findById(userId).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        Ticket ticket = Ticket.builder()
                .time(LocalTime.now())
                .date(LocalDate.now())
                .status(false)
                .user(userInfo)
                .showtime(showtime)
                .finished(false)
                .build();
        ticketRepository.save(ticket);

//        amout
        Double amount = 0.0;

        Double initialTicketAmount = 0.0;
        AtomicReference<Double> ticketAmount = new AtomicReference<>(initialTicketAmount);

//      create details
//        tính tiền ghế
        Seat seatInfo = seatRepository.findById(seatId).orElseThrow();

//         builder
        TicketDetails ticketDetails = TicketDetails.builder()
                .seat(seatInfo)
                .ticket(ticket)
                .price(seatInfo.getPrice())
                .build();
        ticketDetailsRepository.save(ticketDetails);
        ticketAmount.updateAndGet(v -> v + ticketDetails.getPrice());

        amount = ticketAmount.get();
        ticket.setAmount(amount);
        ticketRepository.save(ticket);

        return CreateTicketResponse.builder()
                .id(ticket.getId())
                .date(DateUtils.formatDate(ticket.getDate()))
                .time(DateUtils.formatTime(ticket.getTime()))
                .ticketAmount(ticketAmount.get())
                .foodAmount(0.0)
                .amount(ticket.getAmount())
                .status(ticket.getStatus())
                .userId(userId)
                .showtimeId(showtime.getId())
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    public CheckUserSendMatchingResponse checkUserSendMatching() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userInfo = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        get all request of user
        List<MatchingRequest> matchingRequests = matchingRequestRepository.findAllByUserId(userInfo.getId());

        return CheckUserSendMatchingResponse.builder()
                .isSendMatchingRequest(!matchingRequests.isEmpty())
                .build();
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void delete() {
        // Lấy thông tin người dùng từ SecurityContextHolder
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var userInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        // Lấy yêu cầu matching gần nhất của người dùng theo thời gian tạo (createAt)
        Optional<MatchingRequest> latestRequest = matchingRequestRepository.findTopByUserIdOrderByCreateAtDesc(userInfo.getId());

        if (latestRequest.isEmpty()) {
            throw new MyException(ErrorCode.REQUEST_NOT_EXISTED);
        }

        // Xóa yêu cầu matching gần nhất
        matchingRequestRepository.delete(latestRequest.get());
    }
}
