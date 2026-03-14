package com.thc.capstone.dto;

import com.thc.capstone.domain.Group;
import com.thc.capstone.domain.Space;
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
}
