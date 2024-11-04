package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.AddActorsRequest;
import com.example.booking_movie.dto.request.CreateMovieRequest;
import com.example.booking_movie.dto.request.DeleteActorsRequest;
import com.example.booking_movie.dto.request.UpdateMovieRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.MovieService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {
    MovieService movieService;

    //    ROLE MANAGER & USER
//    get all movie
    @GetMapping("/")
    public ApiResponse<List<MovieResponse>> getAll() {
        return ApiResponse.<List<MovieResponse>>builder()
                .message("Get All Movie Success")
                .result(movieService.getAll())
                .build();
    }

//    get detail movie
    @GetMapping("/{movieId}")
    public ApiResponse<MovieDetailResponse> getDetail(@PathVariable String movieId) {
        return ApiResponse.<MovieDetailResponse>builder()
                .message("Get Detail Movie Success")
                .result(movieService.getDetailMovie(movieId))
                .build();
    }

    //    ROLE MANAGER
//    create movie
    @PostMapping(value = "/")
    public ApiResponse<CreateMovieResponse> create(
            @RequestPart("createMovieRequest") @Valid CreateMovieRequest createMovieRequest,
            @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<CreateMovieResponse>builder()
                .message("Create Movie Success")
                .result(movieService.create(createMovieRequest, file))
                .build();
    }

//    update info of movie (not genre and actor)
    @PutMapping("/{movieId}")
    public ApiResponse<UpdateMovieResponse> update(@PathVariable String movieId, @RequestBody UpdateMovieRequest updateMovieRequest) {
        return ApiResponse.<UpdateMovieResponse>builder()
                .message("Update Movie Success")
                .result(movieService.update(movieId, updateMovieRequest))
                .build();
    }

//    delete movie
    @DeleteMapping("/{movieId}")
    public ApiResponse<Void> delete(@PathVariable String movieId) throws IOException {
        movieService.delete(movieId);
        return ApiResponse.<Void>builder()
                .message("Delete Movie Success")
                .build();
    }

//    add director to movie
    @PutMapping("/{movieId}/{directorId}")
    public ApiResponse<Void> addDirector(@PathVariable String movieId, @PathVariable String directorId) {
        movieService.addDirector(movieId, directorId);
        return ApiResponse.<Void>builder()
                .message("Add Director To Movie Success")
                .build();
    }

//    delete director of movie
    @PutMapping("/{movieId}/deleteDirector")
    public ApiResponse<Void> deleteDirector(@PathVariable String movieId) {
        movieService.deleteDirector(movieId);
        return ApiResponse.<Void>builder()
                .message("Delete Director Of Movie Success")
                .build();
    }

//    add actors to movie
    @PutMapping("/{movieId}/addActors")
    public ApiResponse<Void> addActors(@PathVariable String movieId, @RequestBody @Valid AddActorsRequest addActorsRequest) {
        movieService.addActors(movieId, addActorsRequest);
        return ApiResponse.<Void>builder()
                .message("Add Actors To Movie Success")
                .build();
    }

//    delete actors of movie
    @PutMapping("/{movieId}/deleteActors")
    public ApiResponse<Void> deleteActors(@PathVariable String movieId, @RequestBody @Valid DeleteActorsRequest deleteActorsRequest) {
        movieService.deleteActors(movieId, deleteActorsRequest);
        return ApiResponse.<Void>builder()
                .message("Delete Actors of Movie Success")
                .build();
    }
}
