package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedJob;
import com.example.booking_movie.dto.request.AddActorsRequest;
import com.example.booking_movie.dto.request.CreateMovieRequest;
import com.example.booking_movie.dto.request.DeleteActorsRequest;
import com.example.booking_movie.dto.request.UpdateMovieRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.entity.Elastic.ElasticMovie;
import com.example.booking_movie.entity.Feedback;
import com.example.booking_movie.entity.Genre;
import com.example.booking_movie.entity.Movie;
import com.example.booking_movie.entity.Person;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.GenreRepository;
import com.example.booking_movie.repository.MovieRepository;
import com.example.booking_movie.repository.PersonRepository;
import com.example.booking_movie.service.Elastic.ElasticMovieService;
import com.example.booking_movie.utils.DateUtils;
import com.example.booking_movie.utils.ValidUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {
    MovieRepository movieRepository;
    GenreRepository genreRepository;
    PersonRepository personRepository;
    ElasticMovieService elasticMovieService;

    ImageService imageService;

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
                .image(imageResponse.getImageUrl())
                .publicId(imageResponse.getPublicId())
                .genres(genres)
                .persons(persons)
                .feedbacks(feedbacks)
                .build();
        movieRepository.save(newMovie);

        elasticMovieService.createOrUpdate(ElasticMovie.builder()
                .id(newMovie.getId())
                .name(newMovie.getName())
                .premiere(DateUtils.formatDateToEpochMillis(newMovie.getPremiere()))
                .language(newMovie.getLanguage())
                .duration(newMovie.getDuration())
                .content(newMovie.getContent())
                .rate(newMovie.getRate())
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
    public List<MovieResponse> getAll() {
        return movieRepository.findAll()
                .stream()
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

        return MovieDetailResponse.builder()
                .id(movie.getId())
                .name(movie.getName())
                .premiere(DateUtils.formatDate(movie.getPremiere()))
                .language(movie.getLanguage())
                .duration(movie.getDuration())
                .content(movie.getContent())
                .rate(movie.getRate())
                .image(movie.getImage())
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

    //    update movie
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public UpdateMovieResponse update(String movieId, UpdateMovieRequest updateMovieRequest, MultipartFile file) throws IOException {
//        get movie
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

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
    public void delete(String movieId) throws IOException {
//        check exist
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        delete image
        imageService.deleteImage(movie.getPublicId());

//        delete genre in movie
        movie.getGenres().clear();

//        delete person in movie
        movie.getPersons().clear();

//        delete image in cloudinary
        imageService.deleteImage(movie.getPublicId());

//        delete
        movieRepository.delete(movie);
    }

    //    delete director
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void deleteDirector(String directorId) {
//        check exist
        Movie movie = movieRepository.findById(directorId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        use Iterator
        Iterator<Person> personIterator = movie.getPersons().iterator();
        while (personIterator.hasNext()) {  // person++
            Person person = personIterator.next();
            if (person.getJob().getName().equals(DefinedJob.DIRECTOR)) {
//                [movie.getPersons()].remove() -> delete person
                personIterator.remove();
//                delete movie
                person.getMovies().remove(movie);
            }
        }

//        save
        movieRepository.save(movie);
    }

    //    add director of movie (one movie -> one director)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void addDirector(String movieId, String directorId) {
//        find person
        Person person = personRepository.findById(directorId).orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED));

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
            movie.getPersons().add(person);
            movieRepository.save(movie);
        } else {
            throw new MyException(ErrorCode.DIRECTOR_OF_MOVIE_EXISTED);
        }
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void addActors(String movieId, AddActorsRequest addActorsRequest) {
//        find movie
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

        // get actor
        Set<Person> actors = addActorsRequest.getActorsId()
                .stream()
                .map(personId -> personRepository.findById(personId)
                        .orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED)))
                .collect(Collectors.toSet());

//        add all actors
        movie.getPersons().addAll(actors);
        movieRepository.save(movie);
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void deleteActors(String movieId, DeleteActorsRequest deleteActorsRequest) {
//        find movie
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MyException(ErrorCode.MOVIE_NOT_EXISTED));

//        get actor
        Set<Person> actors = deleteActorsRequest.getActorsId()
                .stream()
                .map(personId -> personRepository.findById(personId)
                        .orElseThrow(() -> new MyException(ErrorCode.PERSON_NOT_EXISTED)))
                .collect(Collectors.toSet());

        movie.getPersons().removeAll(actors);
        movieRepository.save(movie);
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
