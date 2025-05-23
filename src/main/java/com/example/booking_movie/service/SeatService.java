package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateSeatRequest;
import com.example.booking_movie.dto.request.GetSeatInfoRequest;
import com.example.booking_movie.dto.response.SeatResponse;
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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatService {
    SeatRepository seatRepository;
    RoomRepository roomRepository;

    public void create(String roomId, CreateSeatRequest createSeatRequest) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));

        Seat seat = Seat.builder()
                .locateColumn(createSeatRequest.getLocateColumn())
                .locateRow(createSeatRequest.getLocateRow())
                .price(createSeatRequest.getPrice())
                .isCouple(createSeatRequest.getIsCouple())
                .room(room)
                .build();

        seatRepository.save(seat);

        if (room.getSeats() == null) {
            room.setSeats(new HashSet<>());
        }
        room.getSeats().add(seat);
    }

    public List<SeatResponse> getSeatInfoByListId(GetSeatInfoRequest getSeatInfoRequest) {
        List<Seat> seatList = seatRepository.findAllById(getSeatInfoRequest.getSeatIds());

        return seatList.stream()
                .map(seat -> SeatResponse.builder()
                        .id(seat.getId())
                        .locateRow(seat.getLocateRow())
                        .locateColumn(seat.getLocateColumn())
                        .price(seat.getPrice())
                        .isCouple(seat.getIsCouple())
                        .build())
                .collect(Collectors.toList());
    }
}
