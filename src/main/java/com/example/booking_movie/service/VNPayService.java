package com.example.booking_movie.service;

import com.example.booking_movie.config.VNPayConfig;
import com.example.booking_movie.entity.ScheduleSeat;
import com.example.booking_movie.entity.Showtime;
import com.example.booking_movie.entity.Ticket;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.ScheduleSeatRepository;
import com.example.booking_movie.repository.ShowtimeRepository;
import com.example.booking_movie.repository.TicketDetailsRepository;
import com.example.booking_movie.repository.TicketRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayService {
    private final TicketDetailsRepository ticketDetailsRepository;
    @NonFinal
    @Value("${vnpay.vnp_TmnCode}")
    protected String vnp_TmnCode;
    @NonFinal
    @Value("${vnpay.secretKey}")
    protected String secretKey;
    @NonFinal
    @Value("${vnpay.vnp_Version}")
    protected String vnp_Version;
    @NonFinal
    @Value("${vnpay.vnp_Command}")
    protected String vnp_Command;
    @NonFinal
    @Value("${vnpay.vnp_PayUrl}")
    protected String vnp_PayUrl;
    @NonFinal
    @Value("${vnpay.vnp_ReturnUrl}")
    protected String vnp_ReturnUrl;

    TicketRepository ticketRepository;
    ScheduleSeatRepository scheduleSeatRepository;
    ShowtimeRepository showtimeRepository;
//    ElasticTicketService elasticTicketService;

    public String createPaymentVNPay(HttpServletRequest req) {
        String ticketId = req.getParameter("ticketId");

        Ticket ticketInfo = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new MyException(ErrorCode.TICKET_NOT_EXISTED));

        Showtime showtimeInfo = ticketInfo.getShowtime();

        // Kiểm tra ghế đã được đặt
        ticketInfo.getTicketDetails().forEach(ticketDetails -> {
            boolean isBooked = scheduleSeatRepository
                    .findByShowtimeIdAndSeatId(showtimeInfo.getId(), ticketDetails.getSeat().getId())
                    .getStatus();
            if (isBooked) {
                // Xoá ticket và chi tiết nếu ghế đã được đặt
                ticketDetailsRepository.deleteAll(ticketInfo.getTicketDetails());
                ticketRepository.delete(ticketInfo);
                throw new MyException(ErrorCode.SEAT_ALREADY_BOOK);
            }
        });

        long amount = getAmount(ticketId) * 100L;
        String bankCode = req.getParameter("bankCode");
        String vnp_IpAddr = VNPayConfig.getIpAddress(req);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_Locale", "vn");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        vnp_Params.put("vnp_TxnRef", ticketId);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + ticketId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Sử dụng múi giờ Việt Nam chính xác
        TimeZone vnTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar cld = Calendar.getInstance(vnTimeZone);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(vnTimeZone);

        // Thời gian tạo
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Thời gian hết hạn (cộng thêm 15 phút)
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Tạo chuỗi dữ liệu cần ký và URL query
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        // Ký dữ liệu
        String vnp_SecureHash = VNPayConfig.hmacSHA512(secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        // Trả về URL thanh toán
        return vnp_PayUrl + "?" + query;
    }


    private long getAmount(String ticketId) {
        Ticket ticketInfo = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new MyException(ErrorCode.TICKET_NOT_EXISTED));

        return ticketInfo.getAmount().longValue();
    }

    public void callBack(String responseCode, String ticketId) {
        Ticket ticketInfo = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new MyException(ErrorCode.TICKET_NOT_EXISTED));

        if (responseCode.equals("00")) {
//            update status
            ticketInfo.setStatus(true);

//            update status schedule
            AtomicInteger countSeat = new AtomicInteger(0);
            ticketInfo.getTicketDetails().forEach(ticketDetails -> {
                ScheduleSeat scheduleSeatInfo = scheduleSeatRepository.findByShowtimeIdAndSeatId(ticketInfo.getShowtime().getId(), ticketDetails.getSeat().getId());
                scheduleSeatInfo.setStatus(true);
                countSeat.incrementAndGet();
            });

//            update lượng ghế còn lại
            Showtime showtimeInfo = ticketInfo.getShowtime();
            showtimeInfo.setEmptySeat(showtimeInfo.getEmptySeat() - countSeat.get());
            showtimeRepository.save(showtimeInfo);
        }
        ticketRepository.save(ticketInfo);

//        insert value to document elastic
//        elasticTicketService.createOrUpdate(ticketInfo);
    }
}
