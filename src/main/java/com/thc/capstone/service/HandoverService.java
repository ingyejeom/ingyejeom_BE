package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.HandoverDto;
import org.springframework.stereotype.Service;

import java.util.List;

// 인수인계 관련 비즈니스 로직을 정의하는 인터페이스 (실제 구현은 HandoverServiceImpl에서)
@Service
public interface HandoverService {

    // 새 인수인계 문서 생성
    DefaultDto.CreateResDto create(HandoverDto.CreateReqDto param, Long reqUserId);

    // 인수인계 문서 수정
    void update(HandoverDto.UpdateReqDto param, Long reqUserId);

    // 인수인계 문서 삭제 (실제로 지우지 않고 deleted=true로 표시)
    void delete(HandoverDto.UpdateReqDto param, Long reqUserId);

    // 인수인계 문서 1개 상세 조회
    HandoverDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    // 특정 스페이스의 모든 인수인계 문서 목록 조회 (루트 폴더)
    List<HandoverDto.DetailResDto> listBySpaceId(Long spaceId, Long reqUserId);

    // 특정 스페이스와 폴더의 인수인계 문서 목록 조회
    List<HandoverDto.DetailResDto> listBySpaceIdAndFolderId(Long spaceId, Long folderId, Long reqUserId);

    // 인수인계 문서의 모듈 데이터(JSON)만 업데이트
    void updateModules(Long id, String text, Long reqUserId);

    // 인수인계 문서를 다른 폴더로 이동
    void move(HandoverDto.MoveReqDto param, Long reqUserId);
}
