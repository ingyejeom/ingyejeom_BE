package com.thc.capstone.repository;

import com.thc.capstone.domain.Handover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 인수인계 문서를 데이터베이스에서 조회/저장/삭제할 때 사용하는 인터페이스
public interface HandoverRepository extends JpaRepository<Handover, Long> {

    // 특정 UserSpace에 속한 모든 인수인계 문서 조회
    List<Handover> findByUserSpaceId(Long userSpaceId);

    // 특정 UserSpace에서 삭제되지 않은 문서만 조회
    List<Handover> findByUserSpaceIdAndDeleted(Long userSpaceId, Boolean deleted);

    // 특정 역할의 인수인계 문서 조회
    List<Handover> findByRole(String role);

    // 특정 역할에서 삭제되지 않은 문서만 조회
    List<Handover> findByRoleAndDeleted(String role, Boolean deleted);

    // 특정 UserSpace에서 특정 역할의 문서 1개 조회
    Optional<Handover> findByUserSpaceIdAndRole(Long userSpaceId, String role);

    // 제목에 특정 단어가 포함된 문서 검색
    List<Handover> findByTitleContaining(String title);

    // 제목 검색 + 삭제 여부 필터
    List<Handover> findByTitleContainingAndDeleted(String title, Boolean deleted);

    // 특정 UserSpace에 인수인계 문서가 있는지 확인
    boolean existsByUserSpaceId(Long userSpaceId);

    // 특정 UserSpace에서 특정 역할의 문서가 있는지 확인
    boolean existsByUserSpaceIdAndRole(Long userSpaceId, String role);
}
