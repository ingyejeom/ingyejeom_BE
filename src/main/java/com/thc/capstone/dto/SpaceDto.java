package com.thc.capstone.dto;

import com.thc.capstone.domain.Role;
import com.thc.capstone.domain.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class SpaceDto {
    /**
     * REQUEST
     * 스페이스 생성 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class CreateReqDto {
        String workName;
        String spaceCode;
        Long groupId; // (FK)

        String userEmail; // 이메일로 사용자를 조회하여 UserSpace 생성

        public Space toEntity(){
            return Space.of(getWorkName(), getSpaceCode(), getGroupId());
        }
    }

    /**
     * REQUEST
     * 스페이스 정보 수정 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        String workName;
    }

    /**
     * RESPONSE
     * 스페이스 상세 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        String workName;
        String spaceCode;

        String groupName;
    }

    /**
     * REQUEST
     * 스페이스 목록 조회 시 검색 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 검색 조건 : 업무명 (중간 글자 검색 가능)
         */
        String workName;
    }
}
