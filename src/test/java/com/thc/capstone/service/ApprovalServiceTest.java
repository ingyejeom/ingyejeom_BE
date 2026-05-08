package com.thc.capstone.service;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.ApprovalDto;
import com.thc.capstone.dto.UserApprovalDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.mapper.ApprovalMapper;
import com.thc.capstone.repository.ApprovalRepository;
import com.thc.capstone.repository.UserApprovalRepository;
import com.thc.capstone.repository.UserRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.impl.ApprovalServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

    @InjectMocks
    private ApprovalServiceImpl approvalService;

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private ApprovalMapper approvalMapper;

    @Mock
    private UserSpaceService userSpaceService;

    @Mock
    private UserApprovalService userApprovalService;

    @Mock
    private UserApprovalRepository userApprovalRepository;

    @Test
    @DisplayName("성공 케이스: 인계자 정상 서명시 상태가 인수자턴으로 변경된다")
    void sign_assignor_success() {
        // Given
        Long reqUserId = 100L;
        Long approvalId = 1L;
        Approval approval = Approval.of(StepStatus.ASSIGNOR_TURN, 10L);
        ReflectionTestUtils.setField(approval, "id", approvalId);

        UserApproval userApproval = UserApproval.of(ApprovalRole.ASSIGNOR, reqUserId, approvalId);
        ReflectionTestUtils.setField(userApproval, "id", 1000L);

        ApprovalDto.UpdateReqDto param = ApprovalDto.UpdateReqDto.builder().id(approvalId).build();

        given(approvalRepository.findById(approvalId)).willReturn(Optional.of(approval));
        given(approvalMapper.hasRoleByApprovalIdAndUserId(approvalId, reqUserId, ApprovalRole.ASSIGNOR.name()))
                .willReturn(true);
        given(userApprovalRepository.findByApprovalIdAndApprovalRole(approvalId, ApprovalRole.ASSIGNOR))
                .willReturn(Optional.of(userApproval));

        // When
        approvalService.sign(param, reqUserId);

        // Then
        assertThat(approval.getStepStatus()).isEqualTo(StepStatus.ASSIGNEE_TURN);
        verify(userApprovalService, times(1)).update(any(UserApprovalDto.UpdateReqDto.class), eq(reqUserId));
        verify(approvalRepository, times(1)).save(approval);
    }

    @Test
    @DisplayName("성공 케이스: 인수자 정상 서명시 상태가 관리자턴으로 변경된다")
    void sign_assignee_success() {
        // Given
        Long reqUserId = 101L;
        Long approvalId = 1L;
        Approval approval = Approval.of(StepStatus.ASSIGNEE_TURN, 10L);
        ReflectionTestUtils.setField(approval, "id", approvalId);

        UserApproval userApproval = UserApproval.of(ApprovalRole.ASSIGNEE, reqUserId, approvalId);
        ReflectionTestUtils.setField(userApproval, "id", 1001L);

        ApprovalDto.UpdateReqDto param = ApprovalDto.UpdateReqDto.builder().id(approvalId).build();

        given(approvalRepository.findById(approvalId)).willReturn(Optional.of(approval));
        given(approvalMapper.hasRoleByApprovalIdAndUserId(approvalId, reqUserId, ApprovalRole.ASSIGNEE.name()))
                .willReturn(true);
        given(userApprovalRepository.findByApprovalIdAndApprovalRole(approvalId, ApprovalRole.ASSIGNEE))
                .willReturn(Optional.of(userApproval));

        // When
        approvalService.sign(param, reqUserId);

        // Then
        assertThat(approval.getStepStatus()).isEqualTo(StepStatus.ADMIN_TURN);
        verify(userApprovalService, times(1)).update(any(UserApprovalDto.UpdateReqDto.class), eq(reqUserId));
        verify(approvalRepository, times(1)).save(approval);
    }

    @Test
    @DisplayName("성공 케이스: 관리자 정상 서명시 상태가 완료되고 권한이 인계된다")
    void sign_admin_success() {
        // Given
        Long reqUserId = 102L;
        Long approvalId = 1L;
        Long spaceId = 10L;
        Approval approval = Approval.of(StepStatus.ADMIN_TURN, spaceId);
        ReflectionTestUtils.setField(approval, "id", approvalId);

        UserApproval userApproval = UserApproval.of(ApprovalRole.ADMIN, reqUserId, approvalId);
        ReflectionTestUtils.setField(userApproval, "id", 1002L);

        ApprovalDto.UpdateReqDto param = ApprovalDto.UpdateReqDto.builder().id(approvalId).build();

        given(approvalRepository.findById(approvalId)).willReturn(Optional.of(approval));
        given(approvalMapper.hasRoleByApprovalIdAndUserId(approvalId, reqUserId, ApprovalRole.ADMIN.name()))
                .willReturn(true);
        given(userApprovalRepository.findByApprovalIdAndApprovalRole(approvalId, ApprovalRole.ADMIN))
                .willReturn(Optional.of(userApproval));

        Long assignorUserSpaceId = 200L;
        Long assigneeUserSpaceId = 201L;
        given(approvalMapper.findUserSpaceIdByStatus(spaceId, UserSpaceStatus.ACTIVE.toString()))
                .willReturn(assignorUserSpaceId);
        given(approvalMapper.findUserSpaceIdByStatus(spaceId, UserSpaceStatus.PENDING.toString()))
                .willReturn(assigneeUserSpaceId);

        // When
        approvalService.sign(param, reqUserId);

        // Then
        assertThat(approval.getStepStatus()).isEqualTo(StepStatus.COMPLETED);
        verify(userApprovalService, times(1)).update(any(UserApprovalDto.UpdateReqDto.class), eq(reqUserId));
        verify(approvalRepository, times(1)).save(approval);
        
        // 권한 스위칭 검증 (INACTIVE, ACTIVE 두 번 업데이트)
        verify(userSpaceService, times(2)).update(any(UserSpaceDto.UpdateReqDto.class));
    }

    @Test
    @DisplayName("실패 케이스 (순서 위반): 인계자 턴에 인수자가 서명을 시도하면 실패한다")
    void sign_fail_invalid_turn() {
        // Given
        Long reqUserId = 101L; // 인수자
        Long approvalId = 1L;
        Approval approval = Approval.of(StepStatus.ASSIGNOR_TURN, 10L);
        ReflectionTestUtils.setField(approval, "id", approvalId);

        ApprovalDto.UpdateReqDto param = ApprovalDto.UpdateReqDto.builder().id(approvalId).build();

        given(approvalRepository.findById(approvalId)).willReturn(Optional.of(approval));
        
        // 현재 상태가 ASSIGNOR_TURN 이므로 requiredRole은 ASSIGNOR임.
        // reqUserId(101L)는 ASSIGNOR 역할이 없으므로 false 반환
        given(approvalMapper.hasRoleByApprovalIdAndUserId(approvalId, reqUserId, ApprovalRole.ASSIGNOR.name()))
                .willReturn(false);

        // When & Then
        assertThatThrownBy(() -> approvalService.sign(param, reqUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("현재 차례에 서명할 권한이 없습니다.");
    }

    @Test
    @DisplayName("실패 케이스 (권한 없음): 결재에 참여하지 않는 제3자가 서명을 시도하면 실패한다")
    void sign_fail_no_permission() {
        // Given
        Long reqUserId = 999L; // 제3자
        Long approvalId = 1L;
        Approval approval = Approval.of(StepStatus.ASSIGNEE_TURN, 10L);
        ReflectionTestUtils.setField(approval, "id", approvalId);

        ApprovalDto.UpdateReqDto param = ApprovalDto.UpdateReqDto.builder().id(approvalId).build();

        given(approvalRepository.findById(approvalId)).willReturn(Optional.of(approval));
        given(approvalMapper.hasRoleByApprovalIdAndUserId(approvalId, reqUserId, ApprovalRole.ASSIGNEE.name()))
                .willReturn(false);

        // When & Then
        assertThatThrownBy(() -> approvalService.sign(param, reqUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("현재 차례에 서명할 권한이 없습니다.");
    }

    @Test
    @DisplayName("실패 케이스 (상태 오류): 이미 완료된 결재건에 서명 시도시 실패한다")
    void sign_fail_already_completed() {
        // Given
        Long reqUserId = 100L;
        Long approvalId = 1L;
        Approval approval = Approval.of(StepStatus.COMPLETED, 10L);
        ReflectionTestUtils.setField(approval, "id", approvalId);

        ApprovalDto.UpdateReqDto param = ApprovalDto.UpdateReqDto.builder().id(approvalId).build();

        given(approvalRepository.findById(approvalId)).willReturn(Optional.of(approval));

        // When & Then
        assertThatThrownBy(() -> approvalService.sign(param, reqUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 완료된 인계 절차입니다.");
    }

    @Test
    @DisplayName("실패 케이스 (상태 오류): 이미 취소된 결재건에 서명 시도시 실패한다")
    void sign_fail_already_deleted() {
        // Given
        Long reqUserId = 100L;
        Long approvalId = 1L;
        Approval approval = Approval.of(StepStatus.ASSIGNOR_TURN, 10L);
        ReflectionTestUtils.setField(approval, "id", approvalId);
        approval.setDeleted(true);

        ApprovalDto.UpdateReqDto param = ApprovalDto.UpdateReqDto.builder().id(approvalId).build();

        given(approvalRepository.findById(approvalId)).willReturn(Optional.of(approval));

        // When & Then
        assertThatThrownBy(() -> approvalService.sign(param, reqUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 취소된 인계 절차입니다.");
    }

    @Test
    @DisplayName("특수 케이스 (다중 권한): 인계자이자 관리자인 유저가 두 턴 모두 정상적으로 서명한다")
    void sign_multiple_roles_success() {
        // --- 1. 인계자 턴 서명 ---
        // Given
        Long reqUserId = 100L; // 인계자이면서 관리자
        Long approvalId = 1L;
        Long spaceId = 10L;
        Approval approval = Approval.of(StepStatus.ASSIGNOR_TURN, spaceId);
        ReflectionTestUtils.setField(approval, "id", approvalId);

        UserApproval assignorApproval = UserApproval.of(ApprovalRole.ASSIGNOR, reqUserId, approvalId);
        ReflectionTestUtils.setField(assignorApproval, "id", 1000L);

        ApprovalDto.UpdateReqDto param = ApprovalDto.UpdateReqDto.builder().id(approvalId).build();

        // 1턴(인계자) Mocking
        given(approvalRepository.findById(approvalId)).willReturn(Optional.of(approval));
        given(approvalMapper.hasRoleByApprovalIdAndUserId(approvalId, reqUserId, ApprovalRole.ASSIGNOR.name()))
                .willReturn(true);
        given(userApprovalRepository.findByApprovalIdAndApprovalRole(approvalId, ApprovalRole.ASSIGNOR))
                .willReturn(Optional.of(assignorApproval));

        // When
        approvalService.sign(param, reqUserId);

        // Then (인계자 서명 검증)
        assertThat(approval.getStepStatus()).isEqualTo(StepStatus.ASSIGNEE_TURN);
        verify(userApprovalService, times(1)).update(any(UserApprovalDto.UpdateReqDto.class), eq(reqUserId));
        verify(approvalRepository, times(1)).save(approval);
        
        
        // --- (인수자 턴은 건너뛰었다고 가정하고, 관리자 턴으로 수동 변경) ---
        approval.setStepStatus(StepStatus.ADMIN_TURN);
        
        // --- 2. 관리자 턴 서명 ---
        // Given
        UserApproval adminApproval = UserApproval.of(ApprovalRole.ADMIN, reqUserId, approvalId);
        ReflectionTestUtils.setField(adminApproval, "id", 1002L);

        // 2턴(관리자) Mocking
        given(approvalMapper.hasRoleByApprovalIdAndUserId(approvalId, reqUserId, ApprovalRole.ADMIN.name()))
                .willReturn(true);
        given(userApprovalRepository.findByApprovalIdAndApprovalRole(approvalId, ApprovalRole.ADMIN))
                .willReturn(Optional.of(adminApproval));

        Long assignorUserSpaceId = 200L;
        Long assigneeUserSpaceId = 201L;
        given(approvalMapper.findUserSpaceIdByStatus(spaceId, UserSpaceStatus.ACTIVE.toString()))
                .willReturn(assignorUserSpaceId);
        given(approvalMapper.findUserSpaceIdByStatus(spaceId, UserSpaceStatus.PENDING.toString()))
                .willReturn(assigneeUserSpaceId);

        // When
        approvalService.sign(param, reqUserId);

        // Then (관리자 서명 검증)
        assertThat(approval.getStepStatus()).isEqualTo(StepStatus.COMPLETED);
        
        // 총 2번씩 업데이트 됨 (1턴 + 2턴)
        verify(userApprovalService, times(2)).update(any(UserApprovalDto.UpdateReqDto.class), eq(reqUserId));
        verify(approvalRepository, times(2)).save(approval);
        
        // 관리자 턴 서명 시 권한 인계가 발생하여 2번 추가 업데이트
        verify(userSpaceService, times(2)).update(any(UserSpaceDto.UpdateReqDto.class));
    }
}