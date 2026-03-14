package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SpaceService {
    /**
     * 스페이스 생성
     * @param param 스페이스 생성 데이터 (업무명, 스페이스 코드, 그룹 ID, 유저 이메일(선택))
     * @param reqUserId 요청한 사용자 ID
     * @return DB에 저장된 스페이스의 고유 ID
     */
    DefaultDto.CreateResDto create(SpaceDto.CreateReqDto param, Long reqUserId);

    /**
     * 스페이스 정보 수정
     * @param param 수정 가능한 스페이스 정보 (업무명(선택))
     * @param reqUserId 요청한 사용자 ID
     */
    void update(SpaceDto.UpdateReqDto param, Long reqUserId);

    /**
     * 스페이스 삭제 (Soft Delete)
     * @param param 삭제할 스페이스 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void delete(SpaceDto.UpdateReqDto param, Long reqUserId);

    /**
     * 스페이스 상세 정보
     * @param param 조회할 스페이스의 ID
     * @param reqUserId 요청한 사용자 ID
     * @return 스페이스의 상세 데이터 (업무명, 스페이스 코드, 그룹 이름)
     */
    SpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    /**
     * 스페이스 조회
     * @param param 필터 검색 조건 (업무명)
     * @param reqUserId 요청한 사용자 ID
     * @return 스페이스의 상세 데이터 리스트 (업무명, 스페이스 코드, 그룹 이름)
     */
    List<SpaceDto.DetailResDto> list(SpaceDto.ListReqDto param, Long reqUserId);
}
