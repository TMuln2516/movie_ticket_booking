package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.dto.request.CreatePersonRequest;
import com.example.booking_movie.dto.request.UpdatePersonRequest;
import com.example.booking_movie.dto.response.PersonResponse;
import com.example.booking_movie.dto.response.CreatePersonResponse;
import com.example.booking_movie.dto.response.JobResponse;
import com.example.booking_movie.dto.response.UpdatePersonResponse;
import com.example.booking_movie.entity.Job;
import com.example.booking_movie.entity.Person;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.JobRepository;
import com.example.booking_movie.repository.MovieRepository;
import com.example.booking_movie.repository.PersonRepository;
import com.example.booking_movie.utils.DateUtils;
import com.example.booking_movie.utils.ValidUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonService {
    PersonRepository personRepository;
    JobRepository jobRepository;
    MovieRepository movieRepository;

    // Create person
    @PreAuthorize("hasRole('MANAGER')")
    public CreatePersonResponse createActor(CreatePersonRequest createPersonRequest) {
        return createPerson(createPersonRequest, DefinedJob.ACTOR);
    }

    // Create director
    @PreAuthorize("hasRole('MANAGER')")
    public CreatePersonResponse createDirector(CreatePersonRequest createPersonRequest) {
        return createPerson(createPersonRequest, DefinedJob.DIRECTOR);
    }

    //    create person
    private CreatePersonResponse createPerson(CreatePersonRequest createPersonRequest, String definedJob) {
        if (personRepository.existsByNameAndGenderAndDateOfBirth(createPersonRequest.getName(), createPersonRequest.getGender(),
                createPersonRequest.getDateOfBirth())) {
            throw new MyException(ErrorCode.PERSON_EXISTED);
        }

        Job job = jobRepository.findByName(definedJob).orElseThrow();

        Person newPerson = Person.builder()
                .name(createPersonRequest.getName())
                .gender(createPersonRequest.getGender())
                .dateOfBirth(createPersonRequest.getDateOfBirth())
                .image(createPersonRequest.getImage())
                .job(job)
                .build();

        job.getPersons().add(newPerson);

        personRepository.save(newPerson);

        return CreatePersonResponse.builder()
                .id(newPerson.getId())
                .name(newPerson.getName())
                .gender(newPerson.getGender())
                .dateOfBirth(newPerson.getDateOfBirth() != null ? DateUtils.formatDateTime(newPerson.getDateOfBirth()) : null)
                .image(newPerson.getImage())
                .build();
    }

    //    get all
    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    public List<PersonResponse> getAll(String jobName) {
        List<Person> personResponses;

        if (jobName == null || jobName.isEmpty()) {
            personResponses = personRepository.findAll();
        } else {
            personResponses = personRepository.findByJobNameIgnoreCase(jobName);
        }
        return personResponses
                .stream()
                .map(person -> PersonResponse.builder()
                        .id(person.getId())
                        .name(person.getName())
                        .gender(person.getGender())
                        .dateOfBirth(person.getDateOfBirth() != null ? DateUtils.formatDateTime(person.getDateOfBirth()) : null)
                        .image(person.getImage())
                        .job(JobResponse.builder()
                                .id(person.getJob().getId())
                                .name(person.getJob().getName())
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('MANAGER')")
    public UpdatePersonResponse update(String personId, UpdatePersonRequest updatePersonRequest) {
//        get actor
        Person person = personRepository.findById(personId).orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED));

//        check null and update
        ValidUtils.updateFieldIfNotEmpty(person::setName, updatePersonRequest.getName());
        ValidUtils.updateFieldIfNotEmpty(person::setGender, updatePersonRequest.getGender());
        ValidUtils.updateFieldIfNotEmpty(person::setDateOfBirth, updatePersonRequest.getDateOfBirth());
        ValidUtils.updateFieldIfNotEmpty(person::setImage, updatePersonRequest.getImage());
        personRepository.save(person);

        return UpdatePersonResponse.builder()
                .id(person.getId())
                .name(person.getName())
                .gender(person.getGender())
                .dateOfBirth(person.getDateOfBirth() != null ? DateUtils.formatDateTime(person.getDateOfBirth()) : null)
                .description(person.getImage())
                .build();
    }

    public void delete(String personId) {
//        check exist
        Person person = personRepository.findById(personId).orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

//        delete person in movie
        person.getMovies().forEach(movie -> {
            movie.getPersons().remove(person);
            movieRepository.save(movie);
        });

        personRepository.delete(person);
    }
}
