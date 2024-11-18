package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateFeedbackRequest;
import com.example.booking_movie.dto.response.CreateFeedbackResponse;
import com.example.booking_movie.entity.Feedback;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.FeedbackRepository;
import com.example.booking_movie.repository.MovieRepository;
import com.example.booking_movie.repository.TicketRepository;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackService {
    FeedbackRepository commentRepository;
    TicketRepository ticketRepository;
    UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    public CreateFeedbackResponse create(CreateFeedbackRequest createCommentRequest) {
        var ticketInfo = ticketRepository.findById(createCommentRequest.getTicketId())
                .orElseThrow(() -> new MyException(ErrorCode.TICKET_NOT_EXISTED));

//        get movie
        var movieInfo = ticketInfo.getShowtime().getMovie();

        //        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var userInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        var localDate = LocalDate.now();
        var localTime = LocalTime.now();

        Feedback feedback = Feedback.builder()
                .content(createCommentRequest.getContent())
                .rate(createCommentRequest.getRate())
                .date(localDate)
                .time(localTime)
                .status(false)
                .movie(movieInfo)
                .user(userInfo)
                .build();
        commentRepository.save(feedback);

        movieInfo.getFeedbacks().add(feedback);
        userInfo.getFeedbacks().add(feedback);

        return CreateFeedbackResponse.builder()
                .id(feedback.getId())
                .content(feedback.getContent())
                .rate(feedback.getRate())
                .date(DateUtils.formatDate(feedback.getDate()))
                .time(DateUtils.formatTime(feedback.getTime()))
                .status(feedback.getStatus())
                .movieId(feedback.getMovie().getId())
                .userId(feedback.getUser().getId())
                .build();
    }
}
