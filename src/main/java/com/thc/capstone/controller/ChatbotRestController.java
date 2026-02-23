package com.thc.capstone.controller;

import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
@RestController
public class ChatbotRestController {
    private final ChatbotService chatbotService;

    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }
        return null;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<ChatbotDto.ChatResDto> askChatbot(@RequestBody ChatbotDto.ChatReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(chatbotService.askChatbot(param, getUserId(principalDetails)));
    }

    @GetMapping("/history/{spaceId}")
    public ResponseEntity<List<ChatbotDto.HistoryResDto>> getChatHistory(@PathVariable("spaceId") Long spaceId, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<ChatbotDto.HistoryResDto> history = chatbotService.getHistory(spaceId, getUserId(principalDetails));
        return ResponseEntity.ok(history);

    }
}
