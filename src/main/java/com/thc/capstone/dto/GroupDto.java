package com.thc.capstone.dto;

import com.thc.capstone.domain.Group;
import com.thc.capstone.domain.Role;
import com.thc.capstone.domain.Space;
import com.thc.capstone.domain.UserSpaceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class GroupDto {
    /**
     * REQUEST
     * 그룹 생성 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateReqDto {
        String groupName;

        List<SpaceInfo> spaces;

        /**
         * 그룹 생성 시 스페이스를 1개 이상 생성하기 위함
         */
        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class SpaceInfo {
            private String workName; // 각 스페이스 별 업무 이름
            private String userEmail; // 각 스페이스 별 할당할 유저 이메일
        }

        public Group toEntity(){
            return Group.of(getGroupName());
        }
    }

    /**
     * REQUEST
     * 그룹 정보 수정 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        String groupName;
    }

    /**
     * RESPONSE
     * 그룹 상세 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        String groupName;
    }

    /**
     * REQUEST
     * 그룹 목록 조회 시 검색 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 검색 조건 : 그룹 이름 (중간 글자 검색 가능)
         */
        String groupName;
    }

    /**
     * REQUEST
     * 그룹 목록 조회 시 검색 데이터
     * 대시보드에 현재 USER 로 참여 중인 스페이스 목록을 스크롤 리스트로 조회
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ScrollListReqDto extends DefaultDto.ScrollListReqDto {
        /**
         * 검색 조건 : 요청한 유저 ID, 상태, 역할, 그룹 이름
         */
        @Schema(hidden = true)
        private Long reqUserId;

        UserSpaceStatus status;

        @Schema(hidden = true)
        Role role;

        String groupName;
    }
}
