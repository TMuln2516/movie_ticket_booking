package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateTicketRequest;
import com.example.booking_movie.dto.response.CreateTicketResponse;
import com.example.booking_movie.entity.*;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {
   ShowtimeRepository showtimeRepository;
   TicketRepository ticketRepository;
   ScheduleSeatRepository scheduleSeatRepository;
   TicketDetailsRepository ticketDetailsRepository;
   UserRepository userRepository;
   SeatRepository seatRepository;

   @PreAuthorize("hasRole('USER')")
   public CreateTicketResponse create(CreateTicketRequest createTicketRequest) {
      createTicketRequest.getSeatId().forEach(seatId -> {
         ScheduleSeat scheduleSeatInfo = scheduleSeatRepository.findByShowtimeIdAndSeatId(createTicketRequest.getShowtimeId(), seatId);
         if (scheduleSeatInfo != null) {
            if (scheduleSeatInfo.getStatus()) {
               throw new MyException(ErrorCode.SEAT_ALREADY_BOOK);
            }
         } else {
            throw new MyException(ErrorCode.SEAT_NOT_EXISTED);
         }
      });

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
}
