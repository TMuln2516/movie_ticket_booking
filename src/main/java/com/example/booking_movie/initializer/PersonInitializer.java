package com.example.booking_movie.initializer;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.entity.Person;
import com.example.booking_movie.repository.JobRepository;
import com.example.booking_movie.repository.PersonRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonInitializer {
    PersonRepository personRepository;
    JobRepository jobRepository;

    public void initializePerson() {
        if (!personRepository.existsByName("Shinkai Makoto")) {
            var job = jobRepository.findByName(DefinedJob.DIRECTOR).orElseThrow();
            personRepository.save(Person.builder()
                            .name("Shinkai Makoto")
                            .dateOfBirth(LocalDate.of(1973,2,9))
                            .gender(true)
                            .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732208826/Shinkai%20Makoto/h8luvoyn7vzxhowo1of9.jpg")
                            .publicId("Shinkai Makoto/h8luvoyn7vzxhowo1of9")
                            .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Kamiki Ryūnosuke")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Kamiki Ryūnosuke")
                    .dateOfBirth(LocalDate.of(1993,5,19))
                    .gender(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732208894/Kamiki%20Ry%C5%ABnosuke/olli5q8vegujpuhlelpn.jpg")
                    .publicId("Kamiki Ryūnosuke/olli5q8vegujpuhlelpn")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Kamishiraishi Mone")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Kamishiraishi Mone")
                    .dateOfBirth(LocalDate.of(1998,1,27))
                    .gender(false)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732208937/Kamishiraishi%20Mone/rpeygbgiargi8ertrs78.jpg")
                    .publicId("Kamishiraishi Mone/rpeygbgiargi8ertrs78")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Trấn Thành")) {
            var job = jobRepository.findByName(DefinedJob.DIRECTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Trấn Thành")
                    .dateOfBirth(LocalDate.of(1987 ,2,5))
                    .gender(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732214107/Tr%E1%BA%A5n%20Th%C3%A0nh/cullb5encvykzfin98tz.png")
                    .publicId("Trấn Thành/cullb5encvykzfin98tz")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Phương Anh Đào")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Phương Anh Đào")
                    .dateOfBirth(LocalDate.of(1992 ,4,30))
                    .gender(false)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732213985/Ph%C6%B0%C6%A1ng%20Anh%20%C4%90%C3%A0o/ddrsptsuxsoe9p5t0nqr.jpg")
                    .publicId("Phương Anh Đào/ddrsptsuxsoe9p5t0nqr")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Tuấn Trần")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Tuấn Trần")
                    .dateOfBirth(LocalDate.of(1992 ,11,20))
                    .gender(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732214206/Tu%E1%BA%A5n%20Tr%E1%BA%A7n/lvoawiavwxdqwqektwko.jpg")
                    .publicId("Tuấn Trần/lvoawiavwxdqwqektwko")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Phi Tiến Sơn")) {
            var job = jobRepository.findByName(DefinedJob.DIRECTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Phi Tiến Sơn")
                    .dateOfBirth(LocalDate.of(1964 ,1,1))
                    .gender(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732214560/Phi%20Ti%E1%BA%BFn%20S%C6%A1n/ggz32ebb9umbw4tjv2k9.png")
                    .publicId("Phi Tiến Sơn/ggz32ebb9umbw4tjv2k9")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Doãn Quốc Đam")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Doãn Quốc Đam")
                    .dateOfBirth(LocalDate.of(1988 ,9,9))
                    .gender(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732214485/Do%C3%A3n%20Qu%E1%BB%91c%20%C4%90am/etsl9r2lsr2h1e8ciedd.jpg")
                    .publicId("Doãn Quốc Đam/etsl9r2lsr2h1e8ciedd")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Cao Thị Thùy Linh")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Cao Thị Thùy Linh")
                    .dateOfBirth(LocalDate.of(2003 ,9,2))
                    .gender(false)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732214736/Cao%20Th%E1%BB%8B%20Th%C3%B9y%20Linh/vf8jhtp1vvyxyscbg9zb.jpg")
                    .publicId("Cao Thị Thùy Linh/vf8jhtp1vvyxyscbg9zb")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Trần Lực")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Trần Lực")
                    .dateOfBirth(LocalDate.of(1963 ,9,15))
                    .gender(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732215090/Tr%E1%BA%A7n%20L%E1%BB%B1c/ruxhyfelpvvfrwazbb0k.png")
                    .publicId("Trần Lực/ruxhyfelpvvfrwazbb0k")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Lưu Thành Luân")) {
            var job = jobRepository.findByName(DefinedJob.DIRECTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Lưu Thành Luân")
                    .dateOfBirth(LocalDate.of(1990 ,8,2))
                    .gender(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732215475/L%C6%B0u%20Th%C3%A0nh%20Lu%C3%A2n/vwiz52iexa4o7kzzt5ac.jpg")
                    .publicId("Lưu Thành Luân/vwiz52iexa4o7kzzt5ac")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Hồng Đào")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Hồng Đào")
                    .dateOfBirth(LocalDate.of(1962 ,9,25))
                    .gender(false)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732215581/H%E1%BB%93ng%20%C4%90%C3%A0o/yngzckd9hnogauwduqn3.jpg")
                    .publicId("Hồng Đào/yngzckd9hnogauwduqn3")
                    .job(job)
                    .build());
        }

        if (!personRepository.existsByName("Nguyễn Thúc Thùy Tiên")) {
            var job = jobRepository.findByName(DefinedJob.ACTOR).orElseThrow();
            personRepository.save(Person.builder()
                    .name("Nguyễn Thúc Thùy Tiên")
                    .dateOfBirth(LocalDate.of(1998 ,8,12))
                    .gender(false)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732215788/Nguy%E1%BB%85n%20Th%C3%BAc%20Th%C3%B9y%20Ti%C3%AAn/pcclh2yfepuaxdw9uttf.jpg")
                    .publicId("Nguyễn Thúc Thùy Tiên/pcclh2yfepuaxdw9uttf")
                    .job(job)
                    .build());
        }

    }
}
