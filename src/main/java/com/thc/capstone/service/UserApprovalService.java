package com.thc.capstone.service;

import com.thc.capstone.dto.UserApprovalDto;
import com.thc.capstone.dto.DefaultDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserApprovalService {
    /**
     * 유저-서명 생성
     * @param param 유저-서명 데이터 (유저-서명 ID)
     * @param reqUserId 요청한 사용자 ID
     * @return DB에 저장된 유저-서명의 고유 ID
     */
    DefaultDto.CreateResDto create(UserApprovalDto.CreateReqDto param, Long reqUserId);

    /**
     * 유저-서명 정보 수정
     * @param param 수정 가능한 유저-서명 정보 (없음)
     * @param reqUserId 요청한 사용자 ID
     */
    void update(UserApprovalDto.UpdateReqDto param, Long reqUserId);

    /**
     * 유저-서명 삭제 (Soft Delete)
     * @param param 삭제할 유저-서명 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void delete(UserApprovalDto.UpdateReqDto param, Long reqUserId);

    /**
     * Approval 삭제 시 할당된 UserApproval 삭제
     * @param approvalId 삭제할 서명 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void deleteByApprovalId(Long approvalId, Long reqUserId);

    /**
     * 유저-서명 상세 정보
     * @param param 조회할 유저-서명의 ID
     * @param reqUserId 요청한 사용자 ID
     * @return 유저-서명의 상세 데이터 (인수인계 시 역할, 유저 ID, 서명 테이블 ID)
     */
    UserApprovalDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    /**
     * 유저-서명 조회
     * @param param 필터 검색 조건 (진행 상태)
     * @param reqUserId 요청한 사용자 ID
     * @return 유저-서명의 상세 데이터 리스트 (진행 상태, 스페이스 ID)
     */
    List<UserApprovalDto.DetailResDto> list(UserApprovalDto.ListReqDto param, Long reqUserId);
}
