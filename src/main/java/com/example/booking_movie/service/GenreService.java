package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateGenreRequest;
import com.example.booking_movie.dto.request.UpdateGenreRequest;
import com.example.booking_movie.dto.response.CreateGenreResponse;
import com.example.booking_movie.dto.response.GenreResponse;
import com.example.booking_movie.dto.response.MovieResponse;
import com.example.booking_movie.dto.response.UpdateGenreResponse;
import com.example.booking_movie.entity.Genre;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.GenreRepository;
import com.example.booking_movie.repository.MovieRepository;
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
public class GenreService {
    GenreRepository genreRepository;
    MovieRepository movieRepository;

//    create genre
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public CreateGenreResponse create(CreateGenreRequest createGenreRequest) {
        //    check existed
        if (genreRepository.existsByName(createGenreRequest.getName())) {
            throw new MyException(ErrorCode.GENRE_EXISTED);
        }

//        new genre
        Genre newGenre = Genre.builder()
                .name(createGenreRequest.getName())
                .build();
        genreRepository.save(newGenre);

        return CreateGenreResponse.builder()
                .id(newGenre.getId())
                .name(newGenre.getName())
                .build();
    }

//    get all genre
    @PreAuthorize("hasAnyRole('MANAGER', 'USER', 'ADMIN')")
    public List<GenreResponse> getAll() {
        return genreRepository.findAll()
                .stream()
                .map(genre -> GenreResponse.builder()
                        .id(genre.getId())
                        .name(genre.getName())
                        .build())
                .collect(Collectors.toList());
    }

//    update genre
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public UpdateGenreResponse update(String genreId, UpdateGenreRequest updateGenreRequest) {
//        get genre
        Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new MyException(ErrorCode.GENRE_NOT_EXISTED));

//        update
        genre.setName(updateGenreRequest.getName());
        genreRepository.save(genre);

        return UpdateGenreResponse.builder()
                .id(genre.getId())
                .name(updateGenreRequest.getName())
                .build();
    }

//    delete genre
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void delete(String genreId) {
//        check exist
        Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new MyException(ErrorCode.GENRE_NOT_EXISTED));

//        delete genre in movie
        genre.getMovies().forEach(movie -> {
            movie.getGenres().remove(genre);
            movieRepository.save(movie);
        });

//        delete genre
        genreRepository.delete(genre);
    }
}
