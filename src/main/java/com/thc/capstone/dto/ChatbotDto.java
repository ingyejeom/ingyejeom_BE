package com.thc.capstone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 챗봇 기능과 관련된 DTO
 * ChatbotDto 내부에서 Static 클래스로 그룹화하여 관리합니다.
 */
public class ChatbotDto {

    // 클라이언트가 챗봇에게 질문할 때 사용하는 ReqDto
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ChatReqDto {
        private String question;
        private Long spaceId;
    }

    // 챗봇의 답변을 클라이언트에게 반환할 때 사용하는 ResDto
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ChatResDto {
        private String answer;
//        @JsonProperty("time_taken")
//        private Double timeTaken;
        private List<SourceInfoDto> sources;

    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class SourceInfoDto {
        private String source;
//        private String snippet;
        private Integer page;
    }

    // 파이썬 RAG 서버로 파일을 전송하여 벡터 임베딩(Ingest)을 요청할 때 사용하는 ReqDto
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class IngestReqDto {
        private Long spaceId;
        private byte[] fileBytes;
        private String fileName;
    }

    // 챗봇의 과거 대화 내역을 클라이언트에게 전달할 때 사용하는 ResDto
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class HistoryResDto {
        private Long id;
        private String question;
        private String answer;
        private String createdAt;
        private List<SourceInfoDto> sources;
    }
}
