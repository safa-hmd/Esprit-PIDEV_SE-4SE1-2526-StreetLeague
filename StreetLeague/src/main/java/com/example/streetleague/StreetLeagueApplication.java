package com.example.streetleague;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StreetLeagueApplication {


    public static void main(String[] args) {
        SpringApplication.run(StreetLeagueApplication.class, args);
    }

}
