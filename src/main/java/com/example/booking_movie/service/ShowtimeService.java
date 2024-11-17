package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedStatus;
import com.example.booking_movie.dto.request.CreateShowtimeRequest;
import com.example.booking_movie.dto.request.GetAllShowTimeRequest;
import com.example.booking_movie.dto.request.UpdateShowtimeRequest;
import com.example.booking_movie.dto.response.*;
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
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeService {
    ShowtimeRepository showtimeRepository;
    RoomRepository roomRepository;
    MovieRepository movieRepository;
    TheaterRepository theaterRepository;
    SeatRepository seatRepository;
    ScheduleSeatRepository scheduleSeatRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public CreateShowtimeResponse create(CreateShowtimeRequest createShowtimeRequest) {
//        kiểm tra id phòng
        var room = roomRepository.findById(createShowtimeRequest.getRoomId())
                .orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));

        //        lấy Movie
        var movie = movieRepository.findById(createShowtimeRequest.getMovieId())
                .orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        kiểm tra trùng giờ chiếu
        room.getShowtimes().forEach(showtime -> {
//            kiểm tra ngày
            if (showtime.getDate().isEqual(createShowtimeRequest.getDate())) {
                LocalTime existingStart = showtime.getStartTime();
                LocalTime existingEnd = showtime.getEndTime();
                LocalTime newStart = createShowtimeRequest.getStartTime();
                LocalTime newEnd = newStart.plusMinutes(movie.getDuration()).plusMinutes(15);

                // Kiểm tra thời gian
                if ((newStart.isBefore(existingEnd) && newStart.isAfter(existingStart)) ||
                        (newEnd.isAfter(existingStart) && newEnd.isBefore(existingEnd)) ||
                        (newStart.equals(existingStart) || newEnd.equals(existingEnd)) ||
                        (newStart.isBefore(existingStart) && newEnd.isAfter(existingEnd))) {
                    throw new MyException(ErrorCode.SHOWTIME_EXISTED);
                }
            }
        });
//        lấy totalSeat
        var totalSeat = room.getRowCount() * room.getColumnCount();

//        tính endTime
        var endTime = createShowtimeRequest.getStartTime().plusMinutes(movie.getDuration()).plusMinutes(15);

//        tạo Set<Room>
        Set<Room> rooms = new HashSet<>();
        rooms.add(room);

//        builder
        Showtime newShowtime = Showtime.builder()
                .date(createShowtimeRequest.getDate())
                .startTime(createShowtimeRequest.getStartTime())
                .endTime(endTime)
                .totalSeat(totalSeat)
                .emptySeat(totalSeat)
                .status(DefinedStatus.COMING_SOON)
                .movie(movie)
                .rooms(rooms)
                .build();
        showtimeRepository.save(newShowtime);

//        thêm showtime vào room
        room.getShowtimes().add(newShowtime);

//        Lấy danh sách các ghế map vào Schedule Seat
        seatRepository.findAllByRoomId(room.getId()).forEach(seat -> {
            ScheduleSeat scheduleSeat = ScheduleSeat.builder()
                    .seat(seat)
                    .showtime(newShowtime)
                    .status(false)
                    .build();
            scheduleSeatRepository.save(scheduleSeat);
        });

        return CreateShowtimeResponse.builder()
                .id(newShowtime.getId())
                .date(DateUtils.formatDate(newShowtime.getDate()))
                .startTime(DateUtils.formatTime(newShowtime.getStartTime()))
                .endTime(DateUtils.formatTime(newShowtime.getEndTime()))
                .totalSeat(newShowtime.getTotalSeat())
                .emptySeat(newShowtime.getEmptySeat())
                .status(newShowtime.getStatus())
                .build();
    }

    //   get all suất chiếu theo phim
//    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public List<GetAllShowtimeResponse> getAll(GetAllShowTimeRequest getAllShowTimeRequest) {
//        lấy tất cả rạp theo location
        var listTheater = theaterRepository.findByLocation(getAllShowTimeRequest.getLocation());

// Lấy tất cả các phòng của rạp
        List<Room> listRoom = new ArrayList<>();
        listTheater.forEach(theater -> {
         listRoom.addAll(theater.getRooms());
        });

//        log.info("List Room: {}", listRoom);

        List<Showtime> listShowTime = new ArrayList<>();

        // Duyệt qua từng phòng và thêm các showtime của mỗi phòng vào listShowTime
        listRoom.forEach(room -> {
            listShowTime.addAll(room.getShowtimes());
        });
//
//        listShowTime.forEach(showtime -> {
//            if (showtime.getMovie() != null) {
//                log.info("Showtime Movie ID: {}", showtime.getMovie().getId());
//            } else {
//                log.info("Showtime has no movie associated.");
//            }
//        });

        return listShowTime.stream()
                // Lọc theo ngày
                .filter(showtime -> showtime.getDate().isEqual(getAllShowTimeRequest.getDate())) // Lọc theo date
                .filter(showtime -> showtime.getMovie().getId().equals(getAllShowTimeRequest.getMovieId())) // Lọc theo movieId
                .flatMap(showtime -> showtime.getRooms().stream() // Lấy danh sách các phòng cho mỗi showtime
                        .map(room -> GetAllShowtimeResponse.builder()
                                .id(showtime.getId())
                                .date(DateUtils.formatDate(showtime.getDate()))
                                .startTime(DateUtils.formatTime(showtime.getStartTime()))
                                .endTime(DateUtils.formatTime(showtime.getEndTime()))
                                .totalSeat(showtime.getTotalSeat())
                                .emptySeat(showtime.getEmptySeat())
                                .status(showtime.getStatus())
                                .movieId(showtime.getMovie().getId())
                                .theater(TheaterResponse.builder()
                                        .id(room.getTheater().getId())
                                        .name(room.getTheater().getName())
                                        .location(room.getTheater().getLocation())
                                        .build())
                                .build()))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public UpdateShowtimeResponse update(String showtimeId, UpdateShowtimeRequest updateShowtimeRequest) {
        var showtimeInfo = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));

        showtimeInfo.setStartTime(updateShowtimeRequest.getStartTime());
//        update endTime
        showtimeInfo.setEndTime(updateShowtimeRequest.getStartTime().plusMinutes(showtimeInfo.getMovie().getDuration()).plusMinutes(15));
        showtimeRepository.save(showtimeInfo);

        return UpdateShowtimeResponse.builder()
                .id(showtimeInfo.getId())
                .date(DateUtils.formatDate(showtimeInfo.getDate()))
                .startTime(DateUtils.formatTime(showtimeInfo.getStartTime()))
                .endTime(DateUtils.formatTime(showtimeInfo.getEndTime()))
                .totalSeat(showtimeInfo.getTotalSeat())
                .emptySeat(showtimeInfo.getEmptySeat())
                .status(showtimeInfo.getStatus())
                .build();
    }
}
