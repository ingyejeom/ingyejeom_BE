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

@Component
@RequiredArgsConstructor
public class RagChatbotClient {
    private final RestTemplate pythonRestTemplate;

    public String ask(ChatbotDto.ChatReqDto param, Long spaceId) {
        String question = (param == null) ? null : param.getQuestion();
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question is empty");
        }

        Map<String, Object> body = Map.of(
                "question", question,
                "space_id", String.valueOf(spaceId)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);


        try {
            ResponseEntity<ChatbotDto.ChatResDto> res = pythonRestTemplate.exchange(
                    "/chatbot",
                    HttpMethod.POST,
                    entity,
                    ChatbotDto.ChatResDto.class
            );

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null || res.getBody().getAnswer() == null) {
                throw new IllegalStateException("Python chatbot returned empty response");
            }
            return res.getBody().getAnswer();

        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException("Python chatbot HTTP error: " + e.getStatusCode()
                    + ", body=" + e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            throw new IllegalStateException("Python chatbot timeout/unreachable", e);
        }
    }

//    public void ingest(Long spaceId, String filePath) {
//        Map<String, Object> body = Map.of(
//                "space_id", spaceId.toString(),
//                "file_path", filePath
//        );
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        try {
//            pythonRestTemplate.exchange("/ingest", HttpMethod.POST, entity, String.class);
//        } catch (Exception e) {
//            System.out.println("FastAPI 422 에러 상세 내용: " + e);
//            throw new IllegalStateException("Python ingest request failed", e);
//        }
//    }

    public void ingest(Long userId, Long spaceId, byte[] fileBytes, String fileName) {
        // 바이트 배열을 전송 가능한 리소스로 변환
        ByteArrayResource resource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        body.add("space_id", spaceId.toString());
        body.add("user_id", userId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("ngrok-skip-browser-warning", "true");

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        pythonRestTemplate.postForEntity("/ingest", entity, String.class);
    }
}
