package com.thc.capstone.service.impl;

import com.thc.capstone.domain.Handover;
import com.thc.capstone.domain.UserSpace;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.HandoverDto;
import com.thc.capstone.mapper.HandoverMapper;
import com.thc.capstone.repository.HandoverRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.HandoverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// 인수인계 관련 비즈니스 로직을 실제로 처리하는 클래스
@Slf4j
@RequiredArgsConstructor
@Service
public class HandoverServiceImpl implements HandoverService {

    private final HandoverRepository handoverRepository;    // 인수인계 DB 접근
    private final UserSpaceRepository userSpaceRepository;  // 권한 확인용
    private final HandoverMapper handoverMapper;            // 복잡한 쿼리 실행용

    // 새 인수인계 문서 생성
    @Override
    @Transactional
    public DefaultDto.CreateResDto create(HandoverDto.CreateReqDto param, Long reqUserId) {
        // 로그인 체크
        if (reqUserId == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        try {
            // UserSpace가 존재하는지 확인
            UserSpace userSpace = userSpaceRepository.findById(param.getUserSpaceId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 UserSpace입니다."));

            // 요청한 사람이 이 UserSpace의 주인인지 확인
            if (!userSpace.getUserId().equals(reqUserId)) {
                throw new RuntimeException("해당 인수인계서에 대한 권한이 없습니다.");
            }

            // 제목이 없으면 자동 생성 (역할명 + 인수인계서)
            String title = param.getTitle();
            if (title == null || title.trim().isEmpty()) {
                String role = param.getRole();
                title = (role != null && !role.trim().isEmpty()) ? role + " 인수인계서" : "인수인계서";
            }

            // 내용이 없으면 빈 JSON 구조 생성
            String text = param.getText();
            if (text == null || text.trim().isEmpty()) {
                text = "{\"title\":\"" + title + "\",\"role\":\"" + param.getRole() + "\",\"modules\":[]}";
            }

            // 인수인계 문서 저장
            Handover handover = Handover.of(title, param.getRole(), text, param.getUserSpaceId());
            handoverRepository.save(handover);

            log.info("인수인계 문서 생성 완료 - ID: {}, 제목: {}", handover.getId(), title);
            return handover.toCreateResDto();

        } catch (Exception e) {
            log.error("인수인계 문서 생성 실패: {}", e.getMessage());
            throw new RuntimeException("인수인계 문서 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 인수인계 문서 수정
    @Override
    @Transactional
    public void update(HandoverDto.UpdateReqDto param, Long reqUserId) {
        // 문서가 존재하는지 확인
        Handover handover = handoverRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 인수인계 문서입니다."));

        // 수정 권한 확인
        checkPermission(handover.getUserSpaceId(), reqUserId);

        // 수정 후 저장
        handover.update(param);
        handoverRepository.save(handover);

        log.info("인수인계 문서 수정 완료 - ID: {}", param.getId());
    }

    // 인수인계 문서 삭제 (실제로 지우지 않고 deleted=true로 표시)
    @Override
    @Transactional
    public void delete(HandoverDto.UpdateReqDto param, Long reqUserId) {
        update(HandoverDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);

        log.info("인수인계 문서 삭제 완료 - ID: {}", param.getId());
    }

    // 인수인계 문서 1개 상세 조회 (내부용)
    private HandoverDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
        HandoverDto.DetailResDto res = handoverMapper.detail(param.getId());

        if (res == null) {
            throw new RuntimeException("존재하지 않는 인수인계 문서입니다.");
        }

        return res;
    }

    // 인수인계 문서 1개 상세 조회 (외부용)
    @Override
    public HandoverDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    // 목록의 각 항목에 대해 상세 정보를 추가해서 새 목록 반환
    private List<HandoverDto.DetailResDto> addList(List<HandoverDto.DetailResDto> list, Long reqUserId) {
        List<HandoverDto.DetailResDto> newList = new ArrayList<>();

        for (HandoverDto.DetailResDto handover : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(handover.getId())
                    .build(), reqUserId));
        }

        return newList;
    }

    // 특정 스페이스의 모든 인수인계 문서 목록 조회
    @Override
    public List<HandoverDto.DetailResDto> listBySpaceId(Long spaceId, Long reqUserId) {
        List<HandoverDto.DetailResDto> handovers = handoverMapper.listBySpaceId(spaceId);
        return addList(handovers, reqUserId);
    }

    // 인수인계 문서의 모듈 데이터(JSON)만 업데이트
    @Override
    @Transactional
    public void updateModules(Long id, String text, Long reqUserId) {
        // 문서 조회
        Handover handover = handoverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 인수인계 문서입니다."));

        // 권한 확인
        checkPermission(handover.getUserSpaceId(), reqUserId);

        // 모듈 데이터만 업데이트
        handover.setText(text);
        handoverRepository.save(handover);

        log.info("인수인계 모듈 데이터 업데이트 완료 - ID: {}", id);
    }

    // 요청한 사람이 이 문서를 수정/삭제할 권한이 있는지 확인
    private void checkPermission(Long userSpaceId, Long reqUserId) {
        if (reqUserId == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        UserSpace userSpace = userSpaceRepository.findById(userSpaceId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 UserSpace입니다."));

        // 본인만 수정 가능
        if (!userSpace.getUserId().equals(reqUserId)) {
            throw new RuntimeException("해당 인수인계서에 대한 권한이 없습니다.");
        }
    }
}
