package com.example.booking_movie.service;

import com.example.booking_movie.repository.ShowtimeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeService {
    ShowtimeRepository showtimeRepository;
//    public void create();
}
