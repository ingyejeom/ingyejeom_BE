package com.thc.capstone.dto;

import com.thc.capstone.domain.ApprovalRole;
import com.thc.capstone.domain.UserApproval;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

public class UserApprovalDto {
    /**
     * REQUEST
     * 유저-서명 생성 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class CreateReqDto {
        ApprovalRole approvalRole;
        Long userId;
        Long approvalId;

        public UserApproval toEntity(){
            return UserApproval.of(getApprovalRole(), getUserId(), getApprovalId());
        }
    }

    /**
     * REQUEST
     * 유저-서명 수정 데이터
     */
    @Getter @Setter @NoArgsConstructor @SuperBuilder
    // @AllArgsConstructor
    // 필요한 필드가 없으므로 일단 주석
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {

    }

    /**
     * RESPONSE
     * 유저-서명 상세 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        ApprovalRole approvalRole;
        Long userId;
        Long approvalId;
    }

    /**
     * REQUEST
     * 유저-서명 목록 조회 시 검색 데이터
     */
    @Getter @Setter @NoArgsConstructor @SuperBuilder
    // @AllArgsConstructor
    // 필요한 필드가 없으므로 일단 주석
    public static class ListReqDto extends DefaultDto.ListReqDto {

    }
}
