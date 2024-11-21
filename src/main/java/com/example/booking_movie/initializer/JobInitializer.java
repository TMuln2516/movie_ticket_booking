package com.example.booking_movie.initializer;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.entity.Job;
import com.example.booking_movie.repository.JobRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobInitializer {
    JobRepository jobRepository;

    public void initializeJobs() {
        if (!jobRepository.existsByName(DefinedJob.ACTOR)) {
            jobRepository.save(Job.builder()
                    .name(DefinedJob.ACTOR)
                    .build());
        }

        if (!jobRepository.existsByName(DefinedJob.DIRECTOR)) {
            jobRepository.save(Job.builder()
                    .name(DefinedJob.DIRECTOR)
                    .build());
        }
    }
}
