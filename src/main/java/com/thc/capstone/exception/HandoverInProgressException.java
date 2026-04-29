package com.thc.capstone.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 인수인계 중 스페이스 변경 시도를 막기 위한 예외 처리
 * HttpStatus 403 (Forbidden)
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
@SuppressWarnings("serial")
@NoArgsConstructor
public class HandoverInProgressException extends RuntimeException {
    public HandoverInProgressException(String message) {
        super(message);
    }
}
