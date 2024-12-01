package com.example.booking_movie.service;

import com.example.booking_movie.constant.DefinedRole;
import com.example.booking_movie.dto.request.CreateCouponRequest;
import com.example.booking_movie.dto.request.CreateFeedbackRequest;
import com.example.booking_movie.dto.request.UpdateCouponRequest;
import com.example.booking_movie.dto.request.UpdateFeedbackRequest;
import com.example.booking_movie.dto.response.*;
import com.example.booking_movie.entity.Coupon;
import com.example.booking_movie.entity.Feedback;
import com.example.booking_movie.exception.ErrorCode;
import com.example.booking_movie.exception.MyException;
import com.example.booking_movie.repository.*;
import com.example.booking_movie.utils.DateUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponService {
    CouponRepository couponRepository;
    TicketRepository ticketRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public CreateCouponResponse create(CreateCouponRequest createCouponRequest) {
        if (couponRepository.existsByCode(createCouponRequest.getCode())) {
            throw new MyException(ErrorCode.COUPON_EXISTED);
        }

        var couponInfo = Coupon.builder()
                .code(createCouponRequest.getCode())
                .discountType(createCouponRequest.getDiscountType())
                .discountValue(createCouponRequest.getDiscountValue())
                .startDate(createCouponRequest.getStartDate())
                .endDate(createCouponRequest.getEndDate())
                .minValue(createCouponRequest.getMinValue())
                .description(createCouponRequest.getDescription())
                .status(false)
                .build();
        couponRepository.save(couponInfo);

        return CreateCouponResponse.builder()
                .id(couponInfo.getId())
                .code(couponInfo.getCode())
                .discountType(couponInfo.getDiscountType())
                .discountValue(couponInfo.getDiscountValue())
                .startDate(couponInfo.getStartDate())
                .endDate(couponInfo.getEndDate())
                .minValue(couponInfo.getMinValue())
                .description(couponInfo.getDescription())
                .status(couponInfo.getStatus())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public UpdateCouponResponse update(String couponId, UpdateCouponRequest updateCouponRequest) {
        var couponInfo = couponRepository.findById(couponId)
                .orElseThrow(() -> new MyException(ErrorCode.COUPON_NOT_EXISTED));

        couponInfo.setCode(updateCouponRequest.getCode());
        couponInfo.setDiscountType(updateCouponRequest.getDiscountType());
        couponInfo.setDiscountValue(updateCouponRequest.getDiscountValue());
        couponInfo.setStartDate(updateCouponRequest.getStartDate());
        couponInfo.setEndDate(updateCouponRequest.getEndDate());
        couponInfo.setMinValue(updateCouponRequest.getMinValue());
        couponInfo.setDescription(updateCouponRequest.getDescription());
        couponRepository.save(couponInfo);

        return UpdateCouponResponse.builder()
                .id(couponInfo.getId())
                .code(couponInfo.getCode())
                .discountType(couponInfo.getDiscountType())
                .discountValue(couponInfo.getDiscountValue())
                .startDate(couponInfo.getStartDate())
                .endDate(couponInfo.getEndDate())
                .minValue(couponInfo.getMinValue())
                .description(couponInfo.getDescription())
                .status(couponInfo.getStatus())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void toggleStatus(String couponId) {
        var couponInfo = couponRepository.findById(couponId)
                .orElseThrow(() -> new MyException(ErrorCode.COUPON_NOT_EXISTED));

        couponInfo.setStatus(!couponInfo.getStatus());
        couponRepository.save(couponInfo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream()
                .map(coupon -> CouponResponse.builder()
                        .id(coupon.getId())
                        .code(coupon.getCode())
                        .discountType(coupon.getDiscountType())
                        .discountValue(coupon.getDiscountValue())
                        .startDate(coupon.getStartDate())
                        .endDate(coupon.getEndDate())
                        .minValue(coupon.getMinValue())
                        .description(coupon.getDescription())
                        .status(coupon.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    public CouponResponse getDetails(String couponId) {
        var couponInfo = couponRepository.findById(couponId)
                .orElseThrow(() -> new MyException(ErrorCode.COUPON_NOT_EXISTED));

        return CouponResponse.builder()
                .id(couponInfo.getId())
                .code(couponInfo.getCode())
                .discountType(couponInfo.getDiscountType())
                .discountValue(couponInfo.getDiscountValue())
                .startDate(couponInfo.getStartDate())
                .endDate(couponInfo.getEndDate())
                .minValue(couponInfo.getMinValue())
                .description(couponInfo.getDescription())
                .status(couponInfo.getStatus())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void delete(String couponId) {
        var couponInfo = couponRepository.findById(couponId)
                .orElseThrow(() -> new MyException(ErrorCode.COUPON_NOT_EXISTED));

        couponInfo.getTickets().forEach(ticket -> {
            ticket.setCoupon(null);
            ticketRepository.save(ticket);
        });

        couponRepository.delete(couponInfo);
    }
}
