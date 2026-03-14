package com.thc.capstone;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

/* 파이썬 RAG 서버로 파일 임베딩(Ingest) 요청과 같이 시간이 오래 걸리는 작업을
백그라운드에서 비동기(@Async)로 처리할 수 있도록 스프링의 비동기 기능을 활성화합니다. (현재는 ingest만 비동기 해당)*/
@EnableAsync
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
