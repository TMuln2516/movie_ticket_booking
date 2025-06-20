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
//import com.example.booking_movie.repository.Elastic.ElasticMovieRepository;
//import com.example.booking_movie.service.Elastic.ElasticMovieService;
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
import org.springframework.security.core.Authentication;
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
//    ElasticMovieRepository elasticMovieRepository;
    MovieRepository movieRepository;
    GenreRepository genreRepository;
    PersonRepository personRepository;
    UserRepository userRepository;
    TicketRepository ticketRepository;
    FeedbackRepository feedbackRepository;
    ShowtimeRepository showtimeRepository;

//    ElasticMovieService elasticMovieService;
    ImageService imageService;
    ShowtimeService showtimeService;

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

        // Xóa cache ListMovie
        redisTemplate.delete("ListMovie");

//        elastic
//        elasticMovieService.createOrUpdate(ElasticMovie.builder()
//                .id(newMovie.getId())
//                .name(newMovie.getName())
//                .premiere(DateUtils.formatDateToEpochMillis(newMovie.getPremiere()))
//                .language(newMovie.getLanguage())
//                .duration(newMovie.getDuration())
//                .content(newMovie.getContent())
//                .rate(newMovie.getRate())
//                .createAt(DateUtils.formatDateToEpochMillis(newMovie.getCreateAt()))
//                .image(newMovie.getImage())
//                .publicId(newMovie.getPublicId())
//                .genreIds(newMovie.getGenres().stream()
//                        .map(Genre::getId)
//                        .collect(Collectors.toSet()))
//                .personIds(newMovie.getPersons().stream()
//                        .map(Person::getId)
//                        .collect(Collectors.toSet()))
//                .build());

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

    private List<MovieCacheResponse> getCachedMovies() throws JsonProcessingException {
        String jsonMovies = (String) redisTemplate.opsForValue().get("ListMovie");

        if (jsonMovies != null) {
            return objectMapper.readValue(jsonMovies,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, MovieCacheResponse.class));
        } else {
            List<MovieCacheResponse> movies = movieRepository.findAll().stream()
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
            return movies;
        }
    }


    //        get all movie
    public List<MovieResponse> getAll() throws JsonProcessingException {

//        Kiểm tra và lấy thông tin movie từ cache
        List<MovieCacheResponse> movies = getCachedMovies();

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
//        ElasticMovie elasticMovie = elasticMovieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

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
//        elasticMovie.setName(movie.getName());
//        elasticMovie.setPremiere(DateUtils.formatDateToEpochMillis(movie.getPremiere()));
//        elasticMovie.setLanguage(movie.getLanguage());
//        elasticMovie.setDuration(movie.getDuration());
//        elasticMovie.setContent(movie.getContent());
//        elasticMovie.setRate(movie.getRate());
//        elasticMovieService.createOrUpdate(elasticMovie);

        // Xóa cache ListMovie
        redisTemplate.delete("ListMovie");

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
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void delete(String movieId) throws IOException {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

        // Xóa ảnh trên cloud
        imageService.deleteImage(movie.getPublicId());

        // Xóa liên kết với genre và person
        movie.getGenres().clear();
        movie.getPersons().clear();

        // Xóa feedbacks
        feedbackRepository.deleteByMovieId(movieId);

        // Xóa các showtime liên quan
        Set<Showtime> showtimes = movie.getShowtimes();
        for (Showtime showtime : showtimes) {
            showtimeService.deleteShowtime(showtime.getId());
        }

        // Xóa movie
        movieRepository.delete(movie);

        // Xóa cache
        redisTemplate.delete("ListMovie");
    }


    public GetMovieByGenreResponse getAllByGenre(String genreId) throws JsonProcessingException {
        List<MovieCacheResponse> movies = getCachedMovies(); // dùng cache

        // Lọc phim theo genreId
        List<MovieCacheResponse> filteredMovies = movies.stream()
                .filter(movie -> movie.getGenreIds().contains(genreId))
                .toList();

        // Lấy tất cả person liên quan đến các movie
        Set<String> allPersonIds = filteredMovies.stream()
                .flatMap(movie -> movie.getPersonIds().stream())
                .collect(Collectors.toSet());
        Map<String, Person> personMap = personRepository.findAllById(allPersonIds).stream()
                .collect(Collectors.toMap(Person::getId, p -> p));

        // Lấy tất cả genre liên quan để convert sang GenreResponse
        Set<String> allGenreIds = filteredMovies.stream()
                .flatMap(movie -> movie.getGenreIds().stream())
                .collect(Collectors.toSet());
        Map<String, Genre> genreMap = genreRepository.findAllById(allGenreIds).stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        // Lấy thông tin genre tương ứng
        Genre genre = genreMap.get(genreId);
        if (genre == null) {
            throw new MyException(ErrorCode.GENRE_NOT_EXISTED); // hoặc trả về null, tùy xử lý
        }

        // Xác định người dùng và quyền bình luận
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isGuest = (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser"));

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

        List<MovieDetailResponse> movieDetails = filteredMovies.stream()
                .map(movie -> {
                    Set<Person> persons = movie.getPersonIds().stream()
                            .map(personMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    // Tách director và actor
                    AtomicReference<Person> director = new AtomicReference<>();
                    Set<Person> actors = new HashSet<>();
                    persons.forEach(person -> {
                        if (DefinedJob.DIRECTOR.equalsIgnoreCase(person.getJob().getName())) {
                            director.set(person);
                        } else {
                            actors.add(person);
                        }
                    });

                    PersonResponse directorResponse = Optional.ofNullable(director.get())
                            .map(d -> PersonResponse.builder()
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

                    Set<PersonResponse> actorResponses = actors.stream()
                            .map(a -> PersonResponse.builder()
                                    .id(a.getId())
                                    .name(a.getName())
                                    .gender(a.getGender())
                                    .dateOfBirth(DateUtils.formatDate(a.getDateOfBirth()))
                                    .image(a.getImage())
                                    .job(JobResponse.builder()
                                            .id(a.getJob().getId())
                                            .name(a.getJob().getName())
                                            .build())
                                    .build())
                            .collect(Collectors.toSet());

                    Set<GenreResponse> genreResponses = movie.getGenreIds().stream()
                            .map(genreMap::get)
                            .filter(Objects::nonNull)
                            .map(g -> GenreResponse.builder()
                                    .id(g.getId())
                                    .name(g.getName())
                                    .build())
                            .collect(Collectors.toSet());

                    boolean canComment = !isGuest && canCommentMap.containsKey(movie.getId());

                    return MovieDetailResponse.builder()
                            .id(movie.getId())
                            .name(movie.getName())
                            .premiere(movie.getPremiere())
                            .language(movie.getLanguage())
                            .duration(movie.getDuration())
                            .content(movie.getContent())
                            .rate(movie.getRate())
                            .image(movie.getImage())
                            .canComment(canComment)
                            .genres(genreResponses)
                            .director(directorResponse)
                            .actors(actorResponses)
                            .build();
                })
                .collect(Collectors.toList());

        return GetMovieByGenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .listMovies(movieDetails)
                .build();
    }

    public GetMovieByPersonResponse getAllByPerson(String personId) throws JsonProcessingException {
        List<MovieCacheResponse> movies = getCachedMovies(); // cache

        // Lọc phim có chứa personId
        List<MovieCacheResponse> filteredMovies = movies.stream()
                .filter(movie -> movie.getPersonIds().contains(personId))
                .toList();

        // Lấy thông tin person chính
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED));

        // Lấy tất cả person liên quan
        Set<String> allPersonIds = filteredMovies.stream()
                .flatMap(movie -> movie.getPersonIds().stream())
                .collect(Collectors.toSet());
        Map<String, Person> personMap = personRepository.findAllById(allPersonIds).stream()
                .collect(Collectors.toMap(Person::getId, p -> p));

        // Lấy tất cả genre liên quan
        Set<String> allGenreIds = filteredMovies.stream()
                .flatMap(movie -> movie.getGenreIds().stream())
                .collect(Collectors.toSet());
        Map<String, Genre> genreMap = genreRepository.findAllById(allGenreIds).stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        // Xác định user và canComment
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isGuest = (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser"));

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

        // Xử lý danh sách MovieDetailResponse
        List<MovieDetailResponse> movieDetails = filteredMovies.stream()
                .map(movie -> {
                    Set<Person> persons = movie.getPersonIds().stream()
                            .map(personMap::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    // Phân loại đạo diễn / diễn viên
                    AtomicReference<Person> director = new AtomicReference<>();
                    Set<Person> actors = new HashSet<>();
                    persons.forEach(p -> {
                        if (DefinedJob.DIRECTOR.equalsIgnoreCase(p.getJob().getName())) {
                            director.set(p);
                        } else {
                            actors.add(p);
                        }
                    });

                    PersonResponse directorResponse = Optional.ofNullable(director.get())
                            .map(d -> PersonResponse.builder()
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

                    Set<PersonResponse> actorResponses = actors.stream()
                            .map(a -> PersonResponse.builder()
                                    .id(a.getId())
                                    .name(a.getName())
                                    .gender(a.getGender())
                                    .dateOfBirth(DateUtils.formatDate(a.getDateOfBirth()))
                                    .image(a.getImage())
                                    .job(JobResponse.builder()
                                            .id(a.getJob().getId())
                                            .name(a.getJob().getName())
                                            .build())
                                    .build())
                            .collect(Collectors.toSet());

                    Set<GenreResponse> genreResponses = movie.getGenreIds().stream()
                            .map(genreMap::get)
                            .filter(Objects::nonNull)
                            .map(g -> GenreResponse.builder()
                                    .id(g.getId())
                                    .name(g.getName())
                                    .build())
                            .collect(Collectors.toSet());

                    boolean canComment = !isGuest && canCommentMap.containsKey(movie.getId());

                    return MovieDetailResponse.builder()
                            .id(movie.getId())
                            .name(movie.getName())
                            .premiere(movie.getPremiere())
                            .language(movie.getLanguage())
                            .duration(movie.getDuration())
                            .content(movie.getContent())
                            .rate(movie.getRate())
                            .image(movie.getImage())
                            .canComment(canComment)
                            .genres(genreResponses)
                            .director(directorResponse)
                            .actors(actorResponses)
                            .build();
                })
                .collect(Collectors.toList());

        // Trả kết quả
        return GetMovieByPersonResponse.builder()
                .id(person.getId())
                .name(person.getName())
                .gender(person.getGender())
                .dateOfBirth(DateUtils.formatDate(person.getDateOfBirth()))
                .image(person.getImage())
                .job(JobResponse.builder()
                        .id(person.getJob().getId())
                        .name(person.getJob().getName())
                        .build())
                .listMovies(movieDetails)
                .build();
    }
}
