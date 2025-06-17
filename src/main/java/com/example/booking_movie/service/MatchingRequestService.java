package com.example.booking_movie.service;

import com.example.booking_movie.config.MatchingWebSocketHandler;
import com.example.booking_movie.constant.DefinedDiscountType;
import com.example.booking_movie.dto.request.CreateMatchingRequest;
import com.example.booking_movie.dto.request.CreateTicketRequest;
import com.example.booking_movie.dto.response.CheckUserSendMatchingResponse;
import com.example.booking_movie.dto.response.CreateTicketResponse;
import com.example.booking_movie.dto.response.MatchingInfo;
import com.example.booking_movie.dto.response.NotifyResponse;
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
import org.springframework.scheduling.annotation.Scheduled;
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

        int currentUserAge = DateUtils.calculateAge(currentUser.getDateOfBirth());
        log.info("Current user: {}, age: {}", currentUser.getId(), currentUserAge);

        // Tính khoảng ngày sinh phù hợp với minAge - maxAge
        int minAge = createMatchingRequest.getMinAge();
        int maxAge = createMatchingRequest.getMaxAge();

        List<MatchingRequest> matchingRequests = matchingRequestRepository.findMatchingRequests(
                createMatchingRequest.getMovieName(),
                createMatchingRequest.getShowtimeId(),
                createMatchingRequest.getTheaterName(),
                minAge,
                maxAge,
                currentUser.getGender(),
                currentUser.getId()
        );
        log.info("Found {} matching requests", matchingRequests.size());
        matchingRequests.forEach(req -> log.info("Matching request: userId={}, minAge={}, maxAge={}",
                req.getUserId(), req.getMinAge(), req.getMaxAge()));

        MatchingRequest matchedRequest = null;
        User matchedUser = null;

        for (MatchingRequest req : matchingRequests) {
            User user = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));
            int matchedUserAge = DateUtils.calculateAge(user.getDateOfBirth());
            log.info("Checking user: {}, age: {}, DOB: {}", user.getId(), matchedUserAge, user.getDateOfBirth());

            boolean matchedUserInCurrentUserRange = matchedUserAge >= minAge && matchedUserAge <= maxAge;
            boolean currentUserInMatchedUserRange = currentUserAge >= req.getMinAge() && currentUserAge <= req.getMaxAge();

            if (matchedUserInCurrentUserRange && currentUserInMatchedUserRange) {
                matchedRequest = req;
                matchedUser = user;
                log.info("Match found: userId={}, age={}", matchedUser.getId(), matchedUserAge);
                break;
            }
        }

        if (matchedUser != null) {
            matchedRequest.setIsMatched(true);
            matchingRequestRepository.save(matchedRequest);

            MatchingRequest newMatchingRequest = MatchingRequest.builder()
                    .userId(currentUser.getId())
                    .movieName(createMatchingRequest.getMovieName())
                    .showtimeId(createMatchingRequest.getShowtimeId())
                    .theaterName(createMatchingRequest.getTheaterName())
                    .minAge(minAge)
                    .maxAge(maxAge)
                    .genderMatch(createMatchingRequest.getGender())
                    .isMatched(true)
                    .createAt(LocalDateTime.now())
                    .build();
            matchingRequestRepository.save(newMatchingRequest);

            MatchingInfo matchedUserInfo = MatchingInfo.builder()
                    .name(matchedUser.getFirstName() + " " + matchedUser.getLastName())
                    .dateOfBirth(DateUtils.formatDate(matchedUser.getDateOfBirth()))
                    .gender(matchedUser.getGender())
                    .build();

            MatchingInfo currentUserInfo = MatchingInfo.builder()
                    .name(currentUser.getFirstName() + " " + currentUser.getLastName())
                    .dateOfBirth(DateUtils.formatDate(currentUser.getDateOfBirth()))
                    .gender(currentUser.getGender())
                    .build();

            NotifyResponse currentUserResponse = NotifyResponse.builder()
                    .code(200)
                    .message("Ghép đôi thành công")
                    .data(matchedUserInfo)
                    .build();

            NotifyResponse matchedUserResponse = NotifyResponse.builder()
                    .code(200)
                    .message("Ghép đôi thành công")
                    .data(currentUserInfo)
                    .build();

            matchingWebSocketHandler.notifyUser(currentUser.getId(), currentUserResponse, true);
            matchingWebSocketHandler.notifyUser(matchedUser.getId(), matchedUserResponse, true);

            List<Seat> availableCoupleSeats = scheduleSeatRepository.findAvailableCoupleSeats(createMatchingRequest.getShowtimeId());
            availableCoupleSeats.sort(Comparator.comparing(Seat::getLocateRow).thenComparing(Seat::getLocateColumn));

            List<AbstractMap.SimpleEntry<Seat, Seat>> couplePairs = new ArrayList<>();
            for (int i = 0; i < availableCoupleSeats.size() - 1; i++) {
                Seat seat1 = availableCoupleSeats.get(i);
                Seat seat2 = availableCoupleSeats.get(i + 1);
                if (seat1.getLocateRow().equals(seat2.getLocateRow()) &&
                        seat1.getLocateColumn() + 1 == seat2.getLocateColumn()) {
                    couplePairs.add(new AbstractMap.SimpleEntry<>(seat1, seat2));
                }
            }

            if (!couplePairs.isEmpty()) {
                AbstractMap.SimpleEntry<Seat, Seat> selectedPair = couplePairs.get(new Random().nextInt(couplePairs.size()));

                NotifyResponse ticketResponseCurrent = NotifyResponse.builder()
                        .code(201)
                        .message("Tạo vé thành công")
                        .data(createTicketForUser(currentUser.getId(), createMatchingRequest.getShowtimeId(), selectedPair.getKey().getId()))
                        .build();
                matchingWebSocketHandler.notifyUser(currentUser.getId(), ticketResponseCurrent, true);

                NotifyResponse ticketResponseMatched = NotifyResponse.builder()
                        .code(201)
                        .message("Tạo vé thành công")
                        .data(createTicketForUser(matchedUser.getId(), createMatchingRequest.getShowtimeId(), selectedPair.getValue().getId()))
                        .build();
                matchingWebSocketHandler.notifyUser(matchedUser.getId(), ticketResponseMatched, true);
            } else {
                NotifyResponse noSeatResponse = NotifyResponse.builder()
                        .code(400)
                        .message("Không còn ghế đôi khả dụng cho suất chiếu này")
                        .data(null)
                        .build();
                matchingWebSocketHandler.notifyUser(currentUser.getId(), noSeatResponse, true);
                matchingWebSocketHandler.notifyUser(matchedUser.getId(), noSeatResponse, true);
            }
        } else {
            boolean alreadyExist = matchingRequestRepository.existMatchingRequests(
                    createMatchingRequest.getMovieName(),
                    createMatchingRequest.getShowtimeId(),
                    createMatchingRequest.getTheaterName(),
                    minAge,
                    maxAge,
                    createMatchingRequest.getGender(),
                    currentUser.getId());

            if (!alreadyExist) {
                MatchingRequest newMatchingRequest = MatchingRequest.builder()
                        .userId(currentUser.getId())
                        .movieName(createMatchingRequest.getMovieName())
                        .showtimeId(createMatchingRequest.getShowtimeId())
                        .theaterName(createMatchingRequest.getTheaterName())
                        .minAge(minAge)
                        .maxAge(maxAge)
                        .genderMatch(createMatchingRequest.getGender())
                        .isMatched(false)
                        .createAt(LocalDateTime.now())
                        .build();
                matchingRequestRepository.save(newMatchingRequest);
                log.info("Saved new matching request for user: {}", currentUser.getId());
            }

            NotifyResponse noMatchResponse = NotifyResponse.builder()
                    .code(400)
                    .message("Hệ thống vẫn chưa tìm được người phù hợp")
                    .data(null)
                    .build();
            matchingWebSocketHandler.notifyUser(currentUser.getId(), noMatchResponse, true);
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

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void handleUnmatchedRequestsBeforeShowtime() throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesLater = now.plusMinutes(30);

        List<MatchingRequest> requests = matchingRequestRepository.findUnmatchedRequests();

        for (MatchingRequest request : requests) {
            Optional<Showtime> optionalShowtime = showtimeRepository.findById(request.getShowtimeId());
            if (optionalShowtime.isEmpty()) continue;

            Showtime showtime = optionalShowtime.get();

            // Ghép LocalDate + LocalTime => thời điểm bắt đầu
            LocalDateTime showtimeStart = LocalDateTime.of(showtime.getDate(), showtime.getStartTime());

            // Xử lý nếu suất chiếu đã bắt đầu hoặc sắp bắt đầu trong 5 phút
            boolean showtimeStarted = showtimeStart.isBefore(now);
            boolean showtimeIsStartingSoon = showtimeStart.isAfter(now) && showtimeStart.isBefore(fiveMinutesLater);

            if (showtimeStarted || showtimeIsStartingSoon) {
                // Gửi thông báo
                NotifyResponse notifyResponse = NotifyResponse.builder()
                        .code(202)
                        .message("Thông báo hủy yêu cầu ghép đôi")
                        .data("Không tìm được người phù hợp cho suất chiếu " +
                                request.getMovieName() + " tại " + request.getTheaterName() +
                                ". Vui lòng chọn suất chiếu khác.")
                        .build();

                matchingWebSocketHandler.notifyUser(request.getUserId(), notifyResponse, true);

                // Xoá yêu cầu
                matchingRequestRepository.delete(request);
            }
        }
    }
}
