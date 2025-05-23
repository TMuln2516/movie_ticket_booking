package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedDiscountType;
import com.example.booking_movie.dto.request.CreateTicketRequest;
import com.example.booking_movie.dto.request.SetSeatSessionRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.entity.*;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import com.example.booking_movie.utils.DateUtils;
import jakarta.servlet.http.HttpSession;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {
    ShowtimeRepository showtimeRepository;
    TicketRepository ticketRepository;
    TicketDetailsRepository ticketDetailsRepository;
    UserRepository userRepository;
    SeatRepository seatRepository;
    CouponRepository couponRepository;
    FoodRepository foodRepository;
    TicketFoodRepository ticketFoodRepository;

//    session

//    @PreAuthorize("hasRole('USER')")
//    public CreateTicketResponse create(HttpSession httpSession) {
//        var showtimeIdFromSession = (String) httpSession.getAttribute("showtimeId");
//        var seatIdsFromSession = (Set<String>) httpSession.getAttribute("seatIds");
//
//        if (seatIdsFromSession == null || showtimeIdFromSession == null) {
//            throw new MyException(ErrorCode.SESSION_EXPIRED_OR_INVALID);
//        }
//
////      lấy user
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));
//
////      lấy showtime
//        Showtime showtime = showtimeRepository.findById(showtimeIdFromSession)
//                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));
//
//        Ticket ticket = Ticket.builder()
//                .time(LocalTime.now())
//                .date(LocalDate.now())
//                .status(false)
//                .user(user)
//                .showtime(showtime)
//                .finished(false)
//                .build();
//        ticketRepository.save(ticket);
//
////      create details
//        seatIdsFromSession.forEach(seatId -> {
//            Seat seatInfo = seatRepository.findById(seatId).orElseThrow();
//

    /// /         builder
//            TicketDetails ticketDetails = TicketDetails.builder()
//                    .seat(seatInfo)
//                    .ticket(ticket)
//                    .price(seatInfo.getPrice())
//                    .build();
//            ticketDetailsRepository.save(ticketDetails);
//        });
//
//        httpSession.removeAttribute("showtimeId");
//        httpSession.removeAttribute("seatIds");
//
//        return CreateTicketResponse.builder()
//                .id(ticket.getId())
//                .date(DateUtils.formatDate(ticket.getDate()))
//                .time(DateUtils.formatTime(ticket.getTime()))
//                .status(ticket.getStatus())
//                .userId(user.getId())
//                .showtimeId(showtime.getId())
//                .build();
//    }
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public CreateTicketResponse create(CreateTicketRequest createTicketRequest) {
//      lấy user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//      lấy showtime
        Showtime showtime = showtimeRepository.findById(createTicketRequest.getShowtimeId())
                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));

        Ticket ticket = Ticket.builder()
                .time(LocalTime.now())
                .date(LocalDate.now())
                .status(false)
                .user(user)
                .showtime(showtime)
                .finished(false)
                .build();
        ticketRepository.save(ticket);

//        amout
        Double amount = 0.0;

        Double initialTicketAmount = 0.0;
        AtomicReference<Double> ticketAmount = new AtomicReference<>(initialTicketAmount);

        Double initialFoodAmount = 0.0;
        AtomicReference<Double> foodAmount = new AtomicReference<>(initialFoodAmount);

//      create details
//        tính tiền ghế
        createTicketRequest.getSeatId().forEach(seatId -> {
            Seat seatInfo = seatRepository.findById(seatId).orElseThrow();

//         builder
            TicketDetails ticketDetails = TicketDetails.builder()
                    .seat(seatInfo)
                    .ticket(ticket)
                    .price(seatInfo.getPrice())
                    .build();
            ticketDetailsRepository.save(ticketDetails);
            ticketAmount.updateAndGet(v -> v + ticketDetails.getPrice());
        });

//        áp dụng mã giảm giá
        if (createTicketRequest.getCouponId() != null) {
            var couponInfo = couponRepository.findById(createTicketRequest.getCouponId())
                    .orElseThrow(() -> new MyException(ErrorCode.COUPON_NOT_EXISTED));

            if (couponInfo.getDiscountType().equals(DefinedDiscountType.PERCENTAGE)) {
                double discountAmount = (ticketAmount.get() * couponInfo.getDiscountValue()) / 100;
                ticketAmount.updateAndGet(v -> v - discountAmount);
            } else if (couponInfo.getDiscountType().equals(DefinedDiscountType.FIXED)) {
                ticketAmount.updateAndGet(v -> v - couponInfo.getDiscountValue());
            } else {
//                Nếu giá theo giá trị mặt định thì lấy giá trị mặt định
                ticketAmount.set(0.0);
                createTicketRequest.getSeatId().forEach(seatId -> {
                    ticketAmount.updateAndGet(value -> value + couponInfo.getDiscountValue());
                });
            }


            ticket.setCoupon(couponInfo);
            ticketRepository.save(ticket);
        }

//        tính tiền đồ ăn
        createTicketRequest.getOrderRequests().forEach(order -> {
            Food foodInfo = foodRepository.findById(order.getFoodId()).orElseThrow();

//            tính tiền đồ ăn
            foodAmount.updateAndGet(value -> foodInfo.getPrice() * order.getQuantity());

//            init trong bảng ticket_food
            TicketFood ticketFood = TicketFood.builder()
                    .quantity(order.getQuantity())
                    .food(foodInfo)
                    .ticket(ticket)
                    .build();
            ticketFoodRepository.save(ticketFood);
        });

        amount = ticketAmount.get() + foodAmount.get();
        ticket.setAmount(amount);
        ticketRepository.save(ticket);

        return CreateTicketResponse.builder()
                .id(ticket.getId())
                .date(DateUtils.formatDate(ticket.getDate()))
                .time(DateUtils.formatTime(ticket.getTime()))
                .ticketAmount(ticketAmount.get())
                .foodAmount(foodAmount.get())
                .amount(ticket.getAmount())
                .status(ticket.getStatus())
                .userId(user.getId())
                .showtimeId(showtime.getId())
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    public void saveSeatsToSession(HttpSession session, SetSeatSessionRequest setSeatSessionRequest) {
        session.setAttribute("seatIds", setSeatSessionRequest.getSeatId());
        session.setAttribute("showtimeId", setSeatSessionRequest.getShowtimeId());
    }

    @Transactional
    @Scheduled(cron = "0 * * * * ?")
    public void updateFinishedStatus() {
        LocalDateTime now = LocalDateTime.now();

        // Chỉ lấy những vé đã hết suất chiếu
        List<Ticket> tickets = ticketRepository.findAllByFinishedFalseAndShowtimeBefore(now);

        tickets.forEach(ticket -> ticket.setFinished(true));

        ticketRepository.saveAll(tickets); // Một lần duy nhất
    }


    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public RevenueResponse getRevenueByDate(LocalDate date) {
//        List<Ticket> tickets = ticketRepository.findByDate(date);
//
//        double totalRevenue = tickets.stream()
//                .filter(ticket -> ticket.getStatus() != null && ticket.getStatus())
//                .flatMap(ticket -> ticket.getTicketDetails().stream())
//                .mapToDouble(TicketDetails::getPrice)
//                .sum();
//
//        var ticketDetailResponses = tickets.stream()
//                .map(ticket -> TicketDetailResponse.builder()
//                        .id(ticket.getId())
//                        .date(DateUtils.formatDate(ticket.getDate()))
//                        .time(DateUtils.formatTime(ticket.getTime()))
//                        .startTime(DateUtils.formatTime(ticket.getShowtime().getStartTime()))
//                        .endTime(DateUtils.formatTime(ticket.getShowtime().getEndTime()))
//                        .movieName(ticket.getShowtime().getMovie().getName())
//                        .totalPrice(ticket.getTicketDetails().stream()
//                                .mapToDouble(TicketDetails::getPrice)
//                                .sum())
//                        .build())
//                .collect(Collectors.toSet());
//
//        return RevenueResponse.builder()
//                .amount(totalRevenue)
//                .ticketDetails(ticketDetailResponses)
//                .build();
        return getRevenueByDateRange(date, date);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public RevenueResponse getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new MyException(ErrorCode.DATE_NULL);
        }

        if (startDate.isAfter(endDate)) {
            throw new MyException(ErrorCode.DATE_INVALID);
        }

        List<Ticket> tickets = ticketRepository.findByDateBetween(startDate, endDate);

        double totalRevenue = tickets.stream()
                .filter(ticket -> ticket.getStatus() != null && ticket.getStatus())
                .flatMap(ticket -> ticket.getTicketDetails().stream())
                .mapToDouble(TicketDetails::getPrice)
                .sum();

        var ticketDetailResponses = tickets.stream()
                .map(ticket -> TicketDetailResponse.builder()
                        .id(ticket.getId())
                        .date(DateUtils.formatDate(ticket.getDate()))
                        .time(DateUtils.formatTime(ticket.getTime()))
                        .startTime(DateUtils.formatTime(ticket.getShowtime().getStartTime()))
                        .endTime(DateUtils.formatTime(ticket.getShowtime().getEndTime()))
                        .movieName(ticket.getShowtime().getMovie().getName())
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
                .collect(Collectors.toSet());

        return RevenueResponse.builder()
                .amount(totalRevenue)
                .ticketDetails(ticketDetailResponses)
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public RevenueResponse getRevenue() {
        List<Ticket> tickets = ticketRepository.findAll();

        double totalRevenue = tickets.stream()
                .filter(ticket -> ticket.getStatus() != null && ticket.getStatus())
                .flatMap(ticket -> ticket.getTicketDetails().stream())
                .mapToDouble(TicketDetails::getPrice)
                .sum();

        var ticketDetailResponses = tickets.stream()
                .map(ticket -> TicketDetailResponse.builder()
                        .id(ticket.getId())
                        .date(DateUtils.formatDate(ticket.getDate()))
                        .time(DateUtils.formatTime(ticket.getTime()))
                        .startTime(DateUtils.formatTime(ticket.getShowtime().getStartTime()))
                        .endTime(DateUtils.formatTime(ticket.getShowtime().getEndTime()))
                        .movieName(ticket.getShowtime().getMovie().getName())
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
                .collect(Collectors.toSet());

        return RevenueResponse.builder()
                .amount(totalRevenue)
                .ticketDetails(ticketDetailResponses)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<GetAllTicketResponse> getAllTicket() {
        List<Ticket> tickets = ticketRepository.findAll();

        return tickets.stream()
                .map(ticket -> {
                    // Lấy TicketFood theo ticketId
                    TicketFood ticketFood = ticketFoodRepository.findByTicketId(ticket.getId());

                    // Nếu không có food thì để food = null
                    FoodDetailResponse food = ticketFood == null ? null :
                            FoodDetailResponse.builder()
                                    .id(ticketFood.getFood().getId())
                                    .name(ticketFood.getFood().getName())
                                    .price(ticketFood.getFood().getPrice())
                                    .image(ticketFood.getFood().getImage())
                                    .quantity(ticketFood.getQuantity())
                                    .build();

                    return GetAllTicketResponse.builder()
                            .id(ticket.getId())
                            .date(DateUtils.formatDate(ticket.getDate()))
                            .time(DateUtils.formatTime(ticket.getTime()))
                            .status(ticket.getStatus())
                            .amount(ticket.getAmount())
                            .food(food) // đã xử lý null
                            .showtime(GetAllShowtimeResponses.builder()
                                    .id(ticket.getShowtime().getId())
                                    .date(DateUtils.formatDate(ticket.getShowtime().getDate()))
                                    .startTime(DateUtils.formatTime(ticket.getShowtime().getStartTime()))
                                    .endTime(DateUtils.formatTime(ticket.getShowtime().getEndTime()))
                                    .totalSeat(ticket.getShowtime().getTotalSeat())
                                    .emptySeat(ticket.getShowtime().getEmptySeat())
                                    .status(ticket.getShowtime().getStatus())
                                    .theater(TheaterResponse.builder()
                                            .id(ticket.getShowtime().getRoom().getTheater().getId())
                                            .name(ticket.getShowtime().getRoom().getTheater().getName())
                                            .location(ticket.getShowtime().getRoom().getTheater().getLocation())
                                            .build())
                                    .movie(MovieDetailResponse.builder()
                                            .id(ticket.getShowtime().getMovie().getId())
                                            .name(ticket.getShowtime().getMovie().getName())
                                            .premiere(DateUtils.formatDate(ticket.getShowtime().getMovie().getPremiere()))
                                            .language(ticket.getShowtime().getMovie().getLanguage())
                                            .duration(ticket.getShowtime().getMovie().getDuration())
                                            .content(ticket.getShowtime().getMovie().getContent())
                                            .rate(ticket.getShowtime().getMovie().getRate())
                                            .image(ticket.getShowtime().getMovie().getImage())
                                            .canComment(true)
                                            .genres(null)
                                            .director(null)
                                            .actors(null)
                                            .build())
                                    .room(RoomResponse.builder()
                                            .id(ticket.getShowtime().getRoom().getId())
                                            .name(ticket.getShowtime().getRoom().getName())
                                            .rows(ticket.getShowtime().getRoom().getRowCount())
                                            .columns(ticket.getShowtime().getRoom().getColumnCount())
                                            .seats(null)
                                            .build())
                                    .build())
                            .user(UserResponse.builder()
                                    .id(ticket.getUser().getId())
                                    .username(ticket.getUser().getUsername())
                                    .firstName(ticket.getUser().getFirstName())
                                    .lastName(ticket.getUser().getLastName())
                                    .dateOfBirth(ticket.getUser().getDateOfBirth() != null ? DateUtils.formatDate(ticket.getUser().getDateOfBirth()) : null)
                                    .gender(ticket.getUser().getGender())
                                    .email(ticket.getUser().getEmail())
                                    .status(ticket.getUser().getStatus())
                                    .avatar(ticket.getUser().getAvatar())
                                    .roles(ticket.getUser().getRoles())
                                    .build())
                            .coupon(ticket.getCoupon() != null ?
                                    CouponResponse.builder()
                                            .id(ticket.getCoupon().getId())
                                            .code(ticket.getCoupon().getCode())
                                            .discountType(ticket.getCoupon().getDiscountType())
                                            .discountValue(ticket.getCoupon().getDiscountValue())
                                            .startDate(ticket.getCoupon().getStartDate())
                                            .endDate(ticket.getCoupon().getEndDate())
                                            .minValue(ticket.getCoupon().getMinValue())
                                            .description(ticket.getCoupon().getDescription())
                                            .status(ticket.getCoupon().getStatus())
                                            .image(ticket.getCoupon().getImage())
                                            .publicId(ticket.getCoupon().getPublicId())
                                            .build()
                                    : null)
                            .build();
                })
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<GetTicketDetailResponse> getTicketById(String ticketId) {
        List<TicketDetails> tickets = ticketDetailsRepository.findAllByTicketId(ticketId);

        return tickets.stream()
                .map(ticketDetails -> GetTicketDetailResponse.builder()
                        .id(ticketDetails.getId())
                        .price(ticketDetails.getPrice())
                        .ticketId(ticketDetails.getTicket().getId())
                        .seat(SeatResponse.builder()
                                .id(ticketDetails.getSeat().getId())
                                .locateRow(ticketDetails.getSeat().getLocateRow())
                                .locateColumn(ticketDetails.getSeat().getLocateColumn())
                                .price(ticketDetails.getSeat().getPrice())
                                .isCouple(ticketDetails.getSeat().getIsCouple())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

}
