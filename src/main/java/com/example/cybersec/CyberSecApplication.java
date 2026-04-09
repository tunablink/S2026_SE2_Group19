package com.example.cybersec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Điểm khởi chạy chính của ứng dụng Cybersecurity Learning Web App.
 */
@SpringBootApplication
@EnableScheduling
public class CyberSecApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyberSecApplication.class, args);
    }
}
