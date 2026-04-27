package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.ApprovalDto;
import com.thc.capstone.dto.UserApprovalDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.mapper.ApprovalMapper;
import com.thc.capstone.repository.*;
import com.thc.capstone.service.PermittedService;
import com.thc.capstone.service.ApprovalService;
import com.thc.capstone.service.UserApprovalService;
import com.thc.capstone.service.UserSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApprovalServiceImpl implements ApprovalService {
    final ApprovalRepository approvalRepository;
    final ApprovalMapper approvalMapper;
    final UserSpaceRepository userSpaceRepository;
    final UserRepository userRepository;
    final UserSpaceService userSpaceService;
    final UserApprovalService userApprovalService;
    final UserApprovalRepository userApprovalRepository;

    String target = "approval";

    @Override
    @Transactional
    public void startHandover(ApprovalDto.InviteReqDto param, Long reqUserId) {
        User assigneeUser = userRepository.findByEmail(param.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 이메일을 가진 유저가 없습니다."));

        ApprovalDto.CreateReqDto approvalParam = ApprovalDto.CreateReqDto.builder()
                .spaceId(param.getSpaceId())
                .assigneeId(assigneeUser.getId())
                .build();

        create(approvalParam, reqUserId);
    }

    @Override
    @Transactional
    public DefaultDto.CreateResDto create(ApprovalDto.CreateReqDto param, Long reqUserId) {
//        permittedService.check(target, 110, reqUserId);

        Long spaceId = param.getSpaceId();
        Long assigneeId = param.getAssigneeId();

        Long assignorId = approvalMapper.findAssignorIdBySpaceId(spaceId);
        if (assignorId == null) {
            throw new RuntimeException("해당 스페이스의 활성 인계자를 찾을 수 없습니다.");
        }

        if (assignorId.equals(assigneeId)) {
            throw new RuntimeException("본인 스스로에게 인계할 수 없습니다.");
        }

        Approval approval = approvalRepository.save(param.toEntity());

        Long adminId = approvalMapper.findAdminIdBySpaceId(spaceId);
        if (adminId == null) {
            throw new RuntimeException("해당 스페이스의 활성 관리자를 찾을 수 없습니다.");
        }

        UserSpace assigneeUserSpace = userSpaceRepository.findByUserIdAndSpaceIdAndRole(assigneeId, spaceId, Role.USER).orElse(null);

        if (assigneeUserSpace != null) {
            // 이전에 스페이스에 참여했던 이력이 있다면 PENDING 으로 업데이트
            userSpaceService.update(UserSpaceDto.UpdateReqDto.builder()
                    .id(assigneeUserSpace.getId())
                    .status(UserSpaceStatus.PENDING)
                    .deleted(false)
                    .build());
        } else {
            // 처음 스페이스에 들어오는 것이라면 PENDING 상태로 새로 생성
            userSpaceService.create(UserSpaceDto.CreateReqDto.builder()
                    .userId(assigneeId)
                    .spaceId(spaceId)
                    .role(Role.USER)
                    .status(UserSpaceStatus.PENDING)
                    .build());
        }

        userApprovalService.create(UserApprovalDto.CreateReqDto.builder()
                .approvalId(approval.getId())
                .userId(assignorId)
                .approvalRole(ApprovalRole.ASSIGNOR)
                .build(), reqUserId);

        userApprovalService.create(UserApprovalDto.CreateReqDto.builder()
                .approvalId(approval.getId())
                .userId(assigneeId)
                .approvalRole(ApprovalRole.ASSIGNEE)
                .build(), reqUserId);

        userApprovalService.create(UserApprovalDto.CreateReqDto.builder()
                .approvalId(approval.getId())
                .userId(adminId)
                .approvalRole(ApprovalRole.ADMIN)
                .build(), reqUserId);

        return approval.toCreateResDto();
    }

    @Override
    @Transactional
    public void sign(ApprovalDto.UpdateReqDto param, Long reqUserId){
        Approval approval = approvalRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 서명입니다"));

        if (approval.getStepStatus() == StepStatus.COMPLETED) {
            throw new RuntimeException("이미 완료된 인계 절차입니다.");
        }

        ApprovalRole requiredRole = getRequiredRoleForCurrentStep(approval.getStepStatus());

        boolean hasPermission = approvalMapper.hasRoleByApprovalIdAndUserId(
                approval.getId(), reqUserId, requiredRole.name()
        );

        if (!hasPermission) {
            throw new RuntimeException("현재 차례에 서명할 권한이 없습니다.");
        }

        UserApproval currentUserApproval = userApprovalRepository
                .findByApprovalIdAndApprovalRole(approval.getId(), requiredRole)
                        .orElseThrow(() -> new RuntimeException("해당 서명자 정보를 찾을 수 없습니다"));

        userApprovalService.update(UserApprovalDto.UpdateReqDto.builder()
                .id(currentUserApproval.getId())
                .signedAt(LocalDateTime.now())
                .build(), reqUserId);

        // 4. 상태 전이 및 인계 완료 처리
        progressToNextStep(approval, reqUserId);
    }

    private ApprovalRole getRequiredRoleForCurrentStep(StepStatus status) {
        if (status == StepStatus.ASSIGNOR_TURN) return ApprovalRole.ASSIGNOR;
        if (status == StepStatus.ASSIGNEE_TURN) return ApprovalRole.ASSIGNEE;
        if (status == StepStatus.ADMIN_TURN) return ApprovalRole.ADMIN;

        throw new RuntimeException("알 수 없는 진행 상태입니다.");
    }

    private void progressToNextStep(Approval approval, Long reqUserId) {
        StepStatus currentStatus = approval.getStepStatus();

        if (currentStatus == StepStatus.ASSIGNOR_TURN) {
            update(ApprovalDto.UpdateReqDto.builder()
                    .id(approval.getId())
                    .stepStatus(StepStatus.ASSIGNEE_TURN)
                    .build(), reqUserId);
        } else if (currentStatus == StepStatus.ASSIGNEE_TURN) {
            update(ApprovalDto.UpdateReqDto.builder()
                    .id(approval.getId())
                    .stepStatus(StepStatus.ADMIN_TURN)
                    .build(), reqUserId);
        } else if (currentStatus == StepStatus.ADMIN_TURN) {
            update(ApprovalDto.UpdateReqDto.builder()
                    .id(approval.getId())
                    .stepStatus(StepStatus.COMPLETED)
                    .build(), reqUserId);

            // 서명 완료되면 권한 인계
            completeApproval(approval.getSpaceId());
        }
    }

    private void completeApproval(Long spaceId){
        Long assignorUserSpaceId = approvalMapper.findUserSpaceIdByStatus(spaceId, UserSpaceStatus.ACTIVE.toString());

        Long assigneeUserSpaceId = approvalMapper.findUserSpaceIdByStatus(spaceId, UserSpaceStatus.PENDING.toString());

        if (assignorUserSpaceId != null) {
            userSpaceService.update(UserSpaceDto.UpdateReqDto.builder()
                    .id(assignorUserSpaceId)
                    .status(UserSpaceStatus.INACTIVE)
                    .build());
        }

        if (assigneeUserSpaceId != null) {
            userSpaceService.update(UserSpaceDto.UpdateReqDto.builder()
                    .id(assigneeUserSpaceId)
                    .status(UserSpaceStatus.ACTIVE)
                    .build());
        }
    }

    @Override
    public void cancel(ApprovalDto.UpdateReqDto param, Long reqUserId) {
        Approval approval = approvalRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 서명 테이블 입니다"));

        if(approval.getStepStatus() == StepStatus.COMPLETED) {
            throw new RuntimeException("이미 완료된 인계는 취소할 수 없습니다.");
        }

        update(ApprovalDto.UpdateReqDto.builder()
                .id(approval.getId())
                .deleted(true)
                .build(), reqUserId);

        userApprovalService.deleteByApprovalId(approval.getId(), reqUserId);

        Long assigneeUserSpaceId = approvalMapper.findUserSpaceIdByStatus(approval.getSpaceId(), UserSpaceStatus.PENDING.toString());
        if (assigneeUserSpaceId != null) {
            userSpaceService.delete(UserSpaceDto.UpdateReqDto.builder()
                    .id(assigneeUserSpaceId)
                    .build());
        }
    }

    @Override
    public void update(ApprovalDto.UpdateReqDto param, Long reqUserId) {
        // 존재하는 서명 테이블인지 검증
        Approval approval = approvalRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        // 수정 적용 및 DB 저장
        approval.update(param);
        approvalRepository.save(approval);
    }

    @Override
    public void delete(ApprovalDto.UpdateReqDto param, Long reqUserId) {
        update(ApprovalDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    // Mapper 를 이용한 사용자 정보 조회 함수
    public ApprovalDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
//        permittedService.check(target, 200, reqUserId);

        return approvalMapper.detail(param.getId());
    }

    @Override
    public ApprovalDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    // Mapper 를 통해 받은 사용자 리스트의 ID 값을 이용해 객체 리스트로 넘김
    public List<ApprovalDto.DetailResDto> addlist(List<ApprovalDto.DetailResDto> list, Long reqUserId){
        List<ApprovalDto.DetailResDto> newList = new ArrayList<>();
        for(ApprovalDto.DetailResDto approval : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(approval.getId())
                    .build(), reqUserId));
        }

        return newList;
    }

    @Override
    public List<ApprovalDto.DetailResDto> list(ApprovalDto.ListReqDto param, Long reqUserId) {
        List<ApprovalDto.DetailResDto> approvals = approvalMapper.list(param);

        return addlist(approvals, reqUserId);
    }
}
