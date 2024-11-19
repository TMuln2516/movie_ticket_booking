package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateTicketRequest;
import com.example.booking_movie.dto.request.SetSeatSessionRequest;
import com.example.booking_movie.dto.response.CreateTicketResponse;
import com.example.booking_movie.dto.response.RevenueResponse;
import com.example.booking_movie.dto.response.TicketDetailResponse;
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
////         builder
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

//      create details
        createTicketRequest.getSeatId().forEach(seatId -> {
            Seat seatInfo = seatRepository.findById(seatId).orElseThrow();

//         builder
            TicketDetails ticketDetails = TicketDetails.builder()
                    .seat(seatInfo)
                    .ticket(ticket)
                    .price(seatInfo.getPrice())
                    .build();
            ticketDetailsRepository.save(ticketDetails);
        });


        return CreateTicketResponse.builder()
                .id(ticket.getId())
                .date(DateUtils.formatDate(ticket.getDate()))
                .time(DateUtils.formatTime(ticket.getTime()))
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
        var tickets = ticketRepository.findAllByFinished(false);

        LocalDateTime now = LocalDateTime.now();

        tickets.forEach(ticket -> {
            LocalDateTime showtimeEnd = LocalDateTime.of(ticket.getShowtime().getDate(), ticket.getShowtime().getEndTime());

            if (now.isAfter(showtimeEnd)) {
                ticket.setFinished(true);
                ticketRepository.save(ticket);
            }
        });
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
                        .build())
                .collect(Collectors.toSet());

        return RevenueResponse.builder()
                .amount(totalRevenue)
                .ticketDetails(ticketDetailResponses)
                .build();
    }

}
