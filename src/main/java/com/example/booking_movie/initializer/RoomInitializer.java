package com.example.booking_movie.initializer;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.entity.Job;
import com.example.booking_movie.entity.Room;
import com.example.booking_movie.repository.JobRepository;
import com.example.booking_movie.repository.RoomRepository;
import com.example.booking_movie.repository.TheaterRepository;
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
        }

        if (!roomRepository.existsByName("Room B")) {
            roomRepository.save(Room.builder()
                    .name("Room B")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(galaxyLinhTrungThuDuc)
                    .build());
        }

        if (!roomRepository.existsByName("Room 1")) {
            roomRepository.save(Room.builder()
                    .name("Room 1")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomThuDuc)
                    .build());
        }

        if (!roomRepository.existsByName("Room 2")) {
            roomRepository.save(Room.builder()
                    .name("Room 2")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomThuDuc)
                    .build());
        }

        if (!roomRepository.existsByName("2D")) {
            roomRepository.save(Room.builder()
                    .name("2D")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvGigamallThuDuc)
                    .build());
        }

        if (!roomRepository.existsByName("3D")) {
            roomRepository.save(Room.builder()
                    .name("3D")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvGigamallThuDuc)
                    .build());
        }

        if (!roomRepository.existsByName("Phòng 1")) {
            roomRepository.save(Room.builder()
                    .name("Phòng 1")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomDaNang)
                    .build());
        }

        if (!roomRepository.existsByName("Phòng 2")) {
            roomRepository.save(Room.builder()
                    .name("Phòng 2")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomDaNang)
                    .build());
        }

        if (!roomRepository.existsByName("Phòng A")) {
            roomRepository.save(Room.builder()
                    .name("Phòng A")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomBaTrieu)
                    .build());
        }

        if (!roomRepository.existsByName("Phòng B")) {
            roomRepository.save(Room.builder()
                    .name("Phòng B")
                    .rowCount(7)
                    .columnCount(5)
                    .theater(cgvVincomBaTrieu)
                    .build());
        }
    }
}
