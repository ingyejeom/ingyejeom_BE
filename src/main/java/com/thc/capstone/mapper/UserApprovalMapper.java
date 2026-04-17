package com.thc.capstone.mapper;

import com.thc.capstone.dto.UserApprovalDto;

import java.util.List;

public interface UserApprovalMapper {
    UserApprovalDto.DetailResDto detail(Long id);

    List<UserApprovalDto.DetailResDto> list(UserApprovalDto.ListReqDto param);
}
