package com.thc.capstone.client;

import com.thc.capstone.dto.ChatbotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.util.List;
import java.util.Map;

/**
 * 파이썬 기반의 RAG 챗봇 서버와 통신하기 위한 HTTP 클라이언트 컴포넌트입니다.
 * RestTemplate을 활용하여 질의응답 및 문서 벡터화 요청을 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class RagChatbotClient {

    // ChatbotHttpConfig에서 Bean으로 등록된 커스텀 RestTemplate을 주입받아 사용합니다.
    // 타임아웃 설정 및 기본 URL(application.yaml/chatbot.python.base-url)이 미리 세팅되어 있습니다.
    private final RestTemplate pythonRestTemplate;

    /**
     * 사용자 질의(Question)를 파이썬 챗봇 서버로 전송하고 응답을 받아옵니다.
     * @param param 사용자의 질문(question)과 스페이스 ID(spaceId)
     * @return 챗봇의 응답 결과 텍스트
     */
    public ChatbotDto.ChatResDto ask(ChatbotDto.ChatReqDto param) {
        // Map.of를 사용하여 불변(Immutable) 맵으로 안전하게 데이터를 구성합니다.
        Map<String, Object> body = Map.of(
                "question", param.getQuestion(),
                "space_id", String.valueOf(param.getSpaceId())
        );;

        // HTTP 헤더 설정 (JSON 타입 통신)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // 헤더와 바디를 결합하여 HTTP 엔티티 객체 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);


        try {
            // 파이썬 서버의 '/chatbot' 엔드포인트로 POST 요청 전송
            ResponseEntity<ChatbotDto.ChatResDto> res = pythonRestTemplate.exchange(
                    "/chatbot",
                    HttpMethod.POST,
                    entity,
                    ChatbotDto.ChatResDto.class
            );

            // 파이썬 서버의 '/chatbot' 엔드포인트로 POST 요청 전송
            // 2xx 성공 코드가 아니거나, 응답 바디/답변이 비어있으면 예외 처리
            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null || res.getBody().getAnswer() == null) {
                throw new IllegalStateException("Python chatbot returned empty response");
            }

            // 정상의 경우 챗봇의 답변 DTO 전체를 반환
            return res.getBody();

        } catch (HttpStatusCodeException e) { // 에러 발생 시 상세 내용과 함께 예외 처리
            throw new IllegalStateException("Python chatbot HTTP error: " + e.getStatusCode()
                    + ", body=" + e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) { // Timeout 혹은 서버 연결 안 되는 문제 발생 시 예외 처리
            throw new IllegalStateException("Python chatbot timeout/unreachable", e);
        }
    }

    /**
     * 파일을 파이썬 챗봇 서버로 전송하고 벡터화(Ingest)합니다.
     * @param param 파일과 스페이스 ID를 담은 Dto
     * @param reqUserId 요청을 보낸 사용자의 ID
     */
    public void ingest(ChatbotDto.IngestReqDto param, Long reqUserId) {
        // 바이트 배열을 전송 가능한 리소스로 변환
        ByteArrayResource resource = new ByteArrayResource(param.getFileBytes()) {
            @Override
            public String getFilename() {
                return param.getFileName();
            }
        };

        // MultipartFormData 요청 바디 생성
        // MultiValueMap을 사용하여 file, spaceId, userId를 폼 데이터로 매핑합니다.
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        body.add("space_id", param.getSpaceId().toString());
        body.add("user_id", reqUserId.toString());

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        // 파일 전송을 위한 멀티파트 폼 데이터 타입 지정
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("ngrok-skip-browser-warning", "true"); // [ngrok 사용 시] 발생하는 경고창을 우회하기 위한 커스텀 헤더

        // 파이썬 서버의 '/ingest' 엔드포인트로 파일 전송 (POST)
        // 응답 본문이 딱히 필요하지 않으므로 String.class로 받아 처리만 넘깁니다.
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        pythonRestTemplate.postForEntity("/ingest", entity, String.class);
    }
}
