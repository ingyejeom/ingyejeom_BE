package com.thc.capstone.mapper;

import com.thc.capstone.dto.HandoverDto;

import java.util.List;

// 복잡한 SQL 쿼리(여러 테이블 JOIN 등)를 실행할 때 사용하는 인터페이스
public interface HandoverMapper {

    // 인수인계 문서 1개의 상세 정보 조회 (스페이스명, 그룹명, 작성자명 포함)
    HandoverDto.DetailResDto detail(Long id);

    // 특정 스페이스의 모든 인수인계 문서 목록 조회
    List<HandoverDto.DetailResDto> listBySpaceId(Long spaceId);

    // 특정 스페이스와 폴더의 인수인계 문서 목록 조회
    List<HandoverDto.DetailResDto> listBySpaceIdAndFolderId(Long spaceId, Long folderId);
}
