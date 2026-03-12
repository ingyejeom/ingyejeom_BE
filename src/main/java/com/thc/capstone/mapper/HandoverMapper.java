package com.thc.capstone.mapper;

import com.thc.capstone.dto.HandoverDto;

import java.util.List;

/**
 * 인수인계 문서 MyBatis 매퍼 인터페이스
 *
 * 여러 테이블 JOIN이 필요한 복잡한 조회 쿼리를 처리한다.
 * 단순 CRUD는 JPA Repository를 사용하고, 연관 데이터 조회 시 이 매퍼를 사용한다.
 */
public interface HandoverMapper {

    /**
     * 단일 인수인계 문서의 상세 정보를 조회한다.
     * 스페이스명, 그룹명, 작성자명 등 연관 테이블 데이터가 포함된다.
     */
    HandoverDto.DetailResDto detail(Long id);

    /**
     * 특정 스페이스의 루트 폴더에 있는 인수인계 문서 목록을 조회한다.
     * folderId가 NULL인 문서만 반환하며, 생성일 내림차순으로 정렬된다.
     */
    List<HandoverDto.DetailResDto> listBySpaceId(Long spaceId);

    /**
     * 특정 스페이스의 특정 폴더에 있는 인수인계 문서 목록을 조회한다.
     */
    List<HandoverDto.DetailResDto> listBySpaceIdAndFolderId(Long spaceId, Long folderId);

    /**
     * 특정 UserSpace에 연결된 최신 인수인계 문서를 조회한다.
     * 사용자가 스페이스 목록에서 인수인계 버튼 클릭 시 기존 문서 존재 여부 확인에 사용된다.
     */
    HandoverDto.DetailResDto findByUserSpaceId(Long userSpaceId);
}
