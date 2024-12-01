package com.example.booking_movie.repository;

import com.example.booking_movie.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findAllByFinished(Boolean status);
    List<Ticket> findByDate(LocalDate date);
    List<Ticket> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Ticket> findAllByUserId(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.user = NULL WHERE t.user.id = :userId")
    void updateUserToNullByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.showtime = NULL WHERE t.showtime.id = :showtimeId")
    void setShowtimeToNull(String showtimeId);

    List<Ticket> findAllByUserIdAndFinishedTrue(String userId);
}
