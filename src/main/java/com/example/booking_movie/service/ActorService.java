package com.example.booking_movie.service;

import com.example.booking_movie.dto.request.CreateActorRequest;
import com.example.booking_movie.dto.request.CreateGenreRequest;
import com.example.booking_movie.dto.request.UpdateActorRequest;
import com.example.booking_movie.dto.response.ActorResponse;
import com.example.booking_movie.dto.response.CreateActorResponse;
import com.example.booking_movie.dto.response.UpdateActorResponse;
import com.example.booking_movie.entity.Actor;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.ActorRepository;
import com.example.booking_movie.utils.DateUtils;
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

    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    public List<ActorResponse> getAll() {
        return actorRepository.findAll()
                .stream()
                .map(actor -> ActorResponse.builder()
                        .id(actor.getId())
                        .name(actor.getName())
                        .gender(actor.getGender())
                        .dateOfBirth(actor.getDateOfBirth() != null ? DateUtils.formatDateTime(actor.getDateOfBirth()) : null)
                        .job(actor.getJob())
                        .description(actor.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('MANAGER')")
    public UpdateActorResponse update(String id, UpdateActorRequest updateActorRequest) {
//        get actor
        Actor actor = actorRepository.findById(id).orElseThrow(() -> new MyException(ErrorCode.ACTOR_NOT_EXISTED));

//        check null and update
        ValidUtils.updateFieldIfNotEmpty(actor::setName, updateActorRequest.getName());
        ValidUtils.updateFieldIfNotEmpty(actor::setGender, updateActorRequest.getGender());
        ValidUtils.updateFieldIfNotEmpty(actor::setDateOfBirth, updateActorRequest.getDateOfBirth());
        ValidUtils.updateFieldIfNotEmpty(actor::setJob, updateActorRequest.getJob());
        ValidUtils.updateFieldIfNotEmpty(actor::setDescription, updateActorRequest.getDescription());
        actorRepository.save(actor);

        return UpdateActorResponse.builder()
                .id(actor.getId())
                .name(actor.getName())
                .gender(actor.getGender())
                .dateOfBirth(actor.getDateOfBirth() != null ? DateUtils.formatDateTime(actor.getDateOfBirth()) : null)
                .job(actor.getJob())
                .description(actor.getDescription())
                .build();
    }

    public void delete(String id) {
//        check exist
        if (actorRepository.findById(id).isEmpty()) {
            throw new MyException(ErrorCode.ACTOR_NOT_EXISTED);
        }

        actorRepository.deleteById(id);
    }
}
