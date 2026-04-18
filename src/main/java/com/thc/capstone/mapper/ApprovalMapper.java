package com.thc.capstone.mapper;

import com.thc.capstone.domain.ApprovalRole;
import com.thc.capstone.dto.ApprovalDto;

import java.util.List;

public interface ApprovalMapper {
    ApprovalDto.DetailResDto detail(Long id);

    List<ApprovalDto.DetailResDto> list(ApprovalDto.ListReqDto param);

    Long findAssignorIdBySpaceId(Long spaceId);

    Long findAdminIdBySpaceId(Long spaceId);

    boolean hasRoleByApprovalIdAndUserId(Long approvalId, Long reqUserId, String requiredRole);

    Long findUserSpaceIdByStatus(Long spaceId, String status);
}
