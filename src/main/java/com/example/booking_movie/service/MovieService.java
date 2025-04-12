package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.dto.request.AddActorsRequest;
import com.example.booking_movie.dto.request.CreateMovieRequest;
import com.example.booking_movie.dto.request.DeleteActorsRequest;
import com.example.booking_movie.dto.request.UpdateMovieRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.entity.*;
import com.example.booking_movie.entity.Elastic.ElasticMovie;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import com.example.booking_movie.repository.Elastic.ElasticMovieRepository;
import com.example.booking_movie.service.Elastic.ElasticMovieService;
import com.example.booking_movie.utils.DateUtils;
import com.example.booking_movie.utils.ValidUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {
    ElasticMovieRepository elasticMovieRepository;
    MovieRepository movieRepository;
    GenreRepository genreRepository;
    PersonRepository personRepository;
    UserRepository userRepository;
    TicketRepository ticketRepository;
    FeedbackRepository feedbackRepository;
    ShowtimeRepository showtimeRepository;

    ElasticMovieService elasticMovieService;
    ImageService imageService;

    RedisTemplate<String, Object> redisTemplate;
    ObjectMapper objectMapper;

    //    create movie
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public CreateMovieResponse create(CreateMovieRequest createMovieRequest, MultipartFile file) throws IOException {
//        check file null
        if (file.isEmpty()) {
            throw new MyException(ErrorCode.MOVIE_IMAGE_NOT_NULL);
        }

        // check exist
        if (movieRepository.existsByName(createMovieRequest.getName())) {
            throw new MyException(ErrorCode.MOVIE_EXISTED);
        }

        // get genre
        Set<Genre> genres = createMovieRequest.getGenresId()
                .stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new MyException(ErrorCode.GENRE_NOT_EXISTED)))
                .collect(Collectors.toSet());

//        get director
        Person director = personRepository.findById(createMovieRequest.getDirectorId())
                .orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED));

        // get actor
        Set<Person> persons = createMovieRequest.getActorsId()
                .stream()
                .map(personId -> personRepository.findById(personId)
                        .orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED)))
                .collect(Collectors.toSet());
        persons.add(director);

//        upload image
        var imageResponse = imageService.uploadImage(file, "MovieImage");

//        set feedback
        Set<Feedback> feedbacks = new HashSet<>();

        // init
        Movie newMovie = Movie.builder()
                .name(createMovieRequest.getName())
                .premiere(createMovieRequest.getPremiere())
                .language(createMovieRequest.getLanguage())
                .duration(createMovieRequest.getDuration())
                .content(createMovieRequest.getContent())
                .rate(createMovieRequest.getRate())
                .createAt(LocalDate.now())
                .image(imageResponse.getImageUrl())
                .publicId(imageResponse.getPublicId())
                .genres(genres)
                .persons(persons)
                .feedbacks(feedbacks)
                .build();
        movieRepository.save(newMovie);

//        elastic
        elasticMovieService.createOrUpdate(ElasticMovie.builder()
                .id(newMovie.getId())
                .name(newMovie.getName())
                .premiere(DateUtils.formatDateToEpochMillis(newMovie.getPremiere()))
                .language(newMovie.getLanguage())
                .duration(newMovie.getDuration())
                .content(newMovie.getContent())
                .rate(newMovie.getRate())
                .createAt(DateUtils.formatDateToEpochMillis(newMovie.getCreateAt()))
                .image(newMovie.getImage())
                .publicId(newMovie.getPublicId())
                .genreIds(newMovie.getGenres().stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet()))
                .personIds(newMovie.getPersons().stream()
                        .map(Person::getId)
                        .collect(Collectors.toSet()))
                .build());

        return CreateMovieResponse.builder()
                .id(newMovie.getId())
                .name(newMovie.getName())
                .premiere(DateUtils.formatDate(newMovie.getPremiere()))
                .language(newMovie.getLanguage())
                .duration(newMovie.getDuration())
                .content(newMovie.getContent())
                .rate(newMovie.getRate())
                .image(newMovie.getImage())
                .build();
    }

    //        get all movie
    public List<MovieResponse> getAll() throws JsonProcessingException {
        List<MovieCacheResponse> movies;
        // Kiểm tra cache trước
        String jsonMovies = (String) redisTemplate.opsForValue().get("ListMovie");

        if (jsonMovies != null) {
            movies = objectMapper.readValue(jsonMovies,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, MovieCacheResponse.class));
        } else {
            // Nếu không có trong cache, lấy từ database sau đó lưu vào cache
            movies = movieRepository.findAll().stream()
                    .map(movie -> MovieCacheResponse.builder()
                            .id(movie.getId())
                            .name(movie.getName())
                            .premiere(DateUtils.formatDate(movie.getPremiere()))
                            .language(movie.getLanguage())
                            .duration(movie.getDuration())
                            .content(movie.getContent())
                            .rate(movie.getRate())
                            .image(movie.getImage())
                            .publicId(movie.getPublicId())
                            .createAt(movie.getCreateAt())
                            .genreIds(movie.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                            .personIds(movie.getPersons().stream().map(Person::getId).collect(Collectors.toSet()))
                            .showtimeIds(movie.getShowtimes().stream().map(Showtime::getId).collect(Collectors.toSet()))
                            .feedbackIds(movie.getFeedbacks().stream().map(Feedback::getId).collect(Collectors.toSet()))
                            .build())
                    .collect(Collectors.toList());
            if (!movies.isEmpty()) {
                redisTemplate.opsForValue().set("ListMovie", objectMapper.writeValueAsString(movies), 1, TimeUnit.HOURS);
            }
        }

        List<MovieResponse> movieResponses;
        // Kiểm tra guest
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isGuest = authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName());

        // Lấy tất cả genreIds trước
        Set<String> allGenreIds = movies.stream()
                .flatMap(movie -> movie.getGenreIds().stream())
                .collect(Collectors.toSet());
        Map<String, Genre> genreMap = genreRepository.findAllById(allGenreIds).stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre));

        // Nếu authenticated, lấy tickets trước
        Map<String, Boolean> canCommentMap;
        if (!isGuest) {
            var userInfo = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));
            var tickets = ticketRepository.findAllByUserIdAndFinishedTrue(userInfo.getId());
            canCommentMap = tickets.stream()
                    .collect(Collectors.toMap(
                            ticket -> ticket.getShowtime().getMovie().getId(),
                            ticket -> true,
                            (v1, v2) -> v1));
        } else {
            canCommentMap = new HashMap<>();
        }

        // Xử lý movieResponses
        movieResponses = movies.stream()
                .map(movie -> {
                    List<GenreResponse> genres = movie.getGenreIds().stream()
                            .map(id -> GenreResponse.builder()
                                    .id(id)
                                    .name(genreMap.get(id).getName())
                                    .build())
                            .collect(Collectors.toList());

                    boolean canComment = !isGuest && canCommentMap.containsKey(movie.getId());

                    return MovieResponse.builder()
                            .id(movie.getId())
                            .name(movie.getName())
                            .content(movie.getContent())
                            .premiere(movie.getPremiere())
                            .language(movie.getLanguage())
                            .duration(movie.getDuration())
                            .rate(movie.getRate())
                            .image(movie.getImage())
                            .genres(genres)
                            .canComment(canComment)
                            .build();
                })
                .collect(Collectors.toList());

        return movieResponses;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'USER', 'ADMIN')")
    public MovieDetailResponse getDetailMovie(String id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

        AtomicReference<Person> director = new AtomicReference<>(null);
        Set<Person> actors = new HashSet<>();

//        check actor or director
        movie.getPersons().forEach(person -> {
            if (person.getJob().getName().equals(DefinedJob.DIRECTOR)) {
                director.set(person);
            } else {
                actors.add(person);
            }
        });

//        init object optional with value of atomic reference with .ofNullable
        Optional<Person> optionalDirector = Optional.ofNullable(director.get());
        PersonResponse directorResponse = optionalDirector.map(d -> PersonResponse.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .gender(d.getGender())
                        .dateOfBirth(DateUtils.formatDate(d.getDateOfBirth()))
                        .image(d.getImage())
                        .job(JobResponse.builder()
                                .id(d.getJob().getId())
                                .name(d.getJob().getName())
                                .build())
                        .build())
                .orElse(null);

        //        get user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var userInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_EXISTED));

        boolean canComment = ticketRepository.findAllByUserIdAndFinishedTrue(userInfo.getId()).stream()
                .anyMatch(ticket -> ticket.getShowtime().getMovie().getId().equals(movie.getId()));

        return MovieDetailResponse.builder()
                .id(movie.getId())
                .name(movie.getName())
                .premiere(DateUtils.formatDate(movie.getPremiere()))
                .language(movie.getLanguage())
                .duration(movie.getDuration())
                .content(movie.getContent())
                .rate(movie.getRate())
                .image(movie.getImage())
                .canComment(canComment)
                .genres(movie.getGenres()
                        .stream()
                        .map(genre -> GenreResponse.builder()
                                .id(genre.getId())
                                .name(genre.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .director(directorResponse)
                .actors(actors.stream().map(person -> PersonResponse.builder()
                                .id(person.getId())
                                .name(person.getName())
                                .gender(person.getGender())
                                .dateOfBirth(DateUtils.formatDate(person.getDateOfBirth()))
                                .image(person.getImage())
                                .job(JobResponse.builder()
                                        .id(person.getJob().getId())
                                        .name(person.getJob().getName())
                                        .build())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }

//        return MovieDetailResponse.builder()
//                .id(movie.getId())
//                .name(movie.getName())
//                .premiere(DateUtils.formatDate(movie.getPremiere()))
//                .language(movie.getLanguage())
//                .duration(movie.getDuration())
//                .content(movie.getContent())
//                .rate(movie.getRate())
//                .image(movie.getImage())
//                .genres(movie.getGenres()
//                        .stream()
//                        .map(genre -> GenreResponse.builder()
//                                .id(genre.getId())
//                                .name(genre.getName())
//                                .build())
//                        .collect(Collectors.toSet()))
//                .director(directorResponse)
//                .actors(actors.stream().map(person -> PersonResponse.builder()
//                                .id(person.getId())
//                                .name(person.getName())
//                                .gender(person.getGender())
//                                .dateOfBirth(DateUtils.formatDate(person.getDateOfBirth()))
//                                .image(person.getImage())
//                                .job(JobResponse.builder()
//                                        .id(person.getJob().getId())
//                                        .name(person.getJob().getName())
//                                        .build())
//                                .build())
//                        .collect(Collectors.toSet()))
//                .build();

    //    update movie
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public UpdateMovieResponse update(String movieId, UpdateMovieRequest updateMovieRequest, MultipartFile file) throws IOException {
//        get movie
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));
//        get movie in elastic
        ElasticMovie elasticMovie = elasticMovieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        set image
        if (!file.isEmpty()) {
//            delete image
            imageService.deleteImage(movie.getPublicId());
//        upload image
            var imageResponse = imageService.uploadImage(file, "MovieImage");
            movie.setImage(imageResponse.getImageUrl());
            movie.setPublicId(imageResponse.getPublicId());
            movieRepository.save(movie);
        }

//        check null field
        ValidUtils.updateFieldIfNotEmpty(movie::setName, updateMovieRequest.getName());
        ValidUtils.updateFieldIfNotEmpty(movie::setPremiere, updateMovieRequest.getPremiere());
        ValidUtils.updateFieldIfNotEmpty(movie::setLanguage, updateMovieRequest.getLanguage());
        ValidUtils.updateFieldIfNotEmpty(movie::setDuration, updateMovieRequest.getDuration());
        ValidUtils.updateFieldIfNotEmpty(movie::setContent, updateMovieRequest.getContent());
        ValidUtils.updateFieldIfNotEmpty(movie::setRate, updateMovieRequest.getRate());
        movieRepository.save(movie);

//        elastic
        elasticMovie.setName(movie.getName());
        elasticMovie.setPremiere(DateUtils.formatDateToEpochMillis(movie.getPremiere()));
        elasticMovie.setLanguage(movie.getLanguage());
        elasticMovie.setDuration(movie.getDuration());
        elasticMovie.setContent(movie.getContent());
        elasticMovie.setRate(movie.getRate());
        elasticMovieService.createOrUpdate(elasticMovie);

        return UpdateMovieResponse.builder()
                .id(movie.getId())
                .name(movie.getName())
                .premiere(DateUtils.formatDate(movie.getPremiere()))
                .language(movie.getLanguage())
                .duration(movie.getDuration())
                .content(movie.getContent())
                .rate(movie.getRate())
                .image(movie.getImage())
                .genres(movie.getGenres())
                .actors(movie.getPersons())
                .build();
    }

    //    delete movie
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public void delete(String movieId) throws IOException {
//        check exist
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        delete image
        imageService.deleteImage(movie.getPublicId());

//        delete genre in movie
        movie.getGenres().clear();

//        delete person in movie
        movie.getPersons().clear();

//        delete feedback
        feedbackRepository.deleteByMovieId(movieId);

//        set movie_id in showtime = null
        Set<Showtime> showtimes = movie.getShowtimes();
        for (Showtime showtime : showtimes) {
            showtime.setMovie(null);
            showtimeRepository.save(showtime);
        }

//        delete image in cloudinary
        imageService.deleteImage(movie.getPublicId());

//        delete
        movieRepository.delete(movie);

//        elastic
        elasticMovieRepository.deleteById(movieId);
    }

    //    delete director
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public void deleteDirector(String movieId) {
//        check exist
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));
//        get movie in elastic
        ElasticMovie elasticMovie = elasticMovieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        use Iterator
        Iterator<Person> personIterator = movie.getPersons().iterator();
        while (personIterator.hasNext()) {  // person++
            Person person = personIterator.next();
            if (person.getJob().getName().equals(DefinedJob.DIRECTOR)) {
//                [movie.getPersons()].remove() -> delete person
                personIterator.remove();
//                delete movie
                person.getMovies().remove(movie);

//                delete in elastic
                elasticMovie.getPersonIds().remove(person.getId());
            }
        }

//        save
        movieRepository.save(movie);

//        save elastic
        elasticMovieRepository.save(elasticMovie);
    }

    //    add director of movie (one movie -> one director)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Transactional
    public void addDirector(String movieId, String directorId) {
//        find person
        Person person = personRepository.findById(directorId).orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED));

        //        get movie in elastic
        ElasticMovie elasticMovie = elasticMovieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        check job
        if (!person.getJob().getName().equals(DefinedJob.DIRECTOR)) {
            throw new MyException(ErrorCode.PERSON_NOT_PERMISSION);
        }

//        find movie
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        check existed
        boolean directorExisted = !movie.getPersons().isEmpty() &&
                movie.getPersons().stream().anyMatch(existingPerson -> existingPerson.getJob().getName().equals(DefinedJob.DIRECTOR));

        if (!directorExisted) {
//            mysql
            movie.getPersons().add(person);
            movieRepository.save(movie);

//            elastic
            elasticMovie.getPersonIds().add(person.getId());
            elasticMovieRepository.save(elasticMovie);
        } else {
            throw new MyException(ErrorCode.DIRECTOR_OF_MOVIE_EXISTED);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void addActors(String movieId, AddActorsRequest addActorsRequest) {
//        find movie
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

        //        get movie in elastic
        ElasticMovie elasticMovie = elasticMovieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

        // get actor
        Set<Person> actors = addActorsRequest.getActorsId()
                .stream()
                .map(personId -> personRepository.findById(personId)
                        .orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED)))
                .collect(Collectors.toSet());

//        add all actors
        movie.getPersons().addAll(actors);
        movieRepository.save(movie);

//        elastic
        Set<String> actorIds = actors.stream()
                .map(Person::getId)
                .collect(Collectors.toSet());
        elasticMovie.getPersonIds().addAll(actorIds);
        elasticMovieRepository.save(elasticMovie);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void deleteActors(String movieId, DeleteActorsRequest deleteActorsRequest) {
//        find movie
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

        //        get movie in elastic
        ElasticMovie elasticMovie = elasticMovieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        get actor
        Set<Person> actors = deleteActorsRequest.getActorsId()
                .stream()
                .map(personId -> personRepository.findById(personId)
                        .orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED)))
                .collect(Collectors.toSet());

        movie.getPersons().removeAll(actors);
        movieRepository.save(movie);

//        elastic
        Set<String> actorIds = actors.stream()
                .map(Person::getId)
                .collect(Collectors.toSet());
        elasticMovie.getPersonIds().removeAll(actorIds);
        elasticMovieRepository.save(elasticMovie);
    }

    public List<MovieResponse> getAllByGenre(String genreId) {
        return movieRepository.findAllByGenresId(genreId).stream()
                .map(movie -> MovieResponse.builder()
                        .id(movie.getId())
                        .name(movie.getName())
                        .premiere(DateUtils.formatDate(movie.getPremiere()))
                        .language(movie.getLanguage())
                        .duration(movie.getDuration())
                        .rate(movie.getRate())
                        .image(movie.getImage())
                        .genres(movie.getGenres().stream()
                                .map(genre -> GenreResponse.builder()
                                        .id(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
