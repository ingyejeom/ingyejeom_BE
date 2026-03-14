package com.thc.capstone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

// 접근 설정 및 헤더에 담을 내용 확인
@Configuration
public class CorsFilterConfiguration {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // 쿠키/인증 정보 허용
        config.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 출처 허용 (Credentials true일 때 안전한 방식)

        config.addAllowedMethod("*"); // 모든 HTTP 메서드(GET, POST 등) 허용
        config.addAllowedHeader("*"); // 💡 수정: 모든 요청 헤더(Content-Type 등) 허용 (덮어쓰지 않음!)

        // 💡 수정: 프론트엔드(React)에서 응답으로 받은 토큰을 읽을 수 있도록 '노출(Expose)' 설정
        config.setExposedHeaders(Arrays.asList("Authorization", "RefreshToken"));

        // "/api/**" 뿐만 아니라 혹시 모를 다른 경로를 위해 "/**"로 넉넉하게 잡는 것을 추천합니다.
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}