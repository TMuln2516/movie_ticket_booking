package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedStatus;
import com.example.booking_movie.dto.request.CreateShowtimeRequest;
import com.example.booking_movie.dto.response.CreateShowtimeResponse;
import com.example.booking_movie.entity.Movie;
import com.example.booking_movie.entity.Room;
import com.example.booking_movie.entity.Showtime;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.MovieRepository;
import com.example.booking_movie.repository.RoomRepository;
import com.example.booking_movie.repository.ShowtimeRepository;
import com.example.booking_movie.utils.DateUtils;
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
public class ShowtimeService {
    ShowtimeRepository showtimeRepository;
    RoomRepository roomRepository;
    MovieRepository movieRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public CreateShowtimeResponse create(CreateShowtimeRequest createShowtimeRequest) {
//        kiểm tra id phòng
        var room = roomRepository.findById(createShowtimeRequest.getRoomId())
                .orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));

//        kiểm tra trùng giờ chiếu
        room.getShowtimes().forEach(showtime -> {
            if (showtime.getDate().isEqual(createShowtimeRequest.getDate())) {
                if (createShowtimeRequest.getStartTime().isBefore(showtime.getStartTime())
                        && createShowtimeRequest.getStartTime().isAfter(showtime.getEndTime())) {
                    throw new MyException(ErrorCode.SHOWTIME_EXISTED);
                }
            }
        });
//        lấy totalSeat
        var totalSeat = room.getColumns() * room.getColumns();

//        lấy Movie
        var movie = movieRepository.findById(createShowtimeRequest.getMovieId())
                .orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        tính endTime
        var endTime = createShowtimeRequest.getStartTime().plusMinutes(movie.getDuration()).plusMinutes(15);

//        builder
        Showtime newShowtime = Showtime.builder()
                .date(createShowtimeRequest.getDate())
                .startTime(createShowtimeRequest.getStartTime())
                .endTime(endTime)
                .totalSeat(totalSeat)
                .emptySeat(totalSeat)
                .status(DefinedStatus.COMING_SOON)
                .movie(movie)
                .build();
        showtimeRepository.save(newShowtime);

//        thêm showtime vào room
        room.getShowtimes().add(newShowtime);

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
}
