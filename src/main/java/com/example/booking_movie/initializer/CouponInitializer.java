package com.example.booking_movie.initializer;

import com.example.booking_movie.constant.DefinedDiscountType;
import com.example.booking_movie.entity.Coupon;
import com.example.booking_movie.repository.CouponRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponInitializer {
    CouponRepository couponRepository;

    public void initializeCoupons() {
        if (!couponRepository.existsByCode("CHAOMUNGT12")) {
            couponRepository.save(Coupon.builder()
                    .code("CHAOMUNGT12")
                    .discountType(DefinedDiscountType.PERCENTAGE)
                    .discountValue(40)
                    .minValue(200000.0)
                    .startDate(LocalDate.of(2024, 12, 1))
                    .endDate(LocalDate.of(2026, 12, 15))
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732215221/voucher/cxkzttmdpge92jawczly.jpg")
                    .publicId("voucher/cxkzttmdpge92jawczly")
                    .status(true)
                    .build());
        }

        if (!couponRepository.existsByCode("CHAOMUNG")) {
            couponRepository.save(Coupon.builder()
                    .code("CHAOMUNG")
                    .discountType(DefinedDiscountType.FIXED)
                    .discountValue(10000)
                    .minValue(100000.0)
                    .startDate(LocalDate.of(2024, 12, 1))
                    .endDate(LocalDate.of(2024, 12, 15))
                    .status(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1732215221/voucher/cxkzttmdpge92jawczly.jpg")
                    .publicId("voucher/cxkzttmdpge92jawczly")
                    .build());
        }

        if (!couponRepository.existsByCode("GIAMGIA")) {
            couponRepository.save(Coupon.builder()
                    .code("GIAMGIA")
                    .discountType(DefinedDiscountType.OTHER)
                    .discountValue(88000)
                    .minValue(0.0)
                    .startDate(LocalDate.of(2024, 12, 1))
                    .endDate(LocalDate.of(2026, 12, 15))
                    .status(true)
                    .image("https://res.cloudinary.com/ddwbopzwt/image/upload/v1741069433/voucher/yzmnf2d5zcaxolo4190l.jpg")
                    .publicId("voucher/yzmnf2d5zcaxolo4190l")
                    .build());
        }
    }
}
