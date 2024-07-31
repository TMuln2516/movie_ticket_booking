package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateTheaterRequest;
import com.example.booking_movie.dto.request.UpdateTheaterRequest;
import com.example.booking_movie.dto.response.CreateTheaterResponse;
import com.example.booking_movie.dto.response.TheaterResponse;
import com.example.booking_movie.dto.response.UpdateTheaterResponse;
import com.example.booking_movie.entity.Theater;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.TheaterRepository;
import com.example.booking_movie.utils.ValidUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TheaterService {
    TheaterRepository theaterRepository;

//    create theater
    @PreAuthorize("hasRole('MANAGER')")
    public CreateTheaterResponse create(CreateTheaterRequest createTheaterRequest) {
//        check existed
        if (theaterRepository.existsByName(createTheaterRequest.getName())) {
            throw new MyException(ErrorCode.THEATER_EXISTED);
        }

//        build new theater
        Theater newTheater = Theater.builder()
                .name(createTheaterRequest.getName())
                .location(createTheaterRequest.getLocation())
                .build();
        theaterRepository.save(newTheater);

        return CreateTheaterResponse.builder()
                .id(newTheater.getId())
                .name(newTheater.getName())
                .location(newTheater.getLocation())
                .build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public List<TheaterResponse> getAll() {
        return theaterRepository.findAll()
                .stream()
                .map(theater -> TheaterResponse.builder()
                        .id(theater.getId())
                        .name(theater.getName())
                        .location(theater.getLocation())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('MANAGER')")
    public UpdateTheaterResponse update(String theaterId, UpdateTheaterRequest updateTheaterRequest) {
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new MyException(ErrorCode.THEATER_NOT_EXISTED));

        ValidUtils.updateFieldIfNotEmpty(theater::setName, updateTheaterRequest.getName());
        ValidUtils.updateFieldIfNotEmpty(theater::setLocation, updateTheaterRequest.getLocation());
        theaterRepository.save(theater);

        return UpdateTheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .location(theater.getLocation())
                .build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    public void delete(String theaterId) {
//        check exist
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new MyException(ErrorCode.THEATER_NOT_EXISTED));

//        delete
        theaterRepository.delete(theater);
    }
}
