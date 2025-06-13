package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedJob;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
    TicketRepository ticketRepository;

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


//        builder
        Showtime newShowtime = Showtime.builder()
                .date(createShowtimeRequest.getDate())
                .startTime(createShowtimeRequest.getStartTime())
                .endTime(endTime)
                .totalSeat(totalSeat)
                .emptySeat(totalSeat)
                .status(DefinedStatus.COMING_SOON)
                .movie(movie)
                .room(room)
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
                .theater(TheaterResponse.builder()
                        .id(room.getTheater().getId())
                        .name(room.getTheater().getName())
                        .location(room.getTheater().getLocation())
                        .build())
                .movie(MovieDetailResponse.builder()
                        .id(movie.getId())
                        .name(movie.getName())
                        .premiere(DateUtils.formatDate(movie.getPremiere()))
                        .language(movie.getLanguage())
                        .duration(movie.getDuration())
                        .content(movie.getContent())
                        .rate(movie.getRate())
                        .image(movie.getImage())
                        .canComment(true)
                        .genres(movie.getGenres().stream()
                                .map(genre -> GenreResponse.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toSet()))
                        .director(null)
                        .actors(null)
                        .build())
                .room(RoomResponse.builder()
                        .id(newShowtime.getRoom().getId())
                        .name(newShowtime.getRoom().getName())
                        .rows(newShowtime.getRoom().getRowCount())
                        .columns(newShowtime.getRoom().getColumnCount())
                        .seats(newShowtime.getRoom().getSeats().stream().map(
                                        seat -> SeatResponse.builder()
                                                .id(seat.getId())
                                                .locateRow(seat.getLocateRow())
                                                .locateColumn(seat.getLocateColumn())
                                                .price(seat.getPrice())
                                                .build())
                                .collect(Collectors.toSet()))
                        .build())
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
                .map(showtime -> GetAllShowtimeResponse.builder()
                        .id(showtime.getId())
                        .date(DateUtils.formatDate(showtime.getDate()))
                        .startTime(DateUtils.formatTime(showtime.getStartTime()))
                        .endTime(DateUtils.formatTime(showtime.getEndTime()))
                        .totalSeat(showtime.getTotalSeat())
                        .emptySeat(showtime.getEmptySeat())
                        .status(showtime.getStatus())
                        .movieId(showtime.getMovie().getId())
                        .theater(TheaterResponse.builder()
                                .id(showtime.getRoom().getTheater().getId())
                                .name(showtime.getRoom().getTheater().getName())
                                .location(showtime.getRoom().getTheater().getLocation())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public List<GetAllShowtimeResponses> getAllShowtimes() {
        List<Showtime> showtimes = showtimeRepository.findAll();

        return showtimes.stream()
                .map(showtime -> {
                    Movie movie = showtime.getMovie();
                    Theater theater = showtime.getRoom().getTheater();

                    return GetAllShowtimeResponses.builder()
                            .id(showtime.getId())
                            .date(DateUtils.formatDate(showtime.getDate()))
                            .startTime(DateUtils.formatTime(showtime.getStartTime()))
                            .endTime(DateUtils.formatTime(showtime.getEndTime()))
                            .totalSeat(showtime.getTotalSeat())
                            .emptySeat(showtime.getEmptySeat())
                            .status(showtime.getStatus())
                            .theater(TheaterResponse.builder()
                                    .id(theater.getId())
                                    .name(theater.getName())
                                    .location(theater.getLocation())
                                    .build())
                            .movie(MovieDetailResponse.builder()
                                    .id(movie.getId())
                                    .name(movie.getName())
                                    .premiere(DateUtils.formatDate(movie.getPremiere()))
                                    .language(movie.getLanguage())
                                    .duration(movie.getDuration())
                                    .content(movie.getContent())
                                    .rate(movie.getRate())
                                    .image(movie.getImage())
                                    .canComment(true)
                                    .genres(null)
                                    .director(null)
                                    .actors(null)
                                    .build())
                            .room(RoomResponse.builder()
                                    .id(showtime.getRoom().getId())
                                    .name(showtime.getRoom().getName())
                                    .rows(showtime.getRoom().getRowCount())
                                    .columns(showtime.getRoom().getColumnCount())
                                    .seats(null)
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public GetOneShowtimeResponses getOneShowtime(String showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));

        Movie movie = showtime.getMovie();
        Theater theater = showtime.getRoom().getTheater();

        Set<Person> persons = showtime.getMovie().getPersons();
        Person director = null;
        Set<Person> actors = new HashSet<>();
        for (Person person : persons) {
            if (DefinedJob.DIRECTOR.equalsIgnoreCase(person.getJob().getName())) {
                director = person;
            } else {
                actors.add(person);
            }
        }

        assert director != null;
        return GetOneShowtimeResponses.builder()
                .id(showtime.getId())
                .date(DateUtils.formatDate(showtime.getDate()))
                .startTime(DateUtils.formatTime(showtime.getStartTime()))
                .endTime(DateUtils.formatTime(showtime.getEndTime()))
                .totalSeat(showtime.getTotalSeat())
                .emptySeat(showtime.getEmptySeat())
                .status(showtime.getStatus())
                .theater(TheaterResponse.builder()
                        .id(theater.getId())
                        .name(theater.getName())
                        .location(theater.getLocation())
                        .build())
                .movie(MovieDetailResponse.builder()
                        .id(movie.getId())
                        .name(movie.getName())
                        .premiere(DateUtils.formatDate(movie.getPremiere()))
                        .language(movie.getLanguage())
                        .duration(movie.getDuration())
                        .content(movie.getContent())
                        .rate(movie.getRate())
                        .image(movie.getImage())
                        .canComment(true)
                        .genres(movie.getGenres().stream().map(
                                genre -> GenreResponse.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build()
                        ).collect(Collectors.toSet()))
                        .director(PersonResponse.builder()
                                .id(director.getId())
                                .name(director.getName())
                                .gender(director.getGender())
                                .dateOfBirth(DateUtils.formatDate(director.getDateOfBirth()))
                                .image(director.getImage())
                                .job(JobResponse.builder()
                                        .id(director.getJob().getId())
                                        .name(director.getJob().getName())
                                        .build())
                                .build())
                        .actors(actors.stream().map(
                                        person -> PersonResponse.builder()
                                                .id(person.getId())
                                                .name(person.getName())
                                                .gender(person.getGender())
                                                .dateOfBirth(DateUtils.formatDate(person.getDateOfBirth()))
                                                .image(person.getImage())
                                                .job(JobResponse.builder()
                                                        .id(person.getJob().getId())
                                                        .name(person.getJob().getName())
                                                        .build())
                                                .build())
                                .collect(Collectors.toSet()))
                        .build())
                .room(RoomResponse.builder()
                        .id(showtime.getRoom().getId())
                        .name(showtime.getRoom().getName())
                        .rows(showtime.getRoom().getRowCount())
                        .columns(showtime.getRoom().getColumnCount())
                        .seats(showtime.getRoom().getSeats().stream().map(
                                seat -> SeatResponse.builder()
                                        .id(seat.getId())
                                        .locateColumn(seat.getLocateColumn())
                                        .locateRow(seat.getLocateRow())
                                        .price(seat.getPrice())
                                        .isCouple(seat.getIsCouple())
                                        .build()
                        ).collect(Collectors.toSet()))
                        .build())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public UpdateShowtimeResponse update(String showtimeId, UpdateShowtimeRequest updateShowtimeRequest) {
        var showtimeInfo = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));

        var roomInfo = roomRepository.findById(updateShowtimeRequest.getRoomId())
                .orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));

        showtimeInfo.setStartTime(updateShowtimeRequest.getStartTime());
//        update endTime
        showtimeInfo.setEndTime(updateShowtimeRequest.getStartTime().plusMinutes(showtimeInfo.getMovie().getDuration()).plusMinutes(15));

//        update date
        showtimeInfo.setDate(updateShowtimeRequest.getDate());

//        update room
        showtimeInfo.setRoom(roomInfo);
        showtimeRepository.save(showtimeInfo);

        return UpdateShowtimeResponse.builder()
                .id(showtimeInfo.getId())
                .date(DateUtils.formatDate(showtimeInfo.getDate()))
                .startTime(DateUtils.formatTime(showtimeInfo.getStartTime()))
                .endTime(DateUtils.formatTime(showtimeInfo.getEndTime()))
                .totalSeat(showtimeInfo.getTotalSeat())
                .emptySeat(showtimeInfo.getEmptySeat())
                .status(showtimeInfo.getStatus())
                .theater(TheaterResponse.builder()
                        .id(showtimeInfo.getRoom().getTheater().getId())
                        .name(showtimeInfo.getRoom().getTheater().getName())
                        .location(showtimeInfo.getRoom().getTheater().getLocation())
                        .build())
                .movie(MovieDetailResponse.builder()
                        .id(showtimeInfo.getMovie().getId())
                        .name(showtimeInfo.getMovie().getName())
                        .premiere(DateUtils.formatDate(showtimeInfo.getMovie().getPremiere()))
                        .language(showtimeInfo.getMovie().getLanguage())
                        .duration(showtimeInfo.getMovie().getDuration())
                        .content(showtimeInfo.getMovie().getContent())
                        .rate(showtimeInfo.getMovie().getRate())
                        .image(showtimeInfo.getMovie().getImage())
                        .canComment(true)
                        .genres(null)
                        .director(null)
                        .actors(null)
                        .build())
                .room(RoomResponse.builder()
                        .id(showtimeInfo.getRoom().getId())
                        .name(showtimeInfo.getRoom().getName())
                        .rows(showtimeInfo.getRoom().getRowCount())
                        .columns(showtimeInfo.getRoom().getColumnCount())
                        .seats(showtimeInfo.getRoom().getSeats().stream().map(
                                        seat -> SeatResponse.builder()
                                                .id(seat.getId())
                                                .locateRow(seat.getLocateRow())
                                                .locateColumn(seat.getLocateColumn())
                                                .price(seat.getPrice())
                                                .build())
                                .collect(Collectors.toSet()))
                        .build())
                .build();
    }

    public List<GetAllShowtimeResponse> getAllShowtimeByMovie(String movieId) {
        return showtimeRepository.findAllByMovieId(movieId).stream()
                .map(showtime -> GetAllShowtimeResponse.builder()
                        .id(showtime.getId())
                        .date(DateUtils.formatDate(showtime.getDate()))
                        .startTime(DateUtils.formatTime(showtime.getStartTime()))
                        .endTime(DateUtils.formatTime(showtime.getEndTime()))
                        .totalSeat(showtime.getTotalSeat())
                        .emptySeat(showtime.getEmptySeat())
                        .status(showtime.getStatus())
                        .movieId(showtime.getMovie().getId())
                        .status(showtime.getStatus())
                        .theater(TheaterResponse.builder()
                                .id(showtime.getRoom().getTheater().getId())
                                .name(showtime.getRoom().getTheater().getName())
                                .location(showtime.getRoom().getTheater().getLocation())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CheckSeatInShowtimeResponse checkSeatInShowtime(String showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));

        List<ScheduleSeat> listSeatOfShowtime = scheduleSeatRepository.findAllByShowtimeId(showtimeId);

        return CheckSeatInShowtimeResponse.builder()
                .id(showtime.getId())
                .date(DateUtils.formatDate(showtime.getDate()))
                .startTime(DateUtils.formatTime(showtime.getStartTime()))
                .endTime(DateUtils.formatTime(showtime.getEndTime()))
                .totalSeat(showtime.getTotalSeat())
                .emptySeat(showtime.getEmptySeat())
                .bookedSeat(showtime.getTotalSeat() - showtime.getEmptySeat())
                .seats(showtime.getRoom().getSeats().stream().map(
                        seat -> CheckSeatResponse.builder()
                                .id(seat.getId())
                                .locateRow(seat.getLocateRow())
                                .locateColumn(seat.getLocateColumn())
                                .price(seat.getPrice())
                                .isCouple(seat.getIsCouple())
                                .isBooked(listSeatOfShowtime.stream()
                                        .filter(scheduleSeat -> scheduleSeat.getSeat().getId().equals(seat.getId()))
                                        .anyMatch(ScheduleSeat::getStatus))
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public void deleteShowtime(String showtimeId) {
        var showtimeInfo = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new MyException(ErrorCode.SHOWTIME_NOT_EXISTED));

        ticketRepository.setShowtimeToNull(showtimeId);

//        xóa dữ liệu trong bảng many to many
        showtimeInfo.getScheduleSeats().clear();

        showtimeRepository.deleteById(showtimeId);
    }
}
