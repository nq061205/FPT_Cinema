package com.group6.mvc.fpt_cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FptCinemaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FptCinemaApplication.class, args);
    }

}
