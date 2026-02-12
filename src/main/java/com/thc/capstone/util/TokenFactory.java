package com.thc.capstone.util;

import com.thc.capstone.domain.RefreshToken;
import com.thc.capstone.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class TokenFactory {
    final RefreshTokenRepository refreshTokenRepository;

    static int refreshTokenValidityHour = 12;
    static int accessTokenValidityHour = 1;

    // 토큰 생성 공통 함수
    public String createToken(Long userId, int termHour) {
        LocalDateTime now = LocalDateTime.now();

        now = now.plusHours(termHour);

        String token = null;

        String info = userId + "_" + now;

        try {
            token = AES256Cipher.AES_Encode(null, info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return token;
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTokenValidityHour);
    }

    // 엑세스 토큰 생성
    public String createAccessToken(String refreshToken) {
        Long userId = validateToken(refreshToken);

        RefreshToken entity = refreshTokenRepository.findByContent(refreshToken)
                .orElse(null);

        if(entity == null) {
            return null;
        }

        Long userIdFormToken = entity.getUserId();

        if(!userIdFormToken.equals(userId)) {
            return null;
        }

        if(userId == null) {
            return null;
        }

        return createToken(userId, accessTokenValidityHour);
    }

    // 리프레시 토큰 복호화
    public Long validateToken(String token) {
        String info = null;

        try {
            info = AES256Cipher.AES_Decode(null, token);

            String[] array_info = info.split("_");
            Long userId = Long.parseLong(array_info[0]);

            LocalDateTime now = LocalDateTime.now();
            String due = array_info[1];
            String nowTime = now.toString();

            // 만료 여부 확인
            // Sort를 통해서 만료시간보다 현재시간이 더 앞에 있는지를 파악
            String[] tempArray = {due, nowTime};
            Arrays.sort(tempArray);

            if(nowTime.equals(tempArray[0])) {
                return userId;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
