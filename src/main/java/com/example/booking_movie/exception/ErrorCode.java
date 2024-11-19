package com.example.booking_movie.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    //    manager
    MANAGER_EXISTED(HttpStatus.BAD_REQUEST.value(), "Username đã tồn tại"),
    //    date
    DATE_NULL(HttpStatus.BAD_REQUEST.value(), "Ngày bắt đầu và ngày kết thúc không được để trống"),
    DATE_INVALID(HttpStatus.BAD_REQUEST.value(), "Ngày bắt đầu không thể lớn hơn ngày kết thúc"),
    //    Session
    SESSION_EXPIRED_OR_INVALID(HttpStatus.BAD_REQUEST.value(), "Không có thông tin trong session"),
    //    Showtime
    TICKET_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Vé không tồn tại"),
    //    Showtime
    SHOWTIME_EXISTED(HttpStatus.BAD_REQUEST.value(), "Showtime already existed"),
    SHOWTIME_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Suất chiếu không tồn tại"),
    SEAT_ALREADY_BOOK(HttpStatus.BAD_REQUEST.value(), "Xin lỗi ghế bạn chọn đã được đặt. Vui lòng thử lại sau"),
    SEAT_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Ghế không tồn tại"),

    //    password
    CONFIRM_PASS_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "Xác nhận mật khẩu không trùng khớp"),
    PASSWORD_EXISTED(HttpStatus.BAD_REQUEST.value(), "Tài khoản đã có mật khẩu"),

    //    otp
    MAIL_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Email not existed"),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST.value(), "OTP has expired"),
    OTP_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "OTP does not match"),

    //    room
    ROOM_EXISTED(HttpStatus.BAD_REQUEST.value(), "Room already existed"),
    ROOM_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Room not exist"),

    //    theater
    THEATER_EXISTED(HttpStatus.BAD_REQUEST.value(), "Theater already existed"),
    THEATER_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Theater not exist"),

    //    Movie
    MOVIE_EXISTED(HttpStatus.BAD_REQUEST.value(), "Movie already existed"),
    MOVIE_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Movie not exist"),
    DIRECTOR_OF_MOVIE_EXISTED(HttpStatus.BAD_REQUEST.value(), "Director of movie existed"),
    MOVIE_IMAGE_NOT_NULL(HttpStatus.BAD_REQUEST.value(), "Hình ảnh của Phim không được để trống"),

    //    Person
    PERSON_EXISTED(HttpStatus.BAD_REQUEST.value(), "Person already existed"),
    PERSON_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Person not exist"),
    PERSON_NOT_PERMISSION(HttpStatus.BAD_REQUEST.value(), "Person not permission"),

    //    Genre
    GENRE_EXISTED(HttpStatus.BAD_REQUEST.value(), "Genre already existed"),
    GENRE_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Genre not exist"),

    //    Invalid password
    MISSING_UPPERCASE(HttpStatus.BAD_REQUEST.value(), "Password must contain 1 or more uppercase characters."),
    MISSING_LOWERCASE(HttpStatus.BAD_REQUEST.value(), "Password must contain 1 or more lowercase characters."),
    MISSING_SPECIAL_CHARACTERS(HttpStatus.BAD_REQUEST.value(), "Password must contain 1 or more special characters."),
    ILLEGAL_NUMERICAL_SEQUENCE(HttpStatus.BAD_REQUEST.value(), "Password contains the illegal numerical sequence '123456'."),
    PASSWORD_LENGTH(HttpStatus.BAD_REQUEST.value(), "Password must be 8 or more characters in length."),

    //    User
    PASSWORD_OR_USERNAME_INCORRECT(HttpStatus.BAD_REQUEST.value(), "Username or Password incorrect"),
    USER_EXISTED(HttpStatus.BAD_REQUEST.value(), "User already existed"),
    USER_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "User not exist"),
    ACCOUNT_BANNED(HttpStatus.BAD_REQUEST.value(), "Sorry! Your account has already banned"),

    //    Token
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "You do not have permission"),
    UNAUTHENTICATED(HttpStatus.BAD_REQUEST.value(), "Unauthenticated"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST.value(), "Token Invalid"),

    //    Invalid
    INVALID(HttpStatus.BAD_REQUEST.value(), "Invalid Parameter");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
