package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.ApprovalDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ApprovalService {
    /**
     * 인계 시작
     * @param param 초대에 필요한 데이터 (이메일, 스페이스 ID)
     * @param reqUserId 요청한 사용자 ID
     */
    void startHandover(ApprovalDto.InviteReqDto param, Long reqUserId);

    /**
     * 서명 테이블 생성
     * @param param 서명 테이블 데이터 (서명 테이블 ID)
     * @param reqUserId 요청한 사용자 ID
     * @return DB에 저장된 서명테이블의 고유 ID
     */
    DefaultDto.CreateResDto create(ApprovalDto.CreateReqDto param, Long reqUserId);

    /**
     * 서명 시 다음 단계로 변경
     * @param param 서명 테이블 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void sign(ApprovalDto.UpdateReqDto param, Long reqUserId);

    /**
     * 서명 테이블 취소 (UserApproval 도 연쇄 삭제)
     * @param param 서명 테이블 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void cancel(ApprovalDto.UpdateReqDto param, Long reqUserId);

    /**
     * 서명 테이블 정보 수정
     * @param param 수정 가능한 서명 테이블 정보 (진행 상태)
     * @param reqUserId 요청한 사용자 ID
     */
    void update(ApprovalDto.UpdateReqDto param, Long reqUserId);

    /**
     * 서명 테이블 삭제 (Soft Delete)
     * @param param 삭제할 서명 테이블 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void delete(ApprovalDto.UpdateReqDto param, Long reqUserId);

    /**
     * 서명 테이블 상세 정보
     * @param param 조회할 서명 테이블의 ID
     * @param reqUserId 요청한 사용자 ID
     * @return 서명 테이블의 상세 데이터 (진행 상태, 스페이스 ID)
     */
    ApprovalDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    /**
     * 서명 테이블 조회
     * @param param 필터 검색 조건 (진행 상태)
     * @param reqUserId 요청한 사용자 ID
     * @return 서명 테이블의 상세 데이터 리스트 (진행 상태, 스페이스 ID)
     */
    List<ApprovalDto.DetailResDto> list(ApprovalDto.ListReqDto param, Long reqUserId);
}
