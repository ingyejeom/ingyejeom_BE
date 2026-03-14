package com.thc.capstone.dto;

import com.thc.capstone.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

public class UserDto {
    /**
     * REQUEST
     * 로그인 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginReqDto {
        String username;
        String password;
    }

    /**
     * REQUEST
     * 사용자 생성 데이터 (신규 회원가입)
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateReqDto {
        String username; // 로그인 ID
        String password;
        String name; // 사용자 실명
        String email;

        public User toEntity(){
            return User.of(getUsername(), getPassword(), getName(), getEmail());
        }
    }

    /**
     * REQUEST
     * 사용자 정보 수정 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        String password;
        String name;
        String email;
    }

    /**
     * RESPONSE
     * 사용자 상세 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        String username;
        String name;
        String email;
    }

    /**
     * REQUEST
     * 사용자 목록 조회 시 검색 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 검색 조건 : 사용자 이름 (중간 글자 검색 가능)
         */
        String name;
    }
}
