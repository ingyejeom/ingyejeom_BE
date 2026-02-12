package com.thc.capstone.service.impl;

import com.thc.capstone.domain.Permission;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.PermissionDto;
import com.thc.capstone.dto.PermissiondetailDto;
import com.thc.capstone.mapper.PermissionMapper;
import com.thc.capstone.repository.PermissionRepository;
import com.thc.capstone.service.PermissionService;
import com.thc.capstone.service.PermissiondetailService;
import com.thc.capstone.service.PermittedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements PermissionService {
    final PermissionRepository permissionRepository;
    final PermissionMapper permissionMapper;
    final PermissiondetailService permissiondetailService;
    final PermittedService permittedService;

    String target = "permission";

    @Override
    public List<String> access(Long reqUserId) {
        return permissionMapper.access(reqUserId);
    }

    @Override
    public DefaultDto.CreateResDto create(PermissionDto.CreateReqDto param, Long reqUserId) {
        permittedService.check(target, 110, reqUserId);
        DefaultDto.CreateResDto res = permissionRepository.save(param.toEntity()).toCreateResDto();

        return res;
    }

    @Override
    public void update(PermissionDto.UpdateReqDto param, Long reqUserId) {
        permittedService.check(target, 120, reqUserId);
        Permission permission = permissionRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("권한을 찾을 수 없습니다"));
        permission.update(param);
        permissionRepository.save(permission);
    }

    @Override
    public void delete(PermissionDto.UpdateReqDto param, Long reqUserId) {
        update(PermissionDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    public PermissionDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
        permittedService.check(target, 200, reqUserId);
        PermissionDto.DetailResDto res = permissionMapper.detail(param.getId());

        res.setDetails(permissiondetailService.list(PermissiondetailDto.ListReqDto.builder().deleted(false).permissionId(res.getId()).build(), reqUserId));
        res.setTargets(PermissionDto.targets);

        return res;
    }

    @Override
    public PermissionDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    public List<PermissionDto.DetailResDto> addlist(List<PermissionDto.DetailResDto> list, Long reqUserId) {
        List<PermissionDto.DetailResDto> newList = new ArrayList<>();
        for (PermissionDto.DetailResDto permission : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder().id(permission.getId()).build(), reqUserId));
        }
        return newList;
    }

    @Override
    public List<PermissionDto.DetailResDto> list(PermissionDto.ListReqDto param, Long reqUserId) {
        List<PermissionDto.DetailResDto> permissions = permissionMapper.list(param);
        return addlist(permissions, reqUserId);
    }
}
