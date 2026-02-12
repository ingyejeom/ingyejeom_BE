package com.thc.capstone.service;

import com.thc.capstone.dto.ChatbotDto;
import org.springframework.stereotype.Service;

@Service
public interface ChatbotService {
    ChatbotDto.ChatResDto askChatbot(ChatbotDto.ChatReqDto param, Long reqUserId);
}
