package com.example.booking_movie.initializer;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.dto.request.CreateSeatRequest;
import com.example.booking_movie.entity.Job;
import com.example.booking_movie.entity.Room;
import com.example.booking_movie.entity.Seat;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.JobRepository;
import com.example.booking_movie.repository.RoomRepository;
import com.example.booking_movie.repository.SeatRepository;
import com.example.booking_movie.repository.TheaterRepository;
import com.example.booking_movie.service.SeatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomInitializer {
    RoomRepository roomRepository;
    TheaterRepository theaterRepository;
    SeatService seatService;

    public void initializeRooms() {
        var galaxyLinhTrungThuDuc = theaterRepository.findByName("Galaxy Linh Trung Thủ Đức").orElseThrow();
        var cgvVincomThuDuc = theaterRepository.findByName("CGV Vincom Thủ Đức").orElseThrow();
        var cgvGigamallThuDuc = theaterRepository.findByName("CGV Giga Mall Thủ Đức").orElseThrow();
        var cgvVincomDaNang = theaterRepository.findByName("CGV Vincom Đà Nẵng").orElseThrow();
        var cgvVincomBaTrieu = theaterRepository.findByName("CGV Vincom Bà Triệu").orElseThrow();

        if (!roomRepository.existsByName("Room A")) {
            roomRepository.save(Room.builder()
                    .name("Room A")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(galaxyLinhTrungThuDuc)
                    .build());

            Room room = roomRepository.findByName("Room A").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("Room B")) {
            roomRepository.save(Room.builder()
                    .name("Room B")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(galaxyLinhTrungThuDuc)
                    .build());

            Room room = roomRepository.findByName("Room B").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("Room 1")) {
            roomRepository.save(Room.builder()
                    .name("Room 1")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomThuDuc)
                    .build());

            Room room = roomRepository.findByName("Room 1").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("Room 2")) {
            roomRepository.save(Room.builder()
                    .name("Room 2")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomThuDuc)
                    .build());

            Room room = roomRepository.findByName("Room 2").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("2D")) {
            roomRepository.save(Room.builder()
                    .name("2D")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvGigamallThuDuc)
                    .build());

            Room room = roomRepository.findByName("2D").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("3D")) {
            roomRepository.save(Room.builder()
                    .name("3D")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvGigamallThuDuc)
                    .build());

            Room room = roomRepository.findByName("3D").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("Phòng 1")) {
            roomRepository.save(Room.builder()
                    .name("Phòng 1")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomDaNang)
                    .build());

            Room room = roomRepository.findByName("Phòng 1").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("Phòng 2")) {
            roomRepository.save(Room.builder()
                    .name("Phòng 2")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomDaNang)
                    .build());

            Room room = roomRepository.findByName("Phòng 2").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("Phòng A")) {
            roomRepository.save(Room.builder()
                    .name("Phòng A")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomBaTrieu)
                    .build());

            Room room = roomRepository.findByName("Phòng A").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }

        if (!roomRepository.existsByName("Phòng B")) {
            roomRepository.save(Room.builder()
                    .name("Phòng B")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomBaTrieu)
                    .build());

            Room room = roomRepository.findByName("Phòng B").orElseThrow(() -> new MyException(ErrorCode.ROOM_NOT_EXISTED));
            for (int row = 1; row <= room.getRowCount(); row++) {
                for (int column = 1; column <= room.getColumnCount(); column++) {
//                init request
                    CreateSeatRequest createSeatRequest = CreateSeatRequest.builder()
                            .locateColumn(column)
                            .locateRow((char) ('A' + (row - 1)))
                            .price(100000.0)
                            .build();
                    seatService.create(room.getId(), createSeatRequest);
                }
            }
        }
    }
}
