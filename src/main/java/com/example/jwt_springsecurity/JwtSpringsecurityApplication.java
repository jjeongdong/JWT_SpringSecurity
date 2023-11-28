package com.example.jwt_springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JwtSpringsecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtSpringsecurityApplication.class, args);
    }

}
