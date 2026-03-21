package com.thc.capstone.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thc.capstone.dto.ChatbotDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 챗봇 질의응답을 위한 JPA Entity 입니다.
 */
@Slf4j
@Entity @Getter @Setter
public class Chatbot extends AuditingFields {

    // 질문과 답변이 길어질 수 있어 TEXT 타입으로 DB Column을 지정합니다.
    @Column(columnDefinition = "TEXT")
    String question;
    @Column(columnDefinition = "TEXT")
    String answer;
    // 챗봇이 어느 스페이스에서 작동하는지 식별하기 위한 FK (*로직에서 사용되는 방식 통일/수정 필요)
    Long userSpaceId;
    // 파이썬이 넘겨준 SourceInfoDto 배열을 JSON 문자열로 저장하기 위함
    @Column(columnDefinition = "TEXT")
    private String sourcesJson;

    /**
     * JPA에서 기본적으로 요구되는 파라미터 없는 기본 생성자입니다.
     * 외부에서의 빈 객체 생성을 막기 위해 protected를 사용했습니다
     */
    protected Chatbot() {}

    /**
     * 필드 초기화를 위한 private 생성자로, of 메서드를 통해서만 가능하도록 제한합니다.
     */
    private Chatbot(String question, String answer, Long userSpaceId, String sourcesJson) {
        this.question = question;
        this.answer = answer;
        this.userSpaceId = userSpaceId;
        this.sourcesJson = sourcesJson;
    }

    /**
     * Chatbot 객체 생성을 위한 정적 팩토리 메서드입니다.
     */
    public static Chatbot of (String question, String answer, Long userSpaceId, String sourcesJson) {
        return new Chatbot(question, answer, userSpaceId, sourcesJson);
    }

    /**
     * Entity 객체를 HistoryRestDto로 변환하여 반환합니다.
     */
    public ChatbotDto.HistoryResDto toHistoryResDto(ObjectMapper objectMapper) {
        List<ChatbotDto.SourceInfoDto> sourceList = null;

        try {
            if (this.sourcesJson != null && !this.sourcesJson.equals("[]")) {
                // JSON 문자열을 다시 List<SourceInfoDto>로 변환
                sourceList = objectMapper.readValue(this.sourcesJson, new TypeReference<List<ChatbotDto.SourceInfoDto>>() {});
            }
        } catch (Exception e) {
            // 파싱 실패 시 로그만 남기고 null 처리 (기존 데이터 호환성을 위해)
            log.warn("[toHistoryResDto] Failed to parse sourcesJson for legacy data. Chatbot ID: {}, Cause: {}", this.id, e.getMessage());
        }

        return ChatbotDto.HistoryResDto.builder()
                .id(this.getId())
                .question(this.question)
                .answer(this.answer)
                .createdAt(this.getCreatedAt() != null ? this.getCreatedAt().toString() : "")
                .sources(sourceList)
                .build();
    }
}
