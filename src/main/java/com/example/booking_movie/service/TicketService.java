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

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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
    MovieRepository movieRepository;

    @PreAuthorize("hasRole('USER')")
    @Transactional
    public CreateTicketResponse create(CreateTicketRequest createTicketRequest) {
//      lấy user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//      lấy showtime
        Showtime showtime = showtimeRepository.findById(createTicketRequest.getShowtimeId())
                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));

        ZonedDateTime vnTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        Ticket ticket = Ticket.builder()
                .time(vnTime.toLocalTime())
                .date(LocalDate.now())
                .status(false)
                .user(user)
                .showtime(showtime)
                .movieId(showtime.getMovie().getId())
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

    @PreAuthorize("hasRole('ADMIN')")
    public List<GetAllTicketResponse> getAllTicket() {
        List<Ticket> tickets = ticketRepository.findAll();

        return tickets.stream()
                .map(ticket -> {
                    // Lấy TicketFood theo ticketId
                    List<TicketFood> ticketFoods = ticketFoodRepository.findAllByTicketId(ticket.getId());

                    // Nếu không có food thì để food = null
                    List<FoodDetailResponse> foodDetailResponses = ticketFoods.stream()
                            .map(ticketFood -> FoodDetailResponse.builder()
                                    .id(ticketFood.getFood().getId())
                                    .name(ticketFood.getFood().getName())
                                    .price(ticketFood.getFood().getPrice())
                                    .image(ticketFood.getFood().getImage())
                                    .quantity(ticketFood.getQuantity())
                                    .build()
                            )
                            .toList();

                    // Kiểm tra null cho showtime
                    Showtime showtime = ticket.getShowtime();
                    GetAllShowtimeResponses showtimeResponse = null;
                    if (showtime != null) {
                        showtimeResponse = GetAllShowtimeResponses.builder()
                                .id(showtime.getId())
                                .date(DateUtils.formatDate(showtime.getDate()))
                                .startTime(DateUtils.formatTime(showtime.getStartTime()))
                                .endTime(DateUtils.formatTime(showtime.getEndTime()))
                                .totalSeat(showtime.getTotalSeat())
                                .emptySeat(showtime.getEmptySeat())
                                .status(showtime.getStatus())
                                .theater(TheaterResponse.builder()
                                        .id(showtime.getRoom().getTheater().getId())
                                        .name(showtime.getRoom().getTheater().getName())
                                        .location(showtime.getRoom().getTheater().getLocation())
                                        .build())
                                .movie(MovieDetailResponse.builder()
                                        .id(showtime.getMovie().getId())
                                        .name(showtime.getMovie().getName())
                                        .premiere(DateUtils.formatDate(showtime.getMovie().getPremiere()))
                                        .language(showtime.getMovie().getLanguage())
                                        .duration(showtime.getMovie().getDuration())
                                        .content(showtime.getMovie().getContent())
                                        .rate(showtime.getMovie().getRate())
                                        .image(showtime.getMovie().getImage())
                                        .canComment(true)
                                        .genres(null)
                                        .director(null)
                                        .actors(null)
                                        .build())
                                .room(RoomResponse.builder()
                                        .id(showtime.getRoom().getId())
                                        .name(showtime.getRoom().getName())
                                        .rows(showtime.getRoom().getRowCount())
                                        .columns(showtime.getRoom().getColumnCount())
                                        .seats(null)
                                        .build())
                                .build();
                    }

                    return GetAllTicketResponse.builder()
                            .id(ticket.getId())
                            .date(DateUtils.formatDate(ticket.getDate()))
                            .time(DateUtils.formatTime(ticket.getTime()))
                            .status(ticket.getStatus())
                            .amount(ticket.getAmount())
                            .foods(foodDetailResponses.isEmpty() ? null : foodDetailResponses)
                            .showtime(showtimeResponse)
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
                .map(ticketDetails -> {
                    Seat seat = ticketDetails.getSeat();
                    SeatResponse seatResponse = null;

                    if (seat != null) {
                        seatResponse = SeatResponse.builder()
                                .id(seat.getId())
                                .locateRow(seat.getLocateRow())
                                .locateColumn(seat.getLocateColumn())
                                .price(seat.getPrice())
                                .isCouple(seat.getIsCouple())
                                .build();
                    }

                    return GetTicketDetailResponse.builder()
                            .id(ticketDetails.getId())
                            .price(ticketDetails.getPrice())
                            .ticketId(ticketDetails.getTicket().getId())
                            .seat(seatResponse)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueResponse> getTop3MoviesRevenueIn1Day() {
        LocalDate date = LocalDate.now();
        return getTop3MoviesRevenueByDateRange(date, date);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueResponse> getTop3MoviesRevenueIn7Days() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);
        return getTop3MoviesRevenueByDateRange(start, end);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RevenueResponse> getTop3MoviesRevenueInMonth() {
        YearMonth currentMonth = YearMonth.now();
        return getTop3MoviesRevenueByDateRange(currentMonth.atDay(1), currentMonth.atEndOfMonth());
    }

    private List<RevenueResponse> getTop3MoviesRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        List<TicketDetails> details = ticketDetailsRepository.findTicketDetailsBetweenDates(startDate, endDate);

        // Tính tổng doanh thu theo movieId
        Map<String, Double> revenueMap = details.stream()
                .filter(td -> td.getTicket() != null && td.getTicket().getMovieId() != null)
                .collect(Collectors.groupingBy(
                        td -> td.getTicket().getMovieId(),
                        Collectors.summingDouble(TicketDetails::getPrice)
                ));

        // Lấy top 3 movieId
        List<String> topMovieIds = revenueMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Movie> movies = movieRepository.findAllByIdIn(topMovieIds);
        Map<String, Movie> movieMap = movies.stream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));

        return topMovieIds.stream()
                .map(movieId -> {
                    Movie movie = movieMap.get(movieId);
                    if (movie == null) return null;
                    return RevenueResponse.builder()
                            .amount(revenueMap.getOrDefault(movieId, 0.0))
                            .movie(MovieResponse.builder()
                                    .id(movie.getId())
                                    .name(movie.getName())
                                    .premiere(DateUtils.formatDate(movie.getPremiere()))
                                    .language(movie.getLanguage())
                                    .duration(movie.getDuration())
                                    .content(movie.getContent())
                                    .rate(movie.getRate())
                                    .image(movie.getImage())
                                    .canComment(true)
                                    .genres(new ArrayList<>())
                                    .build())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
