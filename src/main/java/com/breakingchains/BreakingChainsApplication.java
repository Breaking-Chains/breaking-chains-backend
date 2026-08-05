package com.breakingchains;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BreakingChainsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BreakingChainsApplication.class, args);
    }
}
