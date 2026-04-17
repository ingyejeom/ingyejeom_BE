package com.thc.capstone.dto;

import com.thc.capstone.domain.Approval;
import com.thc.capstone.domain.StepStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

public class ApprovalDto {
    /**
     * REQUEST
     * 서명테이블 생성 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class CreateReqDto {
        Long spaceId;

        Long assigneeId;

        public Approval toEntity(){
            return Approval.of(StepStatus.ASSIGNOR_TURN, getSpaceId());
        }
    }

    /**
     * REQUEST
     * 서명 테이블 수정 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        StepStatus stepStatus;
    }

    /**
     * RESPONSE
     * 서명 테이블 상세 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        StepStatus stepStatus;
        Long spaceId;

        Long assignorId;
        Long assigneeId;
        Long adminId;
    }

    /**
     * REQUEST
     * 서명 테이블 목록 조회 시 검색 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 검색 조건 : 진행 상태
         */
        StepStatus stepStatus;
    }
}
