package com.example.booking_movie.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "ddwbopzwt",
                "api_key", "729265288257994",
                "api_secret", "Zia31XJWH50R-6TCC-BWkzv9mf0"));
    }
}
