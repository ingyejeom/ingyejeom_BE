package com.thc.capstone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 파이썬 RAG 챗봇 서버와의 원활한 HTTP 통신을 위한 설정 클래스입니다.
 * 챗봇의 답변 생성 시간을 고려하여 타임아웃(Timeout)을 설정했습니다.
 */
@Configuration
public class ChatbotHttpConfig {

    /**
     * 파이썬 서버 전용 RestTemplate 빈(Bean)을 생성합니다.
     * application.yaml에 설정된 URL과 타임아웃 값을 환경변수로 주입받아 사용합니다.
     * @param builder 스프링 부트가 제공하는 기본 RestTemplate 빌더
     * @param baseUrl 파이썬 챗봇 서버의 기본 주소
     * @param timeoutMs 타임아웃 시간
     * @return 커스텀 타임아웃 및 기본 주소가 적용된 RestTemplate 객체
     */
    @Bean
    public RestTemplate pythonRestTemplate(
            RestTemplateBuilder builder,
            @Value("${chatbot.python.base-url}") String baseUrl,
            @Value("${chatbot.python.timeout-ms:25000}") long timeoutMs
    ) {
        // 기본 HTTP 클라이언트를 생성합니다.
        // 파이썬 서버와 최초로 연결을 맺는 데 최대 시간을 5초로 제한합니다.
        // 5초 내에 연결되지 않으면 서버가 다운된 것으로 간주하고 빠르게 에러를 발생시킵니다.
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(5000))
                .build();

        //스프링의 RestTemplate이 위에서 만든 커스텀 HttpClient를 사용할 수 있도록 Factory로 감싸줍니다.
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);

        // 연결은 성공했지만, 파이썬 서버가 답변을 다 만들어서 응답을 줄 때까지 기다리는 최대 시간입니다.
        factory.setReadTimeout(Duration.ofMillis(timeoutMs));

        // RestTemplate을 빌드하여 Spring Bean으로 등록합니다.
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                .requestFactory(() -> factory)
                .build();
    }
}
