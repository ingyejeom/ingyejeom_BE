package com.thc.capstone.service;

import com.thc.capstone.dto.ChatbotDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatbotService {
    ChatbotDto.ChatResDto askChatbot(ChatbotDto.ChatReqDto param, Long reqUserId);
    void ingestRequest(Long spaceId, String filePath);
    List<ChatbotDto.HistoryResDto> getHistory(Long spaceId, Long reqUserId);
}
