package com.thc.capstone;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CapstoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapstoneApplication.class, args);
    }

    // SecurityConfig에서 ObjectMapper에 Bean이 생성이 안되어서 수동으로 Bean을 등록합니다
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
