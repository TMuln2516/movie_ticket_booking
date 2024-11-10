package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateRoomRequest;
import com.example.booking_movie.dto.request.CreateSeatRequest;
import com.example.booking_movie.dto.response.CreateRoomResponse;
import com.example.booking_movie.entity.Room;
import com.example.booking_movie.entity.Theater;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.RoomRepository;
import com.example.booking_movie.repository.SeatRepository;
import com.example.booking_movie.repository.TheaterRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    private final SeatRepository seatRepository;
    RoomRepository roomRepository;
    TheaterRepository theaterRepository;
    SeatService seatService;

//    create room and add to theater
    @PreAuthorize("hasRole('MANAGER')")
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
            for (int column = 1; column <= createRoomRequest.getColumns(); column++) {
//                init request
                CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                        .locateColumn(column)
                        .locateRow((char) ('A' + (row - 1)))
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
                .build();   
    }

    @PreAuthorize("hasRole('MANAGER')")
    public void delete(String roomId, String theaterId) {
        Room room = roomRepository.findRoomByIdAndTheaterId(roomId, theaterId).orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));

        roomRepository.delete(room);
    }
}
