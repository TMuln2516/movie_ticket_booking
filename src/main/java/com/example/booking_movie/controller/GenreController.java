package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreateGenreRequest;
import com.example.booking_movie.dto.request.UpdateGenreRequest;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreateGenreResponse;
import com.example.booking_movie.dto.response.GenreResponse;
import com.example.booking_movie.dto.response.UpdateGenreResponse;
import com.example.booking_movie.service.GenreService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreController {
    GenreService genreService;

//    ROLE USER AND MANAGER
//    get all genre
    @GetMapping("/")
    public ApiResponse<List<GenreResponse>> getAll() {
        return ApiResponse.<List<GenreResponse>>builder()
                .message("Get All Genre Success")
                .result(genreService.getAll())
                .build();
    }

//    ROLE MANAGER
//    create genre
    @PostMapping("/")
    public ApiResponse<CreateGenreResponse> create(@RequestBody @Valid CreateGenreRequest createGenreRequest) {
        return ApiResponse.<CreateGenreResponse>builder()
                .message("Create Genre Success")
                .result(genreService.create(createGenreRequest))
                .build();
    }

//    update genre
    @PutMapping("/{genreId}")
    public ApiResponse<UpdateGenreResponse> update(@PathVariable String genreId, @RequestBody @Valid UpdateGenreRequest updateGenreRequest) {
        return ApiResponse.<UpdateGenreResponse>builder()
                .message("Update Genre Success")
                .result(genreService.update(genreId, updateGenreRequest))
                .build();
    }

//    delete genre
    @DeleteMapping("/{genreId}")
    public ApiResponse<Void> delete(@PathVariable String genreId) {
        genreService.delete(genreId);
        return ApiResponse.<Void>builder()
                .message("Delete Genre Success")
                .build();
    }
}
