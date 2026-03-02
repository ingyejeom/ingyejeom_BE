package com.thc.capstone.domain;

import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.dto.DefaultDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Chatbot extends AuditingFields {

    @Column(columnDefinition = "TEXT")
    String question;
    @Column(columnDefinition = "TEXT")
    String answer;
    Long userSpaceId;

    protected Chatbot() {}
    private Chatbot(String question, String answer, Long userSpaceId) {
        this.question = question;
        this.answer = answer;
        this.userSpaceId = userSpaceId;
    }

    public static Chatbot of (String question, String answer, Long userSpaceId) {
        return new Chatbot(question, answer, userSpaceId);
    }

    public DefaultDto.CreateResDto toCreateResDto(){
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }

    public ChatbotDto.HistoryResDto toHistoryResDto() {
        return ChatbotDto.HistoryResDto.builder()
                .id(this.getId())
                .question(this.question)
                .answer(this.answer)
                .createdAt(this.getCreatedAt() != null ? this.getCreatedAt().toString() : "")
                .build();
    }
}
