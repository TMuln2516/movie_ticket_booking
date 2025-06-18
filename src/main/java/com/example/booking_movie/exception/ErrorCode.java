package com.example.booking_movie.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //    request matching
    SEAT_IS_PROCESSING(HttpStatus.BAD_REQUEST.value(), "Ui! Có người đã chọn ghế trước bạn"),
    //    request matching
    NOTIFY_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Notify not exist"),
    //    request matching
    REQUEST_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Request not exist"),

    //    food
    FOOD_IMAGE_NOT_NULL(HttpStatus.BAD_REQUEST.value(), "Hình ảnh của món ăn không được để trống"),
    FOOD_EXISTED(HttpStatus.BAD_REQUEST.value(), "Món ăn đã tồn tại"),
    FOOD_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Món ăn không tồn tại"),
    //    coupon
    COUPON_EXISTED(HttpStatus.BAD_REQUEST.value(), "Mã giảm giá đã tồn tại"),
    COUPON_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Mã giảm giá không tồn tại"),

    // feedback
    FEEDBACK_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Bình luận không tồn tại"),

    // email
    EMAIL_EXISTED(HttpStatus.BAD_REQUEST.value(), "Email đã được đăng ký"),

    // manager
    MANAGER_EXISTED(HttpStatus.BAD_REQUEST.value(), "Username đã tồn tại"),

    // date
    DATE_NULL(HttpStatus.BAD_REQUEST.value(), "Ngày bắt đầu và ngày kết thúc không được để trống"),
    DATE_INVALID(HttpStatus.BAD_REQUEST.value(), "Ngày bắt đầu không thể lớn hơn ngày kết thúc"),

    // Session
    SESSION_EXPIRED_OR_INVALID(HttpStatus.BAD_REQUEST.value(), "Không có thông tin trong session"),

    // Showtime
    TICKET_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Vé không tồn tại"),

    // Showtime
    SHOWTIME_EXISTED(HttpStatus.BAD_REQUEST.value(), "Suất chiếu đã tồn tại"),
    SHOWTIME_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Suất chiếu không tồn tại"),
    SEAT_ALREADY_BOOK(HttpStatus.BAD_REQUEST.value(), "Xin lỗi ghế bạn chọn đã được đặt. Vui lòng thử lại sau"),
    SEAT_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Ghế không tồn tại"),

    // password
    CONFIRM_PASS_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "Xác nhận mật khẩu không trùng khớp"),
    PASSWORD_EXISTED(HttpStatus.BAD_REQUEST.value(), "Tài khoản đã có mật khẩu"),

    // otp
    MAIL_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Email không tồn tại"),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST.value(), "OTP đã hết hạn"),
    OTP_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "OTP không khớp"),

    // room
    ROOM_EXISTED(HttpStatus.BAD_REQUEST.value(), "Phòng đã tồn tại"),
    ROOM_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Phòng không tồn tại"),

    // theater
    THEATER_EXISTED(HttpStatus.BAD_REQUEST.value(), "Rạp chiếu phim đã tồn tại"),
    THEATER_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Rạp chiếu phim không tồn tại"),

    // Movie
    MOVIE_EXISTED(HttpStatus.BAD_REQUEST.value(), "Phim đã tồn tại"),
    MOVIE_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Phim không tồn tại"),
    DIRECTOR_OF_MOVIE_EXISTED(HttpStatus.BAD_REQUEST.value(), "Đạo diễn của phim đã tồn tại"),
    MOVIE_IMAGE_NOT_NULL(HttpStatus.BAD_REQUEST.value(), "Hình ảnh của phim không được để trống"),

    // Person
    PERSON_EXISTED(HttpStatus.BAD_REQUEST.value(), "Người này đã tồn tại"),
    PERSON_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Người này không tồn tại"),
    PERSON_NOT_PERMISSION(HttpStatus.BAD_REQUEST.value(), "Người này không có quyền"),

    // Genre
    GENRE_EXISTED(HttpStatus.BAD_REQUEST.value(), "Thể loại đã tồn tại"),
    GENRE_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Thể loại không tồn tại"),

    // Invalid password
    MISSING_UPPERCASE(HttpStatus.BAD_REQUEST.value(), "Mật khẩu phải chứa ít nhất 1 ký tự viết hoa."),
    MISSING_LOWERCASE(HttpStatus.BAD_REQUEST.value(), "Mật khẩu phải chứa ít nhất 1 ký tự viết thường."),
    MISSING_SPECIAL_CHARACTERS(HttpStatus.BAD_REQUEST.value(), "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt."),
    ILLEGAL_NUMERICAL_SEQUENCE(HttpStatus.BAD_REQUEST.value(), "Mật khẩu phải chứa ít nhất 1 ký tự số."),
    PASSWORD_LENGTH(HttpStatus.BAD_REQUEST.value(), "Mật khẩu phải có ít nhất 8 ký tự."),

    // User
    PASSWORD_OR_USERNAME_INCORRECT(HttpStatus.BAD_REQUEST.value(), "Tên người dùng hoặc mật khẩu không chính xác"),
    USER_EXISTED(HttpStatus.BAD_REQUEST.value(), "Người dùng đã tồn tại"),
    USER_NOT_EXISTED(HttpStatus.BAD_REQUEST.value(), "Người dùng không tồn tại"),
    ACCOUNT_BANNED(HttpStatus.BAD_REQUEST.value(), "Tài khoản của bạn đã bị cấm"),

    // Token
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "Bạn không có quyền truy cập"),
    UNAUTHENTICATED(HttpStatus.BAD_REQUEST.value(), "Chưa xác thực"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST.value(), "Token không hợp lệ"),

    // Invalid
    INVALID(HttpStatus.BAD_REQUEST.value(), "Tham số không hợp lệ");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
