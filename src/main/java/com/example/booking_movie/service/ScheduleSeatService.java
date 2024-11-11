package com.example.booking_movie.service;

import com.example.booking_movie.dto.response.ScheduleSeatResponse;
import com.example.booking_movie.repository.ScheduleSeatRepository;
import com.example.booking_movie.repository.SeatRepository;
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
public class ScheduleSeatService {
    ScheduleSeatRepository scheduleSeatRepository;
    SeatRepository seatRepository;

    @PreAuthorize("hasAnyRole('MANAGER', 'USER', 'ADMIN')")
    public List<ScheduleSeatResponse> getAllSeatByShowtimeId(String showtimeId) {
        return scheduleSeatRepository.findAllByShowtimeId(showtimeId).stream()
                .map(seat -> ScheduleSeatResponse.builder()
                        .id(seat.getSeat().getId())
                        .locateRow(seat.getSeat().getLocateRow())
                        .locateColumn(seat.getSeat().getLocateColumn())
                        .price(seat.getSeat().getPrice())
                        .status(seat.getStatus())
                        .showtimeId(seat.getShowtime().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
