package com.example.booking_movie.repository;

import com.example.booking_movie.entity.ScheduleSeat;
import com.example.booking_movie.entity.Ticket;
import com.example.booking_movie.entity.TicketFood;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketFoodRepository extends JpaRepository<TicketFood, String> {
    @Modifying
    @Query("DELETE FROM TicketFood tf WHERE tf.food.id = :foodId")
    void deleteAllByFoodId(@Param("foodId") String foodId);

    TicketFood findByTicketId(String ticketId);
}
