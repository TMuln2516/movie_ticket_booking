package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    boolean existsByName(String name);

    Optional<Room> findRoomByIdAndTheaterId(String roomId, String theaterId);

    List<Room> findAllByTheaterId(String theaterId);
}
