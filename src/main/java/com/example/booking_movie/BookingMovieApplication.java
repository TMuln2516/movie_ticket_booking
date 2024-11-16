package com.example.booking_movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingMovieApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookingMovieApplication.class, args);
	}
}