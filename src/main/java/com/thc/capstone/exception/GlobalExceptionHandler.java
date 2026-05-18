package com.thc.capstone.exception; // 본인의 패키지 경로에 맞게 수정하세요

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 인수인계 진행 중 발생한 커스텀 예외 처리
    @ExceptionHandler(HandoverInProgressException.class)
    public ResponseEntity<Map<String, String>> handleHandoverInProgressException(HandoverInProgressException e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage()); // 백엔드에서 작성한 메시지 삽입

        // 403 Forbidden 대신 상황에 맞게 400 Bad Request 또는 409 Conflict 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 인가 실패 (권한 없음) 예외 처리 (SpaceSecurityChecker 및 GroupSecurityChecker 등에서 false 반환 시 발생)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e, jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/group") || requestURI.contains("/getAdminSpaces")) {
            response.put("message", "해당 그룹의 관리에 접근할 권한이 없습니다.");
        } else {
            response.put("message", "다른 스페이스에는 접근이 제한됩니다.");
        }
        
        response.put("error", "ACCESS_DENIED");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 그 외 일반적인 런타임 예외 처리 (권한 에러, 데이터 없음 등)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}