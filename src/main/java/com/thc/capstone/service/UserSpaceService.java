package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserSpaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserSpaceService {
    /**
     * 스페이스 참여
     * @param param 참여에 필요한 데이터 (스페이스 코드)
     * @param reqUserId 요청한 사용자 ID
     */
    void join(UserSpaceDto.JoinReqDto param, Long reqUserId);

    /**
     * 스페이스 초대
     * @param param 초대에 필요한 데이터 (이메일, 스페이스 ID)
     * @param reqUserId 요청한 사용자 ID
     */
    void invite(UserSpaceDto.InviteReqDto param, Long reqUserId);

    /**
     * 유저-스페이스 관계 생성
     * @param param 관계 생성에 필요한 데이터 (역할, 상태, 유저 ID, 스페이스 ID)
     * @return DB에 저장된 유저-스페이스의 고유 ID
     */
    DefaultDto.CreateResDto create(UserSpaceDto.CreateReqDto param);

    /**
     * 유저-스페이스 관계 정보 수정
     * @param param 관계 정보 중 수정할 데이터 (역할, 상태)
     */
    void update(UserSpaceDto.UpdateReqDto param);

    /**
     * 유저-스페이스 관계 삭제
     * @param param 삭제할 유저-스페이스 ID
     */
    void delete(UserSpaceDto.UpdateReqDto param);

    /**
     * 특정 유저가 탈퇴할 때 해당 유저의 모든 유저-스페이스 관계 삭제
     * @param userId 삭제할 유저의 ID
     */
    void deleteByUserId(Long userId);

    /**
     * 특정 스페이스가 삭제될 때 해당 스페이스의 모든 유저-스페이스 관계 삭제
     * @param spaceId 삭제할 스페이스의 ID
     */
    void deleteBySpaceId(Long spaceId);

    /**
     * 유저-스페이스 상세 정보
     * @param param 조회핧 유저-스페이스 ID
     * @return 유저-스페이스의 상세 데이터 (역할, 상태, 유저 ID, 스페이스 ID, 그룹 ID, 그룹 이름, 업무 이름, 스페이스 코드)
     */
    UserSpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param);

    /**
     * 기본 유저-스페이스 목록 조회
     * @param param 조회 필터 DTO (요청한 유저 ID, 상태, 역할, 속해있는 그룹)
     */
    List<UserSpaceDto.DetailResDto> list(UserSpaceDto.ListReqDto param);

    /**
     * 프로필에서 본인이 속한 그룹 및 스페이스를 모두 띄우기
     * @param param 필터 검색 조건 (요청한 유저 ID)
     * @param reqUserId 요청한 사용자 ID
     * @return 유저-스페이스의 상세 데이터 리스트 (역할, 상태, 유저 ID, 스페이스 ID, 그룹 ID, 그룹 이름, 업무 이름, 스페이스 코드)
     */
    List<UserSpaceDto.DetailResDto> getProfileSpaces(UserSpaceDto.ListReqDto param, Long reqUserId);

    /**
     * 대시보드에 현재 사용자가 속한 스페이스 조회
     * @param reqUserId 요청한 사용자 ID
     * @return 현재 접속 중인 사용자가 USER 로 존재하는 스페이스 리스트
     */
    List<UserSpaceDto.DetailResDto> getDashboardSpaces(Long reqUserId);

    /**
     * 그룹 관리에 띄울 스페이스 조회
     * @param param 필터 검색 조건 (속해있는 그룹)
     * @param reqUserId 요청한 사용자 ID
     * @return 요청하는 그룹에서 사용자가 ADMIN 으로 존재하는 스페이스 리스트
     */
    List<UserSpaceDto.DetailResDto> getAdminSpaces(UserSpaceDto.ListReqDto param, Long reqUserId);
}
