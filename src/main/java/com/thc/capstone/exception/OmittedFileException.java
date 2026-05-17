package com.thc.capstone.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  인계자가 업로드한 파일 중 인수인계서에 첨부(연결)되지 않은 누락 파일이 있을 때 사용되는 예외처리
 *  HttpStatus 400 (Bad Request)
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST)
@SuppressWarnings("serial")
@NoArgsConstructor
public class OmittedFileException extends RuntimeException {

    public OmittedFileException(String message) {
        super(message);
    }

}