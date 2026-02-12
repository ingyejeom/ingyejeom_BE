package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.PermissionuserDto;

import java.util.List;

public interface PermissionuserService {
    DefaultDto.CreateResDto create(PermissionuserDto.CreateReqDto param, Long reqUserId);
    void update(PermissionuserDto.UpdateReqDto param, Long reqUserId);
    void delete(PermissionuserDto.UpdateReqDto param, Long reqUserId);
    PermissionuserDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);
    List<PermissionuserDto.DetailResDto> list(PermissionuserDto.ListReqDto param, Long reqUserId);
}
