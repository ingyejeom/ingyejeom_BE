package com.thc.capstone.repository;

import com.thc.capstone.domain.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Chatbot Entity의 데이터베이스 접근을 담당하는 JPA Repository 입니다.
 * JpaRepository를 상속받아 기본적인 쿼리 메서드를 제공받습니다.
 */
public interface ChatbotRepository extends JpaRepository<Chatbot, Long>  {
    /**
     * 특정 스페이스에서 이루어진 과거 챗봇 대화 내역을 생성 시간 기준 오름차순으로 정렬하여 조회합니다.
     * @param userSpaceId 대화 기록을 조회할 스페이스의 ID
     * @return 해당 스페이스의 챗봇 대화 엔티티 리스트
     */
    List<Chatbot> findByUserSpaceIdOrderByCreatedAtAsc(Long userSpaceId);

    // 1. 커서가 없을 때 (최초 진입 시 가장 최근 대화 N개 로딩)
    List<Chatbot> findByUserSpaceIdOrderByIdDesc(Long userSpaceId, Pageable pageable);
    // 2. 커서가 있을 때 (스크롤 업: 기준 ID보다 작은 과거 대화 N개 로딩)
    List<Chatbot> findByUserSpaceIdAndIdLessThanOrderByIdDesc(Long userSpaceId, Long id, Pageable pageable);
}
