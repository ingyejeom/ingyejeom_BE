package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.HandoverDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 인수인계 문서 비즈니스 로직 인터페이스
 *
 * 인수인계 문서의 CRUD 및 폴더 관리 기능을 정의한다.
 * 모든 메서드는 reqUserId를 통해 요청자 권한을 검증한다.
 */
@Service
public interface HandoverService {

    /**
     * 새 인수인계 문서를 생성한다.
     * 제목이 비어있으면 역할명을 기반으로 자동 생성된다.
     */
    DefaultDto.CreateResDto create(HandoverDto.CreateReqDto param, Long reqUserId);

    /**
     * 인수인계 문서의 제목, 역할, 내용을 수정한다.
     * null이 아닌 필드만 업데이트된다.
     */
    void update(HandoverDto.UpdateReqDto param, Long reqUserId);

    /**
     * 인수인계 문서를 논리 삭제한다.
     * deleted 플래그를 true로 설정하여 조회에서 제외한다.
     */
    void delete(HandoverDto.UpdateReqDto param, Long reqUserId);

    /**
     * 단일 인수인계 문서의 상세 정보를 조회한다.
     * 연관된 스페이스, 그룹, 작성자 정보가 포함된다.
     */
    HandoverDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    /**
     * 특정 스페이스의 루트 폴더에 있는 인수인계 문서 목록을 조회한다.
     */
    List<HandoverDto.DetailResDto> listBySpaceId(Long spaceId, Long reqUserId);

    /**
     * 특정 스페이스의 특정 폴더에 있는 인수인계 문서 목록을 조회한다.
     */
    List<HandoverDto.DetailResDto> listBySpaceIdAndFolderId(Long spaceId, Long folderId, Long reqUserId);

    /**
     * 인수인계 문서의 모듈 JSON 데이터만 업데이트한다.
     * 메타데이터(제목, 역할)는 변경되지 않는다.
     */
    void updateModules(Long id, String text, Long reqUserId);

    /**
     * 인수인계 문서를 다른 폴더로 이동한다.
     */
    void move(HandoverDto.MoveReqDto param, Long reqUserId);

    /**
     * 특정 UserSpace에 연결된 인수인계 문서를 조회한다.
     * 문서가 없으면 null을 반환한다.
     */
    HandoverDto.DetailResDto getByUserSpaceId(Long userSpaceId, Long reqUserId);
}
