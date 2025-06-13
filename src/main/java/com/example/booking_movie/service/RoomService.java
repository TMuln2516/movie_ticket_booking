package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateRoomRequest;
import com.example.booking_movie.dto.request.CreateSeatRequest;
import com.example.booking_movie.dto.response.CreateRoomResponse;
import com.example.booking_movie.dto.response.RoomResponse;
import com.example.booking_movie.dto.response.SeatResponse;
import com.example.booking_movie.entity.Room;
import com.example.booking_movie.entity.Seat;
import com.example.booking_movie.entity.Theater;
import com.example.booking_movie.entity.TicketDetails;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    private final SeatRepository seatRepository;
    RoomRepository roomRepository;
    TheaterRepository theaterRepository;
    SeatService seatService;
    ScheduleSeatRepository scheduleSeatRepository;
    TicketDetailsRepository ticketDetailsRepository;

    //    create room and add to theater
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public CreateRoomResponse create(String theaterId, CreateRoomRequest createRoomRequest) {
//        find theater
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new MyException(ErrorCode.THEATER_NOT_EXISTED));

//        check exist
        if (roomRepository.existsByName(createRoomRequest.getName())) {
            throw new MyException(ErrorCode.ROOM_EXISTED);
        }

//        create room
        Room room = Room.builder()
                .name(createRoomRequest.getName())
                .rowCount(createRoomRequest.getRows())
                .columnCount(createRoomRequest.getColumns())
                .build();
        roomRepository.save(room);

//        add to theater
        theater.getRooms().add(room);

//        add theater
        room.setTheater(theater);

//        create seat
        for (int row = 1; row <= createRoomRequest.getRows(); row++) {
//            Chuyển số thành chữ cho hàng
            char rowChar = (char) ('A' + (row - 1));
//            Kiểm tra có phải là hàng dành cho ghế đôi không
            boolean isCouple = createRoomRequest.getCoupleRows() != null && createRoomRequest.getCoupleRows()
                    .contains(String.valueOf(rowChar));

            for (int column = 1; column <= createRoomRequest.getColumns(); column++) {
//                init request
                CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                        .locateColumn(column)
                        .locateRow((char) ('A' + (row - 1)))
                        .isCouple(isCouple)
                        .price(100000.0)
                        .build();
                seatService.create(room.getId(), createSeatRequest);
            }
        }

        return CreateRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .columns(room.getColumnCount())
                .rows(room.getRowCount())
                .seats(room.getSeats().stream().map(
                                seat -> SeatResponse.builder()
                                        .id(seat.getId())
                                        .locateRow(seat.getLocateRow())
                                        .locateColumn(seat.getLocateColumn())
                                        .price(seat.getPrice())
                                        .isCouple(seat.getIsCouple())
                                        .build())
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void delete(String roomId, String theaterId) {
        Room room = roomRepository.findRoomByIdAndTheaterId(roomId, theaterId)
                .orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));

        for (Seat seat : room.getSeats()) {
            // Xóa scheduleSeats
            scheduleSeatRepository.deleteAllBySeatId(seat.getId());

            // Set seat = null trong các ticketDetails liên quan
            for (TicketDetails td : seat.getTicketDetails()) {
                td.setSeat(null);
                ticketDetailsRepository.save(td);
            }

            // Sau khi đã xử lý các quan hệ, xóa seat
            seatRepository.delete(seat);
        }

        // Cuối cùng xóa room
        roomRepository.delete(room);
    }


    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public List<RoomResponse> getAll() {
        return roomRepository.findAll().stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .rows(room.getRowCount())
                        .columns(room.getColumnCount())
                        .seats(room.getSeats().stream().map(
                                        seat -> SeatResponse.builder()
                                                .id(seat.getId())
                                                .locateRow(seat.getLocateRow())
                                                .locateColumn(seat.getLocateColumn())
                                                .price(seat.getPrice())
                                                .isCouple(seat.getIsCouple())
                                                .build())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public List<RoomResponse> getAllRoomByTheater(String theaterId) {
        return roomRepository.findAllByTheaterId(theaterId).stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .rows(room.getRowCount())
                        .columns(room.getColumnCount())
                        .seats(room.getSeats().stream().map(
                                        seat -> SeatResponse.builder()
                                                .id(seat.getId())
                                                .locateRow(seat.getLocateRow())
                                                .locateColumn(seat.getLocateColumn())
                                                .price(seat.getPrice())
                                                .isCouple(seat.getIsCouple())
                                                .build())
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }
}
