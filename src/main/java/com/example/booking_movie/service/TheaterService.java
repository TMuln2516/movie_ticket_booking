package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateTheaterRequest;
import com.example.booking_movie.dto.request.UpdateTheaterRequest;
import com.example.booking_movie.dto.response.CreateTheaterResponse;
import com.example.booking_movie.dto.response.TheaterResponse;
import com.example.booking_movie.dto.response.UpdateTheaterResponse;
import com.example.booking_movie.entity.*;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import com.example.booking_movie.utils.ValidUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TheaterService {
    TheaterRepository theaterRepository;
    TicketRepository ticketRepository;
    TicketDetailsRepository ticketDetailsRepository;
    RoomRepository roomRepository;
    ScheduleSeatRepository scheduleSeatRepository;

//    create theater
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public CreateTheaterResponse create(CreateTheaterRequest createTheaterRequest) {
//        check existed
        if (theaterRepository.existsByName(createTheaterRequest.getName())) {
            throw new MyException(ErrorCode.THEATER_EXISTED);
        }

//        build new theater
        Theater newTheater = Theater.builder()
                .name(createTheaterRequest.getName())
                .location(createTheaterRequest.getLocation())
                .build();
        theaterRepository.save(newTheater);

        return CreateTheaterResponse.builder()
                .id(newTheater.getId())
                .name(newTheater.getName())
                .location(newTheater.getLocation())
                .build();
    }

//    @PreAuthorize("hasAnyRole('MANAGER', 'USER', 'ADMIN')")
    public List<TheaterResponse> getAll() {
        return theaterRepository.findAll()
                .stream()
                .map(theater -> TheaterResponse.builder()
                        .id(theater.getId())
                        .name(theater.getName())
                        .location(theater.getLocation())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public UpdateTheaterResponse update(String theaterId, UpdateTheaterRequest updateTheaterRequest) {
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new MyException(ErrorCode.THEATER_NOT_EXISTED));

        ValidUtils.updateFieldIfNotEmpty(theater::setName, updateTheaterRequest.getName());
        ValidUtils.updateFieldIfNotEmpty(theater::setLocation, updateTheaterRequest.getLocation());
        theaterRepository.save(theater);

        return UpdateTheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .location(theater.getLocation())
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public void delete(String theaterId) {
        // Kiểm tra Theater
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new MyException(ErrorCode.THEATER_NOT_EXISTED));

        // Xử lý Room
        Set<Room> rooms = theater.getRooms();
        if (rooms != null && !rooms.isEmpty()) {
            for (Room room : rooms) {
                // Xử lý Showtime
                Set<Showtime> showtimes = room.getShowtimes();
                if (showtimes != null) {
                    for (Showtime showtime : showtimes) {
                        // Xử lý Ticket
                        Set<Ticket> tickets = showtime.getTickets();
                        if (tickets != null) {
                            for (Ticket ticket : tickets) {
                                // Đặt các tham chiếu liên quan thành null
                                ticket.setShowtime(null);
                                // Đặt các tham chiếu không liên quan thành null
                                ticket.setUser(null);
                                ticket.setCoupon(null);
                                // Không xóa ticketFoods, giữ nguyên @OneToMany
                                ticketRepository.save(ticket);
                            }
                        }
                        // ScheduleSeat sẽ được xóa tự động nhờ cascade = CascadeType.REMOVE
                    }
                }

                // Xử lý Seat
                Set<Seat> seats = room.getSeats();
                if (seats != null) {
                    for (Seat seat : seats) {
                        // Xử lý TicketDetails
                        Set<TicketDetails> ticketDetails = seat.getTicketDetails();
                        if (ticketDetails != null) {
                            for (TicketDetails detail : ticketDetails) {
                                // Đặt seat thành null
                                detail.setSeat(null);
                                ticketDetailsRepository.save(detail);
                            }
                        }

                        // Xóa ScheduleSeat (không có cascade từ Seat)
                        Set<ScheduleSeat> scheduleSeats = seat.getScheduleSeats();
                        if (scheduleSeats != null && !scheduleSeats.isEmpty()) {
                            scheduleSeatRepository.deleteAll(scheduleSeats);
                        }
                    }
                }

                // Xóa Room (Seat và Showtime sẽ được xóa tự động nhờ cascade = CascadeType.ALL)
                roomRepository.delete(room);
            }
        }

        // Xóa Theater
        theaterRepository.delete(theater);
    }
}
