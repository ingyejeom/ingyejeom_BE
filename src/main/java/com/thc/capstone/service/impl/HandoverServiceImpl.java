package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.HandoverDto;
import com.thc.capstone.exception.OmittedFileException;
import com.thc.capstone.mapper.HandoverMapper;
import com.thc.capstone.repository.FileRepository;
import com.thc.capstone.repository.FolderRepository;
import com.thc.capstone.repository.HandoverRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.ChatbotService;
import com.thc.capstone.service.HandoverService;
import com.thc.capstone.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.FileEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final S3Service s3Service;
    private final ChatbotService chatbotService;

    @Override
    @Transactional
    public DefaultDto.CreateResDto create(HandoverDto.CreateReqDto param, Long reqUserId) {
        validateLogin(reqUserId);

        try {
            UserSpace userSpace = userSpaceRepository.findById(param.getUserSpaceId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 UserSpace입니다."));

            validateOwnership(userSpace, reqUserId);
            validateActiveStatus(userSpace);
            validateSpaceCanAcceptDraft(userSpace.getSpaceId(), reqUserId);
            validateNoExistingHandover(userSpace.getId());

            String title = resolveTitle(param.getTitle(), param.getRole());
            String text = resolveText(param.getText(), title, param.getRole());

            /**/
            if (param.getReferencedFileIds() != null && !param.getReferencedFileIds().isEmpty()) {
                List<File> referencedFiles = fileRepository.findAllById(param.getReferencedFileIds());
                for (File file : referencedFiles) {
                    file.setReferenceCount(file.getReferenceCount() + 1);
                }
                fileRepository.saveAll(referencedFiles);
            }
            /**/

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
            // userId + spaceId로 ACTIVE 상태의 UserSpace를 찾는다
            UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(
                    reqUserId, param.getSpaceId(), UserSpaceStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("해당 스페이스의 활성 담당자만 인수인계 문서를 생성할 수 있습니다."));

            validateSpaceCanAcceptDraft(param.getSpaceId(), reqUserId);
            validateNoExistingHandover(userSpace.getId());

            String title = resolveTitle(param.getTitle(), param.getRole());
            String text = resolveText(param.getText(), title, param.getRole());

            /**/
            if (param.getReferencedFileIds() != null && !param.getReferencedFileIds().isEmpty()) {
                List<File> referencedFiles = fileRepository.findAllById(param.getReferencedFileIds());
                for (File file : referencedFiles) {
                    file.setReferenceCount(file.getReferenceCount() + 1);
                }
                fileRepository.saveAll(referencedFiles);
            }
            /**/

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
        UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(
                        reqUserId, param.getSpaceId(), UserSpaceStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("해당 스페이스의 활성 담당자만 인수인계 문서를 생성할 수 있습니다."));

        validateCanEdit(handover.getId(), reqUserId);
        checkPermission(handover.getUserSpaceId(), reqUserId);

        /**/
        List<File> allUserFiles = fileRepository.findAllByUserSpaceId(userSpace.getId());
        List<Long> newFileIds = param.getReferencedFileIds() != null ? param.getReferencedFileIds() : new ArrayList<>();

        for (File file : allUserFiles) {
            boolean isNowReferenced = newFileIds.contains(file.getId());

            if (isNowReferenced && file.getReferenceCount() == 0) {
                file.setReferenceCount(1L);
            }
            else if (!isNowReferenced && file.getReferenceCount() > 0) {
                file.setReferenceCount(0L);
            }
        }
        fileRepository.saveAll(allUserFiles);
        /**/

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

        validateCanEdit(handover.getId(), reqUserId);
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

        validateCanEdit(handover.getId(), reqUserId);
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

    @Override
    public HandoverDto.PolicyResDto policy(Long handoverId, Long spaceId, Long reqUserId) {
        validateLogin(reqUserId);

        HandoverDto.PolicyResDto policy = null;
        if (handoverId != null) {
            policy = handoverMapper.policyByHandoverId(handoverId);
        } else if (spaceId != null) {
            policy = handoverMapper.policyBySpaceId(spaceId);
            if (policy == null) {
                policy = HandoverDto.PolicyResDto.builder()
                        .spaceId(spaceId)
                        .build();
            }
        }

        if (policy == null) {
            throw new RuntimeException("인수인계 정책 정보를 찾을 수 없습니다.");
        }

        applyPolicyFlags(policy, reqUserId);
        return policy;
    }

    @Override
    @Transactional
    public void saveGeneratedPdf(HandoverDto.SaveReqDto param, Long reqUserId) throws IOException {
        validateLogin(reqUserId);
        if (param.getHandoverId() == null) {
            throw new RuntimeException("인수인계 문서 ID가 필요합니다.");
        }
        if (param.getSpaceId() == null) {
            throw new RuntimeException("스페이스 ID가 필요합니다.");
        }
        if (param.getPdfFile() == null || param.getPdfFile().isEmpty()) {
            throw new RuntimeException("생성할 PDF 파일이 없습니다.");
        }

        Handover handover = handoverRepository.findWithLockById(param.getHandoverId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 인수인계 문서입니다."));

        HandoverDto.PolicyResDto policy = policy(handover.getId(), param.getSpaceId(), reqUserId);
        if (policy.getSpaceId() != null && !policy.getSpaceId().equals(param.getSpaceId())) {
            throw new RuntimeException("인수인계 문서와 스페이스 정보가 일치하지 않습니다.");
        }
        if (!Boolean.TRUE.equals(policy.getCanGeneratePdf())) {
            throw new RuntimeException(policy.getLockReason() != null ? policy.getLockReason() : "PDF를 생성할 수 없는 상태입니다.");
        }

        UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceId(reqUserId, param.getSpaceId())
                .orElseThrow(() -> new RuntimeException("해당 스페이스에 대한 접근 권한이 없습니다."));

        Long folderId = resolveHandoverFolderId(param.getSpaceId(), param.getFolderId());
        savePdfOnly(param.getPdfFile(), userSpace.getId(), folderId);

        if (param.getMdFile() != null && !param.getMdFile().isEmpty()) {
            ChatbotDto.IngestReqDto ingestReqDto = ChatbotDto.IngestReqDto.builder()
                    .spaceId(param.getSpaceId())
                    .fileBytes(param.getMdFile().getBytes())
                    .fileName(param.getMdFile().getOriginalFilename())
                    .build();
            chatbotService.ingestRequest(ingestReqDto, reqUserId);
        }

        handover.setPdfGeneratedAt(LocalDateTime.now());
        handover.setPdfGeneratedBy(reqUserId);
        handoverRepository.save(handover);

        log.info("인수인계 PDF 생성 기록 완료 - handoverId: {}, userId: {}", handover.getId(), reqUserId);
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
     * UserSpace가 ACTIVE 상태인지 검증한다.
     * 활성 담당자만 인수인계 문서를 생성할 수 있다.
     */
    private void validateActiveStatus(UserSpace userSpace) {
        if (userSpace.getStatus() != UserSpaceStatus.ACTIVE) {
            throw new RuntimeException("해당 스페이스의 활성 담당자만 인수인계 문서를 생성할 수 있습니다.");
        }
    }

    /**
     * 해당 UserSpace에 이미 인수인계 문서가 존재하는지 검증한다.
     * 1인 1문서 정책: 각 사용자는 스페이스당 하나의 인수인계 문서만 생성할 수 있다.
     */
    private void validateNoExistingHandover(Long userSpaceId) {
        if (handoverRepository.existsByUserSpaceIdAndDeletedFalse(userSpaceId)) {
            throw new RuntimeException("이미 해당 스페이스에 인수인계 문서가 존재합니다. 기존 문서를 삭제하거나 수정해주세요.");
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

    private void validateCanEdit(Long handoverId, Long reqUserId) {
        HandoverDto.PolicyResDto policy = policy(handoverId, null, reqUserId);
        if (!Boolean.TRUE.equals(policy.getCanEdit())) {
            throw new RuntimeException(policy.getLockReason() != null ? policy.getLockReason() : "잠긴 인수인계서는 수정할 수 없습니다.");
        }
    }

    private void validateSpaceCanAcceptDraft(Long spaceId, Long reqUserId) {
        HandoverDto.PolicyResDto policy = policy(null, spaceId, reqUserId);
        String activeStep = policy.getActiveApprovalStepStatus();
        if (activeStep != null && !"ASSIGNOR_TURN".equals(activeStep)) {
            throw new RuntimeException("전임자 서명 완료 이후에는 인수인계서를 새로 작성할 수 없습니다.");
        }
    }

    private void applyPolicyFlags(HandoverDto.PolicyResDto policy, Long reqUserId) {
        String activeStep = policy.getActiveApprovalStepStatus();
        boolean hasActiveApproval = activeStep != null;
        boolean ownerIsInactive = "INACTIVE".equals(policy.getOwnerUserSpaceStatus());
        boolean preSigned = "ASSIGNEE_TURN".equals(activeStep);
        boolean completedLocked = "ADMIN_TURN".equals(activeStep) || ownerIsInactive;
        boolean pdfAlreadyGenerated = policy.getPdfGeneratedAt() != null;
        boolean isPdfActor = reqUserId.equals(policy.getAssignorId()) || reqUserId.equals(policy.getAssigneeId());

        boolean canEdit = !ownerIsInactive && (!hasActiveApproval || "ASSIGNOR_TURN".equals(activeStep));
        boolean canGeneratePdf = preSigned && isPdfActor && !pdfAlreadyGenerated;

        policy.setCanEdit(canEdit);
        policy.setCanGeneratePdf(canGeneratePdf);

        if (pdfAlreadyGenerated) {
            policy.setLockReason("PDF가 이미 생성되었습니다.");
        } else if (canGeneratePdf) {
            policy.setLockReason(null);
        } else if (preSigned && !isPdfActor) {
            policy.setLockReason("전임자 또는 인수자만 PDF를 생성할 수 있습니다.");
        } else if (preSigned) {
            policy.setLockReason("전임자 서명 완료 상태에서만 PDF를 생성할 수 있습니다.");
        } else if (completedLocked || "COMPLETED".equals(activeStep)) {
            policy.setLockReason("인수자 서명 완료 이후에는 인수인계서가 잠깁니다.");
        } else if (hasActiveApproval) {
            policy.setLockReason("전임자 서명 전까지만 수정할 수 있습니다.");
        } else if (ownerIsInactive) {
            policy.setLockReason("완료된 이전 담당자의 인수인계서는 수정할 수 없습니다.");
        } else {
            policy.setLockReason("전임자 서명 완료 상태에서만 PDF를 생성할 수 있습니다.");
        }
    }

    private void savePdfOnly(MultipartFile multipartFile, Long userSpaceId, Long folderId) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        String fileUrl = s3Service.upload(multipartFile, storeFileName);

        com.thc.capstone.domain.File file = com.thc.capstone.domain.File.of(
                originalFilename,
                storeFileName,
                fileUrl,
                multipartFile.getSize(),
                userSpaceId,
                folderId
        );
        fileRepository.save(file);
    }

    private Long resolveHandoverFolderId(Long spaceId, Long requestedFolderId) {
        if (requestedFolderId != null) {
            return requestedFolderId;
        }

        Long existingFolderId = handoverMapper.findHandoverFolderId(spaceId);
        if (existingFolderId != null) {
            return existingFolderId;
        }

        Folder folder = Folder.of("인수인계서", null, spaceId);
        folderRepository.save(folder);
        return folder.getId();
    }

    private String createStoreFileName(String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + ext;
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
