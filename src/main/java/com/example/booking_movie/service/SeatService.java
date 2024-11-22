package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateSeatRequest;
import com.example.booking_movie.entity.Room;
import com.example.booking_movie.entity.Seat;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.RoomRepository;
import com.example.booking_movie.repository.SeatRepository;
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
public class SeatService {
    SeatRepository seatRepository;
    RoomRepository roomRepository;

//    create seat
//    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void create(String roomId, CreateSeatRequest createSeatRequest) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));

        seatRepository.save(Seat.builder()
                        .locateColumn(createSeatRequest.getLocateColumn())
                        .locateRow(createSeatRequest.getLocateRow())
                        .price(createSeatRequest.getPrice())
                        .room(room)
                .build());
    }
}
