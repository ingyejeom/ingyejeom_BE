package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {
    /**
     * 그룹 생성
     * @param param 그룹 생성 데이터 (그룹 이름, 스페이스 목록)
     * @param reqUserId 요청한 사용자 ID
     * @return DB에 저장된 그룹의 고유 ID
     */
    DefaultDto.CreateResDto create(GroupDto.CreateReqDto param, Long reqUserId);

    /**
     * 그룹 정보 수정
     * @param param 수정 가능한 그룹 정보 (그룹 이름)
     * @param reqUserId 요청한 사용자 ID
     */
    void update(GroupDto.UpdateReqDto param, Long reqUserId);

    /**
     * 그룹 삭제 (Soft Delete)
     * @param param 삭제할 그룹 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void delete(GroupDto.UpdateReqDto param, Long reqUserId);

    /**
     * 그룹 상세 정보
     * @param param 조회할 그룹의 ID
     * @param reqUserId 요청한 사용자 ID
     * @return 그룹의 상세 데이터 (그룹 이름)
     */
    GroupDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    /**
     * 그룹 조회
     * @param param 필터 검색 조건 (그룹 이름)
     * @param reqUserId 요청한 사용자 ID
     * @return 그룹의 상세 데이터 리스트 (그룹 이름)
     */
    List<GroupDto.DetailResDto> list(GroupDto.ListReqDto param, Long reqUserId);
    List<GroupDto.DetailResDto> scrollList(GroupDto.ScrollListReqDto param, Long reqUserId);

    /**
     * 프로필에서 본인이 속학 그룹을 모두 띄우기
     * @param param 필터 검색 조건 (그룹 이름)
     * @param reqUserId 요청한 사용자 ID
     * @return 사용자가 ADMIN 으로 존재하는 그룹 리스트
     */
    List<GroupDto.DetailResDto> getProfileGroups(GroupDto.ScrollListReqDto param, Long reqUserId);
}
