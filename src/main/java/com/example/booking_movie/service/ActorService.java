package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateActorRequest;
import com.example.booking_movie.dto.request.CreateGenreRequest;
import com.example.booking_movie.dto.response.CreateActorResponse;
import com.example.booking_movie.entity.Actor;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.ActorRepository;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActorService {
    ActorRepository actorRepository;

//    create actor
    @PreAuthorize("hasRole('MANAGER')")
    public CreateActorResponse create(CreateActorRequest createActorRequest) {
        if (actorRepository.existsByNameAndGenderAndDateOfBirthAndJob(createActorRequest.getName(), createActorRequest.getGender(),
                createActorRequest.getDateOfBirth(), createActorRequest.getJob())) {
            throw new MyException(ErrorCode.ACTOR_EXISTED);
        }

        Actor newActor = Actor.builder()
                .name(createActorRequest.getName())
                .gender(createActorRequest.getGender())
                .dateOfBirth(createActorRequest.getDateOfBirth())
                .job(createActorRequest.getJob())
                .description(createActorRequest.getDescription())
                .build();
        actorRepository.save(newActor);

        return CreateActorResponse.builder()
                .id(newActor.getId())
                .name(newActor.getName())
                .gender(newActor.getGender())
                .dateOfBirth(newActor.getDateOfBirth() != null ? DateUtils.formatDateTime(newActor.getDateOfBirth()) : null)
                .job(newActor.getJob())
                .description(newActor.getDescription())
                .build();
    }
}
