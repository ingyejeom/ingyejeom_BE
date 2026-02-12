package com.thc.capstone.client;

import com.thc.capstone.dto.ChatbotDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RagChatbotClient {
    private final RestTemplate pythonRestTemplate;

    @Value("${chatbot.python.default-space-id:default}")
    private String defaultSpaceId;

    public String ask(ChatbotDto.ChatReqDto param) {
        String question = (param == null) ? null : param.getQuestion();
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question is empty");
        }

        Map<String, Object> body = Map.of(
                "question", question,
                "space_id", defaultSpaceId
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
}
