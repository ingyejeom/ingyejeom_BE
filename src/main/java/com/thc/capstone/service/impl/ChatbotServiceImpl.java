package com.thc.capstone.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thc.capstone.domain.Chatbot;
import com.thc.capstone.domain.UserSpace;
import com.thc.capstone.domain.UserSpaceStatus;
import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.repository.ChatbotRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.ChatbotService;
import com.thc.capstone.client.RagChatbotClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG 챗봇 시스템의 핵심 비지니스 로직을 담당하는 서비스 구현체입니다.
 * 클라이언트의 요청을 검증하고, 파이썬 AI 서버와의 통신 로직과 대화 기록 로직을 수행합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final RagChatbotClient ragChatbotClient;
    private final UserSpaceRepository userSpaceRepository;
    private final ChatbotRepository chatbotRepository;
    private final ObjectMapper objectMapper;

    /**
     * 사용자의 질문을 파이썬 챗봇 서버로 전달하고, 받은 응답을 DB에 저장합니다.
     * @param param 사용자의 질문(question)과 스페이스 ID(spaceId)
     * @param reqUserId 요청을 보낸 사용자의 식별 ID
     * @return 챗봇의 최종 응답 결과를 담은 ChatResDto
     */
    @Override
    @Transactional
    public ChatbotDto.ChatResDto askChatbot(ChatbotDto.ChatReqDto param, Long reqUserId) {
        log.info("[ChatbotRequest] userId: {}, spaceId: {}, question: {}", reqUserId, param.getSpaceId(), param.getQuestion());

        // 요청 파라미터의 유효성을 검사합니다.
        String query = param.getQuestion();
        if (query == null || query.isBlank()) {
            log.warn("[ChatbotRequest] Failed: Question is blank. userId: {}", reqUserId);
            throw new IllegalArgumentException("question is required");
        }
        Long spaceId = param.getSpaceId();
        if(spaceId == null || spaceId <= 0){
            log.warn("[ChatbotRequest] Failed: spaceId is invalid. userId: {}", reqUserId);
            throw new IllegalArgumentException("spaceId is invalid");
        }
        if (reqUserId == null) {
            log.warn("[ChatbotRequest] Failed: reqUserId is null.");
            throw new IllegalArgumentException("reqUserId is required");
        }

        // 권한 및 상태 검증 - 사용자가 해당 스페이스에 소속되어 있고, 상태가 활성(ACTIVE)인지 확인
        UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(reqUserId, spaceId, UserSpaceStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("[ChatbotRequest] Active userSpace not found! userId: {}, spaceId: {}", reqUserId, spaceId);
                    return new IllegalStateException("Active userSpace not found for userId=" + reqUserId);
                });


        try {
            // 파이썬 서버로 질의 요청 전송
            ChatbotDto.ChatResDto res = ragChatbotClient.ask(param);

            String sourcesJson = "[]";
            if (res.getSources() != null) {
                sourcesJson = objectMapper.writeValueAsString(res.getSources());
            }

            // 질의응답 기록(Chat History)을 데이터베이스에 저장
            Long userSpaceId = userSpace.getId();
            Chatbot saved = chatbotRepository.save(Chatbot.of(query, res.getAnswer(), userSpaceId, sourcesJson));
            log.info("[ChatbotResponse] Answer saved to DB. chatbotId: {}, userSpaceId: {}", saved.getId(), userSpaceId);

            // 클라이언트에게 반환할 응답 객체 생성 후 반환
            return res;
        } catch (Exception e) {
            log.error("[ChatbotRequest] Python API Communication Error: {}", e.getMessage(), e);
            try {
                throw e;
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * 파일 업로드 시 파이썬 RAG 서버로 벡터화(Ingest) 작업을 비동기(Async)로 요청합니다.
     * 파일 용량이 클 경우 파이썬 서버 처리 시간이 길어질 수 있으므로, 클라이언트의 응답 지연을 막기 위해 사용됩니다.
     * @param param 파일과 스페이스 ID를 담은 Dto
     * @param reqUserId 요청을 보낸 사용자의 ID
     */
    @Override
    @Async
    public void ingestRequest(ChatbotDto.IngestReqDto param, Long reqUserId) {
        log.info("[IngestRequest] Starting async ingest. spaceId: {}, fileName: {}", param.getSpaceId(), param.getFileName());
        try {
            // 파이썬 서버의 /ingest 엔드포인트로 파일 전송
            ragChatbotClient.ingest(param, reqUserId);
            log.info("[IngestRequest] Ingest request sent successfully. spaceId={}", param.getSpaceId());
        } catch (Exception e) {
            log.warn("[IngestRequest] HTTP timeout or communication issue! spaceId={}", param.getSpaceId(), e);
            log.info("파이썬 백그라운드 [Ingest] 작업 진행 중...");
        }
    }

    /**
     * 특정 스페이스의 과거 챗봇 대화 기록을 조회합니다.
     * @param spaceId 요청이 온 스페이스의 식별 ID
     * @param reqUserId 요청을 보낸 사용자의 식별 ID
     * @return 대화 기록 리스트
     */
    @Override
    public List<ChatbotDto.HistoryResDto> getHistory(Long spaceId, Long reqUserId) {
        log.info("[ChatHistory] Fetching history for userId: {}, spaceId: {}", reqUserId, spaceId);

        // 해당 스페이스에 대한 사용자 접근 권한 검증
        UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(reqUserId, spaceId, UserSpaceStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("[ChatHistory] Active userSpace not found! userId: {}, spaceId: {}", reqUserId, spaceId);
                    return new IllegalStateException("Active userSpace not found for userId=" + reqUserId);
                });

        // DB에서 생성일자(CreatedAt) 오름차순으로 대화 기록을 가져옴
        List<Chatbot> chatHistory = chatbotRepository.findByUserSpaceIdOrderByCreatedAtAsc(userSpace.getId());
        log.info("[ChatHistory] Found {} records for userSpaceId: {}", chatHistory.size(), userSpace.getId());

        // 엔티티(Entity) 리스트를 클라이언트 반환용 DTO 리스트로 변환(Map)하여 반환
        return chatHistory.stream().map(chat -> chat.toHistoryResDto(objectMapper)).collect(Collectors.toList());
    }


}
