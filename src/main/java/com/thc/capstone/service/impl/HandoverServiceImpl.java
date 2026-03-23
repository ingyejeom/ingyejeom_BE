package com.thc.capstone.service.impl;

import com.thc.capstone.domain.Handover;
import com.thc.capstone.domain.UserSpace;
import com.thc.capstone.domain.UserSpaceStatus;
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

/**
 * 인수인계 문서 비즈니스 로직 구현체
 *
 * UserSpace 소유자만 해당 문서를 생성/수정/삭제할 수 있도록 권한을 검증한다.
 * 복잡한 조회(JOIN 필요)는 MyBatis Mapper를, 단순 CRUD는 JPA Repository를 사용한다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class HandoverServiceImpl implements HandoverService {

    private final HandoverRepository handoverRepository;
    private final UserSpaceRepository userSpaceRepository;
    private final HandoverMapper handoverMapper;

    @Override
    @Transactional
    public DefaultDto.CreateResDto create(HandoverDto.CreateReqDto param, Long reqUserId) {
        validateLogin(reqUserId);

        try {
            UserSpace userSpace = userSpaceRepository.findById(param.getUserSpaceId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 UserSpace입니다."));

            validateOwnership(userSpace, reqUserId);

            String title = resolveTitle(param.getTitle(), param.getRole());
            String text = resolveText(param.getText(), title, param.getRole());

            Handover handover = Handover.of(title, param.getRole(), text, param.getUserSpaceId());
            handoverRepository.save(handover);

            log.info("인수인계 문서 생성 완료 - ID: {}, 제목: {}", handover.getId(), title);
            return handover.toCreateResDto();

        } catch (Exception e) {
            log.error("인수인계 문서 생성 실패: {}", e.getMessage());
            throw new RuntimeException("인수인계 문서 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * spaceId로 인수인계 문서를 생성한다.
     * 프론트엔드에서 userSpaceId 대신 spaceId만 전달하는 경우 사용한다.
     */
    @Override
    @Transactional
    public DefaultDto.CreateResDto createBySpaceId(HandoverDto.CreateBySpaceIdReqDto param, Long reqUserId) {
        validateLogin(reqUserId);

        try {
            // userId + spaceId로 UserSpace를 찾는다 (status 무관)
            UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceId(reqUserId, param.getSpaceId())
                    .orElseThrow(() -> new RuntimeException("해당 스페이스에 대한 접근 권한이 없습니다."));

            String title = resolveTitle(param.getTitle(), param.getRole());
            String text = resolveText(param.getText(), title, param.getRole());

            Handover handover = Handover.of(title, param.getRole(), text, userSpace.getId());
            handoverRepository.save(handover);

            log.info("[createBySpaceId] 인수인계 문서 생성 완료 - ID: {}, 제목: {}, spaceId: {}", handover.getId(), title, param.getSpaceId());
            return handover.toCreateResDto();

        } catch (Exception e) {
            log.error("[createBySpaceId] 인수인계 문서 생성 실패: {}", e.getMessage());
            throw new RuntimeException("인수인계 문서 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(HandoverDto.UpdateReqDto param, Long reqUserId) {
        Handover handover = handoverRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 인수인계 문서입니다."));

        checkPermission(handover.getUserSpaceId(), reqUserId);

        handover.update(param);
        handoverRepository.save(handover);

        log.info("인수인계 문서 수정 완료 - ID: {}", param.getId());
    }

    @Override
    @Transactional
    public void delete(HandoverDto.UpdateReqDto param, Long reqUserId) {
        update(HandoverDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);

        log.info("인수인계 문서 삭제 완료 - ID: {}", param.getId());
    }

    @Override
    public HandoverDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    @Override
    public List<HandoverDto.DetailResDto> listBySpaceId(Long spaceId, Long reqUserId) {
        List<HandoverDto.DetailResDto> handovers = handoverMapper.listBySpaceId(spaceId);
        return enrichWithDetails(handovers, reqUserId);
    }

    @Override
    public List<HandoverDto.DetailResDto> listBySpaceIdAndFolderId(Long spaceId, Long folderId, Long reqUserId) {
        List<HandoverDto.DetailResDto> handovers = handoverMapper.listBySpaceIdAndFolderId(spaceId, folderId);
        return enrichWithDetails(handovers, reqUserId);
    }

    @Override
    @Transactional
    public void updateModules(Long id, String text, Long reqUserId) {
        Handover handover = handoverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 인수인계 문서입니다."));

        checkPermission(handover.getUserSpaceId(), reqUserId);

        handover.setText(text);
        handoverRepository.save(handover);

        log.info("인수인계 모듈 데이터 업데이트 완료 - ID: {}", id);
    }

    @Override
    @Transactional
    public void move(HandoverDto.MoveReqDto param, Long reqUserId) {
        Handover handover = handoverRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 인수인계 문서입니다."));

        checkPermission(handover.getUserSpaceId(), reqUserId);

        handover.setFolderId(param.getTargetFolderId());
        handoverRepository.save(handover);

        log.info("인수인계 문서 이동 완료 - ID: {}, 대상 폴더: {}", param.getId(), param.getTargetFolderId());
    }

    @Override
    public HandoverDto.DetailResDto getByUserSpaceId(Long userSpaceId, Long reqUserId) {
        validateLogin(reqUserId);

        UserSpace userSpace = userSpaceRepository.findById(userSpaceId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 UserSpace입니다."));

        validateOwnership(userSpace, reqUserId);

        return handoverMapper.findByUserSpaceId(userSpaceId);
    }

    // ========================================
    // Private Helper Methods
    // ========================================

    /**
     * 로그인 여부를 검증한다.
     */
    private void validateLogin(Long reqUserId) {
        if (reqUserId == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }
    }

    /**
     * 요청자가 UserSpace의 소유자인지 검증한다.
     */
    private void validateOwnership(UserSpace userSpace, Long reqUserId) {
        if (!userSpace.getUserId().equals(reqUserId)) {
            throw new RuntimeException("해당 인수인계서에 대한 권한이 없습니다.");
        }
    }

    /**
     * 문서 수정/삭제 권한을 검증한다.
     * UserSpace 소유자만 해당 문서를 수정할 수 있다.
     */
    private void checkPermission(Long userSpaceId, Long reqUserId) {
        validateLogin(reqUserId);

        UserSpace userSpace = userSpaceRepository.findById(userSpaceId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 UserSpace입니다."));

        validateOwnership(userSpace, reqUserId);
    }

    /**
     * 제목이 비어있으면 역할명 기반으로 기본 제목을 생성한다.
     */
    private String resolveTitle(String title, String role) {
        if (title != null && !title.trim().isEmpty()) {
            return title;
        }
        return (role != null && !role.trim().isEmpty()) ? role + " 인수인계서" : "인수인계서";
    }

    /**
     * 내용이 비어있으면 빈 모듈 배열을 가진 JSON 구조를 생성한다.
     */
    private String resolveText(String text, String title, String role) {
        if (text != null && !text.trim().isEmpty()) {
            return text;
        }
        return "{\"title\":\"" + title + "\",\"role\":\"" + (role != null ? role : "") + "\",\"modules\":[]}";
    }

    /**
     * 단일 인수인계 문서를 조회한다.
     */
    private HandoverDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
        HandoverDto.DetailResDto res = handoverMapper.detail(param.getId());

        if (res == null) {
            throw new RuntimeException("존재하지 않는 인수인계 문서입니다.");
        }

        return res;
    }

    /**
     * 목록의 각 문서에 대해 상세 정보를 조회하여 보강한다.
     * 목록 조회 시 누락된 연관 데이터를 채우기 위해 사용된다.
     */
    private List<HandoverDto.DetailResDto> enrichWithDetails(List<HandoverDto.DetailResDto> list, Long reqUserId) {
        List<HandoverDto.DetailResDto> enrichedList = new ArrayList<>();

        for (HandoverDto.DetailResDto handover : list) {
            enrichedList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(handover.getId())
                    .build(), reqUserId));
        }

        return enrichedList;
    }
}
