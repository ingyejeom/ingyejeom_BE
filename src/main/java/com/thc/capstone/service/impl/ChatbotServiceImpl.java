package com.thc.capstone.service.impl;

import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.service.ChatbotService;
import com.thc.capstone.client.RagChatbotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final RagChatbotClient ragChatbotClient;

    @Override
    @Transactional
    public ChatbotDto.ChatResDto askChatbot(ChatbotDto.ChatReqDto param, Long reqUserId) {

        String query = param.getQuestion();
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("question is required");
        }
        if (reqUserId == null) {
            throw new IllegalArgumentException("reqUserId is required");
        }

//        // 1) userSpaceId 확보
//        Long userSpaceId = userSpaceRepository.findDefaultUserSpaceIdByUserId(reqUserId)
//                .orElseThrow(() -> new IllegalStateException("Default userSpaceId not found for userId=" + reqUserId));

        String answer = ragChatbotClient.ask(param);

//        // 3) 로그 저장
//        Chatbot saved = chatbotRepository.save(Chatbot.of(query, answer, userSpaceId));

        // 4) 응답
        return ChatbotDto.ChatResDto.builder()
                .answer(answer)
                .build();
    }
}
