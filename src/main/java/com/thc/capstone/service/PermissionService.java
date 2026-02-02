package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.PermissionDto;

import java.util.List;

public interface PermissionService {
    List<String> access(Long reqUserId);
    /**/
    DefaultDto.CreateResDto create(PermissionDto.CreateReqDto param, Long reqUserId);
    void update(PermissionDto.UpdateReqDto param, Long reqUserId);
    void delete(PermissionDto.UpdateReqDto param, Long reqUserId);
    PermissionDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);
    List<PermissionDto.DetailResDto> list(PermissionDto.ListReqDto param, Long reqUserId);
}
