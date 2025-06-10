package com.example.booking_movie.initializer;

import com.example.booking_movie.entity.Elastic.ElasticMovie;
import com.example.booking_movie.entity.Genre;
import com.example.booking_movie.entity.Movie;
import com.example.booking_movie.entity.Person;
//import com.example.booking_movie.repository.Elastic.ElasticMovieRepository;
import com.example.booking_movie.repository.GenreRepository;
import com.example.booking_movie.repository.MovieRepository;
import com.example.booking_movie.repository.PersonRepository;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieInitializer {
    MovieRepository movieRepository;
    GenreRepository genreRepository;
    PersonRepository personRepository;

//    ElasticMovieRepository elasticMovieRepository;

    public void movieInitializer() {
        var drama = genreRepository.findByName("Chính Kịch").orElseThrow();
        var romantic = genreRepository.findByName("Lãng Mạn").orElseThrow();
        var history = genreRepository.findByName("Hư Cấu Lịch Sử").orElseThrow();
        var war = genreRepository.findByName("Chiến Tranh").orElseThrow();
        var horror = genreRepository.findByName("Kinh Dị").orElseThrow();

        var Makoto = personRepository.findByName("Shinkai Makoto").orElseThrow();
        var Ryunosuke = personRepository.findByName("Kamiki Ryūnosuke").orElseThrow();
        var Mone = personRepository.findByName("Kamishiraishi Mone").orElseThrow();

        var TranThanh = personRepository.findByName("Trấn Thành").orElseThrow();
        var PhuongAnhDao = personRepository.findByName("Phương Anh Đào").orElseThrow();
        var TuanTran = personRepository.findByName("Tuấn Trần").orElseThrow();

        var PhiTienSon = personRepository.findByName("Phi Tiến Sơn").orElseThrow();
        var DoanQuocDam = personRepository.findByName("Doãn Quốc Đam").orElseThrow();
        var CaoThiThuyLinh = personRepository.findByName("Cao Thị Thùy Linh").orElseThrow();
        var TranLuc = personRepository.findByName("Trần Lực").orElseThrow();

        var LuuThanhLuan = personRepository.findByName("Lưu Thành Luân").orElseThrow();
        var HongDao = personRepository.findByName("Hồng Đào").orElseThrow();
        var NguyenThucThuyTien = personRepository.findByName("Nguyễn Thúc Thùy Tiên").orElseThrow();

        if (!movieRepository.existsByName("Your Name")) {
            Set<Genre> genreSet = new HashSet<>();
            genreSet.add(drama);
            genreSet.add(romantic);

            Set<Person> personSet = new HashSet<>();
            personSet.add(Makoto);
            personSet.add(Ryunosuke);
            personSet.add(Mone);

            Movie movieInfo = Movie.builder()
                    .name("Your Name")
                    .content("Bộ phim kể về Mitsuha – một nữ sinh trung học buồn chán với cuộc sống tẻ nhạt ở vùng thôn quê và Taki – một chàng trai Tokyo – vì một lý do nào đó bị hoán đổi cơ thể trong khi sao chổi thiên niên kỉ đang đến gần.")
                    .duration(120)
                    .language("Japanese")
                    .premiere(LocalDate.of(2024, 11, 15))
                    .rate(9.9)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1747706622/MovieImage/rhhkssq3gsxk0k29nbvi.jpg")
                    .publicId("MovieImage/h4izbjcrlshmyjzfdrri")
                    .genres(genreSet)
                    .persons(personSet)
                    .build();
            movieRepository.save(movieInfo);

//            save elastic
//            elasticMovieRepository.save(ElasticMovie.builder()
//                    .id(movieInfo.getId())
//                    .name(movieInfo.getName())
//                    .premiere(DateUtils.formatDateToEpochMillis(movieInfo.getPremiere()))
//                    .language(movieInfo.getLanguage())
//                    .duration(movieInfo.getDuration())
//                    .content(movieInfo.getContent())
//                    .rate(movieInfo.getRate())
//                    .image(movieInfo.getImage())
//                    .publicId(movieInfo.getPublicId())
//                    .genreIds(movieInfo.getGenres().stream()
//                            .map(Genre::getId)
//                            .collect(Collectors.toSet()))
//                    .personIds(movieInfo.getPersons().stream()
//                            .map(Person::getId)
//                            .collect(Collectors.toSet()))
//                    .build());
        }

        if (!movieRepository.existsByName("Mai")) {
            Set<Genre> genreSet = new HashSet<>();
            genreSet.add(drama);
            genreSet.add(romantic);

            Set<Person> personSet = new HashSet<>();
            personSet.add(TranThanh);
            personSet.add(PhuongAnhDao);
            personSet.add(HongDao);
            personSet.add(TuanTran);

            Movie movieInfo = Movie.builder()
                    .name("Mai")
                    .content("MAI là câu chuyện về cuộc đời của người phụ nữ cùng tên, với ánh nhìn tĩnh lặng, xuyên thấu \" Quá khứ chưa ngủ yên, ngày mai liệu sẽ đến?..\"")
                    .duration(120)
                    .language("Vietnamese")
                    .premiere(LocalDate.of(2024, 11, 15))
                    .rate(9.9)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1748344378/MovieImage/ojac14tyt9ndx96runbj.jpg")
                    .publicId("MovieImage/t9rax2ryriomf74bs1yt")
                    .genres(genreSet)
                    .persons(personSet)
                    .build();
            movieRepository.save(movieInfo);

            //            save elastic
//            elasticMovieRepository.save(ElasticMovie.builder()
//                    .id(movieInfo.getId())
//                    .name(movieInfo.getName())
//                    .premiere(DateUtils.formatDateToEpochMillis(movieInfo.getPremiere()))
//                    .language(movieInfo.getLanguage())
//                    .duration(movieInfo.getDuration())
//                    .content(movieInfo.getContent())
//                    .rate(movieInfo.getRate())
//                    .image(movieInfo.getImage())
//                    .publicId(movieInfo.getPublicId())
//                    .genreIds(movieInfo.getGenres().stream()
//                            .map(Genre::getId)
//                            .collect(Collectors.toSet()))
//                    .personIds(movieInfo.getPersons().stream()
//                            .map(Person::getId)
//                            .collect(Collectors.toSet()))
//                    .build());
        }

        if (!movieRepository.existsByName("Đào, phở và piano")) {
            Set<Genre> genreSet = new HashSet<>();
            genreSet.add(history);
            genreSet.add(romantic);
            genreSet.add(war);

            Set<Person> personSet = new HashSet<>();
            personSet.add(PhiTienSon);
            personSet.add(DoanQuocDam);
            personSet.add(CaoThiThuyLinh);
            personSet.add(TranLuc);

            Movie movieInfo = Movie.builder()
                    .name("Đào, phở và piano")
                    .content("Lấy bối cảnh trận chiến đông xuân kéo dài 60 ngày đêm từ cuối năm 1946 đến đầu năm 1947 ở Hà Nội, câu chuyện theo chân chàng dân quân Văn Dân và chuyện tình với nàng tiểu thư đam mê dương cầm Thục Hương")
                    .duration(120)
                    .language("Vietnamese")
                    .premiere(LocalDate.of(2024, 11, 15))
                    .rate(9.9)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732207761/MovieImage/slfra2itmhuapkyuybeo.webp")
                    .publicId("MovieImage/slfra2itmhuapkyuybeo")
                    .genres(genreSet)
                    .persons(personSet)
                    .build();
            movieRepository.save(movieInfo);

            //            save elastic
//            elasticMovieRepository.save(ElasticMovie.builder()
//                    .id(movieInfo.getId())
//                    .name(movieInfo.getName())
//                    .premiere(DateUtils.formatDateToEpochMillis(movieInfo.getPremiere()))
//                    .language(movieInfo.getLanguage())
//                    .duration(movieInfo.getDuration())
//                    .content(movieInfo.getContent())
//                    .rate(movieInfo.getRate())
//                    .image(movieInfo.getImage())
//                    .publicId(movieInfo.getPublicId())
//                    .genreIds(movieInfo.getGenres().stream()
//                            .map(Genre::getId)
//                            .collect(Collectors.toSet()))
//                    .personIds(movieInfo.getPersons().stream()
//                            .map(Person::getId)
//                            .collect(Collectors.toSet()))
//                    .build());
        }

        if (!movieRepository.existsByName("Linh Miêu: Quỷ Nhập Tràng")) {
            Set<Genre> genreSet = new HashSet<>();
            genreSet.add(horror);
            genreSet.add(drama);

            Set<Person> personSet = new HashSet<>();
            personSet.add(LuuThanhLuan);
            personSet.add(HongDao);
            personSet.add(NguyenThucThuyTien);

            Movie movieInfo = Movie.builder()
                    .name("Linh Miêu: Quỷ Nhập Tràng")
                    .content("Nửa đêm, đoàn kiệu rước thây xuất hiện trong không khí ma mị, u ám, kèm tiếng múa chén kinh dị khiến ai nghe qua cũng lạnh người. Có ai chứng kiến cảnh tượng này bao giờ chưa?")
                    .duration(120)
                    .language("Japanese")
                    .premiere(LocalDate.of(2024, 11, 15))
                    .rate(9.9)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732215221/MovieImage/wzstoenx4cwts9tegj9f.jpg")
                    .publicId("MovieImage/wzstoenx4cwts9tegj9f")
                    .genres(genreSet)
                    .persons(personSet)
                    .build();
            movieRepository.save(movieInfo);

            //            save elastic
//            elasticMovieRepository.save(ElasticMovie.builder()
//                    .id(movieInfo.getId())
//                    .name(movieInfo.getName())
//                    .premiere(DateUtils.formatDateToEpochMillis(movieInfo.getPremiere()))
//                    .language(movieInfo.getLanguage())
//                    .duration(movieInfo.getDuration())
//                    .content(movieInfo.getContent())
//                    .rate(movieInfo.getRate())
//                    .image(movieInfo.getImage())
//                    .publicId(movieInfo.getPublicId())
//                    .genreIds(movieInfo.getGenres().stream()
//                            .map(Genre::getId)
//                            .collect(Collectors.toSet()))
//                    .personIds(movieInfo.getPersons().stream()
//                            .map(Person::getId)
//                            .collect(Collectors.toSet()))
//                    .build());
        }
    }
}
