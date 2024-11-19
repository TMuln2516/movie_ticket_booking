//package com.example.booking_movie.service.Elastic;
//
//import com.example.booking_movie.entity.Elastic.ElasticTicket;
//import com.example.booking_movie.entity.Ticket;
//import com.example.booking_movie.entity.TicketDetails;
//import com.example.booking_movie.repository.Elastic.ElasticTicketRepository;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Objects;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class ElasticTicketService {
//    ElasticTicketRepository elasticTicketRepository;
//
//    public void createOrUpdate(Ticket ticket) {
//        var date = LocalDateTime.of(ticket.getDate(), ticket.getTime());
//        var movieInfo = ticket.getShowtime().getMovie();
//        var totalPrice = ticket.getTicketDetails().stream()
//                .mapToDouble(TicketDetails::getPrice)
//                .sum();
//        var theaterInfo = ticket.getShowtime().getRooms().stream()
//                .map(room -> room.getTheater() != null ? room.getTheater() : null)
//                .filter(Objects::nonNull)
//                .findFirst()
//                .orElse(null);
//
//        assert theaterInfo != null;
//        elasticTicketRepository.save(ElasticTicket.builder()
//                .id(ticket.getId())
//                .date(date)
//                .theaterId(theaterInfo.getId())
//                .showtimeId(ticket.getShowtime().getId())
//                .movieId(movieInfo.getId())
//                .amount(totalPrice)
//                .build());
//    }
//
//
//}
