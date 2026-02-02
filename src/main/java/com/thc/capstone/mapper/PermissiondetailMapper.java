package com.thc.capstone.mapper;

import com.thc.capstone.dto.PermissiondetailDto;

import java.util.List;

public interface PermissiondetailMapper {
    PermissiondetailDto.DetailResDto detail(Long id);
    List<PermissiondetailDto.DetailResDto> list(PermissiondetailDto.ListReqDto param);
}