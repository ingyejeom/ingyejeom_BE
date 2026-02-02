package com.thc.capstone.mapper;

import com.thc.capstone.dto.PermissionuserDto;

import java.util.List;

public interface PermissionuserMapper {
    PermissionuserDto.DetailResDto detail(Long id);
    List<PermissionuserDto.DetailResDto> list(PermissionuserDto.ListReqDto param);
}