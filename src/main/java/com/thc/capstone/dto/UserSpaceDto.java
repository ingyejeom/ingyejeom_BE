package com.thc.capstone.dto;

import com.thc.capstone.domain.Role;
import com.thc.capstone.domain.UserSpace;
import com.thc.capstone.domain.UserSpaceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

public class UserSpaceDto {
    /**
     * REQUEST
     * 스페이스 참여 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class JoinReqDto {
        private String spaceCode;
    }

    /**
     * REQUEST
     * 스페이스 초대 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class InviteReqDto {
        private String email;
        private Long spaceId;
    }

    /**
     * REQUEST
     * 사용자-스페이스 생성 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class CreateReqDto {
        /**
         * 사용자 역할
         * ADMIN : 그룹 관리자
         * USER : 스페이스 사용자
         */
        Role role;

        /**
         * 사용자 상태
         * INACTIVE : 비활성
         * ACTIVE : 활성
         *
         * USER && ACTIVE 는 한명만 존재
         */
        UserSpaceStatus status;

        Long userId;
        Long spaceId;

        public UserSpace toEntity(){
            return UserSpace.of(getRole(), getStatus(), getUserId(), getSpaceId());
        }
    }

    /**
     * REQUEST
     * 사용자-스페이스 정보 수정 데이터
     * 스페이스 인계 시 내부적 실행
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        Role role;
        UserSpaceStatus status;
    }

    /**
     * RESPONSE
     * 유저-스페이스 상세 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        Role role;
        UserSpaceStatus status;
        Long userId;
        Long spaceId;

        Long groupId;
        String groupName;

        String userName;
        // 스페이스의 현재 관리자 이름
        String adminName;

        String workName;
        String spaceCode;
    }

    /**
     * REQUEST
     * 사용자-스페이스 목록 조회 시 검색 데이터
     * 대시보드에 현재 USER 로 참여 중인 스페이스 목록 조회
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 검색 조건 : 요청한 유저 ID, 상태, 역할, 속해있는 그룹
         */
        @Schema(hidden = true)
        private Long reqUserId;

        UserSpaceStatus status;

        @Schema(hidden = true)
        Role role;

        Long groupId;
    }
}
