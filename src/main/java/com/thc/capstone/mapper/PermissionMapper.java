package com.thc.capstone.mapper;

import com.thc.capstone.dto.PermissionDto;

import java.util.List;

public interface PermissionMapper {
    List<String> access(Long reqUserId);
    int isPermitted(PermissionDto.IsPermittedReqDto param);
    /**/
    PermissionDto.DetailResDto detail(Long id);
    List<PermissionDto.DetailResDto> list(PermissionDto.ListReqDto param);
}
