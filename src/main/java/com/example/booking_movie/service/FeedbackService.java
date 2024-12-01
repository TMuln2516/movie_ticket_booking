package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.dto.request.CreateFeedbackRequest;
import com.example.booking_movie.dto.request.UpdateFeedbackRequest;
import com.example.booking_movie.dto.response.CreateFeedbackResponse;
import com.example.booking_movie.dto.response.FeedbackResponse;
import com.example.booking_movie.dto.response.UpdateFeedbackResponse;
import com.example.booking_movie.entity.Feedback;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.FeedbackRepository;
import com.example.booking_movie.repository.MovieRepository;
import com.example.booking_movie.repository.UserRepository;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackService {
    FeedbackRepository commentRepository;
    MovieRepository movieRepository;
    UserRepository userRepository;
    FeedbackRepository feedbackRepository;

    @PreAuthorize("hasRole('USER')")
    public CreateFeedbackResponse create(CreateFeedbackRequest createCommentRequest) {
//        get movie
        var movieInfo = movieRepository.findById(createCommentRequest.getMovieId())
                .orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

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

    public List<FeedbackResponse> getAllByMovie(String movieId) {
        var movieInfo = movieRepository.findById(movieId)
                .orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

        var listFeedbacks = movieInfo.getFeedbacks();

//        check guest
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isGuest = authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName());

        if (isGuest) {
            return listFeedbacks.stream()
                    .filter(Feedback::getStatus)
                    .map(feedback -> FeedbackResponse.builder()
                            .id(feedback.getId())
                            .content(feedback.getContent())
                            .rate(feedback.getRate())
                            .date(DateUtils.formatDate(feedback.getDate()))
                            .time(DateUtils.formatTime(feedback.getTime()))
                            .byName(feedback.getUser().getFirstName() + " " + feedback.getUser().getLastName())
                            .byEmail(feedback.getUser().getEmail())
                            .movieId(movieId)
                            .status(feedback.getStatus())
                            .build())
                    .collect(Collectors.toList());
        }

        //        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var userInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        boolean isUser = userInfo.getRoles().stream()
                .anyMatch(role -> role.getName().equals(DefinedRole.USER_ROLE));

        if (isUser) {
            return listFeedbacks.stream()
                    .filter(Feedback::getStatus)
                    .map(feedback -> FeedbackResponse.builder()
                            .id(feedback.getId())
                            .content(feedback.getContent())
                            .rate(feedback.getRate())
                            .date(DateUtils.formatDate(feedback.getDate()))
                            .time(DateUtils.formatTime(feedback.getTime()))
                            .byName(feedback.getUser().getFirstName() + " " + feedback.getUser().getLastName())
                            .movieId(movieId)
                            .status(feedback.getStatus())
                            .build())
                    .collect(Collectors.toList());
        } else {
            return listFeedbacks.stream()
                    .map(feedback -> FeedbackResponse.builder()
                            .id(feedback.getId())
                            .content(feedback.getContent())
                            .rate(feedback.getRate())
                            .date(DateUtils.formatDate(feedback.getDate()))
                            .time(DateUtils.formatTime(feedback.getTime()))
                            .byName(feedback.getUser().getFirstName() + " " + feedback.getUser().getLastName())
                            .movieId(movieId)
                            .status(feedback.getStatus())
                            .build())
                    .collect(Collectors.toList());
        }
    }

    @PreAuthorize("hasRole('USER')")
    public UpdateFeedbackResponse update(String movieId, UpdateFeedbackRequest updateFeedbackRequest) {
        //        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var userInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        format date and time
//        var date = DateUtils.formatStringToLocalDate(updateFeedbackRequest.getDate(), "dd-MM-yyyy");
//        var time = DateUtils.formatStringToLocalTime(updateFeedbackRequest.getTime(), "HH:mm:ss");

        var feedbackInfo = feedbackRepository.findByMovieIdAndUserIdAndDateAndTime(movieId, userInfo.getId(),
                updateFeedbackRequest.getDate(), updateFeedbackRequest.getTime())
                .orElseThrow(() -> new MyException(ErrorCode.FEEDBACK_NOT_EXISTED));

        feedbackInfo.setContent(updateFeedbackRequest.getContent());
        feedbackInfo.setRate(updateFeedbackRequest.getRate());
        feedbackRepository.save(feedbackInfo);

        return UpdateFeedbackResponse.builder()
                .id(feedbackInfo.getId())
                .content(feedbackInfo.getContent())
                .rate(feedbackInfo.getRate())
                .date(DateUtils.formatDate(feedbackInfo.getDate()))
                .time(DateUtils.formatTime(feedbackInfo.getTime()))
                .status(feedbackInfo.getStatus())
                .movieId(feedbackInfo.getMovie().getId())
                .userId(feedbackInfo.getUser().getId())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public void delete(String feedbackId) {
        var feedbackInfo = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new MyException(ErrorCode.FEEDBACK_NOT_EXISTED));

//        delete relation user and movie
        feedbackRepository.delete(feedbackInfo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void toggleStatus(String feedbackId) {
        var feedbackInfo = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new MyException(ErrorCode.FEEDBACK_NOT_EXISTED));

        feedbackInfo.setStatus(!feedbackInfo.getStatus());
        feedbackRepository.save(feedbackInfo);

//        update rate
        var movieInfo = feedbackInfo.getMovie();
        var listFeedbacks = movieInfo.getFeedbacks().stream()
                .filter(Feedback::getStatus)
                .toList();

        var averageRate = listFeedbacks.stream()
                .mapToDouble(Feedback::getRate)
                .average()
                .orElse(9.0);
        BigDecimal roundedRate = new BigDecimal(averageRate).setScale(1, RoundingMode.HALF_UP);
        movieInfo.setRate(roundedRate.doubleValue());
        movieRepository.save(movieInfo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<FeedbackResponse> getAll() {
        return feedbackRepository.findAll().stream()
                .sorted(Comparator.comparing(Feedback::getStatus))
                .map(feedback -> FeedbackResponse.builder()
                        .id(feedback.getId())
                        .content(feedback.getContent())
                        .rate(feedback.getRate())
                        .date(DateUtils.formatDate(feedback.getDate()))
                        .time(DateUtils.formatTime(feedback.getTime()))
                        .byName(feedback.getUser().getFirstName() + " " + feedback.getUser().getLastName())
                        .byEmail(feedback.getUser().getEmail())
                        .movieId(feedback.getMovie().getId())
                        .status(feedback.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
