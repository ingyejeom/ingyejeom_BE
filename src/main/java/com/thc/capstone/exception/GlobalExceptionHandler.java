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

    // 2. 그 외 일반적인 런타임 예외 처리 (권한 에러, 데이터 없음 등)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}