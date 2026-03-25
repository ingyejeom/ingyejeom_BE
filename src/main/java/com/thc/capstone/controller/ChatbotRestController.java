package com.thc.capstone.controller;

import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RAG 챗봇 시스템의 질의응답 및 대화 기록 조회를 처리하는 REST Controller 입니다.
 * 클라이언트의 HTTP 요청을 ChatbotService로 전달하고 결과를 반환합니다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
@RestController
public class ChatbotRestController {
    private final ChatbotService chatbotService;

    /**
     * Spring Security의 PrincipalDetails에서 사용자의 ID를 추출하는 유틸리티 메서드 입니다.
     * @param principalDetails 사용자의 인증 정보
     * @return 사용자 ID (인증 정보가 없거나 유효하지 않은 경우 null)
     */
    private Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }
        return null;
    }

    /**
     * 사용자의 챗봇 질문을 처리하고 답변을 반환합니다.
     * @param param 클라이언트가 Request Body로 전달한 챗봇 질문과 스페이스 정보
     * @param principalDetails 현재 로그인 사용자의 정보
     * @return HTTP 상태코드와 응답 결과
     */
    @PreAuthorize("hasRole('USER')") // 인가된 일반 회원(USER)만 접근 가능
    @Operation(summary = "챗봇 질문하기", description = "사용자의 질문을 파이썬 RAG 챗봇 서버로 전달하고, 답변을 받아 반환합니다.")
    @PostMapping("")
    public ResponseEntity<ChatbotDto.ChatResDto> askChatbot(@RequestBody ChatbotDto.ChatReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 서비스 계층으로 비즈니스 로직을 넘기고, 결과를 ResponseEntity로 감싸서 반환합니다.
        return ResponseEntity.ok(chatbotService.askChatbot(param, getUserId(principalDetails)));
    }

    /**
     * 특정 스페이스의 과거 챗봇 대화 기록을 조회합니다.
     * Restful API 규칙을 따라 spaceId를 PathVariable로 받습니다.
     * @param param 챗봇의 대화 내역을 불러오기 위한 요청 DTO
     * @param principalDetails 사용자의 인증 정보
     * @return HTTP 상태코드와 응답 결과 List
     */
    @Operation(summary = "챗봇 대화 기록 조회", description = "특정 스페이스 내에서 이루어진 과거의 대화 내역을 시간순으로 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<List<ChatbotDto.HistoryResDto>> getChatHistory(@ModelAttribute ChatbotDto.HistoryReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // DB에 저장된 해당 스페이스의 챗봇 히스토리를 리스트 형태로 조회 후 ResponseEntity로 감싸서 반환합니다.
        return ResponseEntity.ok(chatbotService.getHistory(param, getUserId(principalDetails)));

    }
}
