package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.AddActorsRequest;
import com.example.booking_movie.dto.request.CreateMovieRequest;
import com.example.booking_movie.dto.request.DeleteActorsRequest;
import com.example.booking_movie.dto.request.UpdateMovieRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.service.ElasticsearchService;
import com.example.booking_movie.service.MovieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {
    MovieService movieService;
    ElasticsearchService elasticsearchService;

    //    ROLE MANAGER & USER
//    get all movie
    @GetMapping("/")
    public ApiResponse<List<MovieResponse>> getAll() throws JsonProcessingException {
        return ApiResponse.<List<MovieResponse>>builder()
                .message("Get All Movie Success")
                .result(movieService.getAll())
                .build();
    }

    @GetMapping("/genre")
    public ApiResponse<GetMovieByGenreResponse> getAllByGenre(@RequestParam String genreId) throws JsonProcessingException {
        return ApiResponse.<GetMovieByGenreResponse>builder()
                .message("Lấy thông tin phim theo thể loại thành công")
                .result(movieService.getAllByGenre(genreId))
                .build();
    }

    @GetMapping("/person")
    public ApiResponse<GetMovieByPersonResponse> getAllByPerson(@RequestParam String personId) throws JsonProcessingException {
        return ApiResponse.<GetMovieByPersonResponse>builder()
                .message("Lấy thông tin phim thành công")
                .result(movieService.getAllByPerson(personId))
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
    public ApiResponse<UpdateMovieResponse> update(
            @PathVariable("movieId") String movieId,
            @RequestPart("updateMovieRequest") @Valid UpdateMovieRequest updateMovieRequest,
            @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<UpdateMovieResponse>builder()
                .message("Update Movie Success")
                .result(movieService.update(movieId, updateMovieRequest, file))
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

    //    search
    @GetMapping("/search")
    public ApiResponse<List<MovieResponse>> fuzzySearch(@RequestParam String value) throws IOException {
        return ApiResponse.<List<MovieResponse>>builder()
                .result(elasticsearchService.fuzzyQuery(value))
                .build();
    }

    @GetMapping("/suggest")
    public ApiResponse<Set<String>> autoSuggest(@RequestParam String value) throws IOException {
        return ApiResponse.<Set<String>>builder()
                .result(elasticsearchService.findSuggestedByMovieNames(value))
                .build();
    }
}
