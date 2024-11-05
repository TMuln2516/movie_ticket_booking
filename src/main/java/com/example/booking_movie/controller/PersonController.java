package com.example.booking_movie.controller;

import com.example.booking_movie.dto.request.CreatePersonRequest;
import com.example.booking_movie.dto.request.UpdatePersonRequest;
import com.example.booking_movie.dto.response.PersonResponse;
import com.example.booking_movie.dto.response.ApiResponse;
import com.example.booking_movie.dto.response.CreatePersonResponse;
import com.example.booking_movie.dto.response.UpdatePersonResponse;
import com.example.booking_movie.service.PersonService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonController {
    PersonService personService;

    //    ROLE USER AND MANAGER
//    get all
    @GetMapping("/")
    public ApiResponse<List<PersonResponse>> getAll(@RequestParam(required = false) String jobName) {
        return ApiResponse.<List<PersonResponse>>builder()
                .message("Get All Person Success")
                .result(personService.getAll(jobName))
                .build();
    }

    //    ROLE MANAGER
//    create actor
    @PostMapping("/actor")
    public ApiResponse<CreatePersonResponse> createActor(@RequestBody @Valid CreatePersonRequest createPersonRequest) {
        return ApiResponse.<CreatePersonResponse>builder()
                .message("Create Actor Success")
                .result(personService.createActor(createPersonRequest))
                .build();
    }
//    create director
    @PostMapping("/director")
    public ApiResponse<CreatePersonResponse> createDirector(@RequestBody @Valid CreatePersonRequest createPersonRequest) {
        return ApiResponse.<CreatePersonResponse>builder()
                .message("Create Director Success")
                .result(personService.createDirector(createPersonRequest))
                .build();
    }

    //    update actor
    @PutMapping("/{personId}")
    public ApiResponse<UpdatePersonResponse> update(@PathVariable String personId, @RequestBody @Valid UpdatePersonRequest updatePersonRequest) {
        return ApiResponse.<UpdatePersonResponse>builder()
                .message("Update Person Success")
                .result(personService.update(personId, updatePersonRequest))
                .build();
    }

    //    delete actor
    @DeleteMapping("/{personId}")
    public ApiResponse<Void> delete(@PathVariable String personId) {
        personService.delete(personId);
        return ApiResponse.<Void>builder()
                .message("Delete Person Success")
                .build();
    }
}