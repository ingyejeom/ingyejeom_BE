package com.thc.capstone.service.impl;

import com.thc.capstone.domain.Permissiondetail;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.PermissiondetailDto;
import com.thc.capstone.mapper.PermissiondetailMapper;
import com.thc.capstone.repository.PermissiondetailRepository;
import com.thc.capstone.service.PermissiondetailService;
import com.thc.capstone.service.PermittedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PermissiondetailServiceImpl implements PermissiondetailService {
    final PermissiondetailRepository permissiondetailRepository;
    final PermissiondetailMapper permissiondetailMapper;
    final PermittedService permittedService;
    String target = "permission";

    @Override
    public void toggle(PermissiondetailDto.ToggleReqDto param, Long reqUserId) {
        permittedService.check(target, 110, reqUserId);
        permittedService.check(target, 120, reqUserId);

        Permissiondetail permissiondetail = permissiondetailRepository.findByPermissionIdAndTargetAndFunc(param.getPermissionId(), param.getTarget(), param.getFunc());

        if(permissiondetail != null) {
            create(PermissiondetailDto.CreateReqDto.builder()
                    .permissionId(param.getPermissionId())
                    .target(param.getTarget())
                    .func(param.getFunc())
                    .build(), reqUserId);
        } else {
            permissiondetail.setDeleted(!param.getFlag());
            permissiondetailRepository.save(permissiondetail);
        }
    }

    /**/

    @Override
    public DefaultDto.CreateResDto create(PermissiondetailDto.CreateReqDto param, Long reqUserId) {
        permittedService.check(target, 110, reqUserId);

        Permissiondetail permissiondetail = permissiondetailRepository.findByPermissionIdAndTargetAndFunc(param.getPermissionId(), param.getTarget(), param.getFunc());
        if (permissiondetail != null) {
            return permissiondetail.toCreateResDto();
        }

        DefaultDto.CreateResDto res = permissiondetailRepository.save(param.toEntity()).toCreateResDto();

        return res;
    }

    @Override
    public void update(PermissiondetailDto.UpdateReqDto param, Long reqUserId) {
        permittedService.check(target, 120, reqUserId);

        Permissiondetail permissiondetail = permissiondetailRepository.findById(param.getId()).orElseThrow(() -> new RuntimeException("no data"));

        permissiondetail.update(param);
        permissiondetailRepository.save(permissiondetail);
    }

    @Override
    public void delete(PermissiondetailDto.UpdateReqDto param, Long reqUserId) {
        update(PermissiondetailDto.UpdateReqDto.builder().id(param.getId()).deleted(true).build(), reqUserId);
    }

    public PermissiondetailDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
        permittedService.check(target, 200, reqUserId);

        PermissiondetailDto.DetailResDto res = permissiondetailMapper.detail(param.getId());

        return res;
    }

    @Override
    public PermissiondetailDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    public List<PermissiondetailDto.DetailResDto> addlist(List<PermissiondetailDto.DetailResDto> list, Long reqUserId) {
        List<PermissiondetailDto.DetailResDto> newList = new ArrayList<>();
        for (PermissiondetailDto.DetailResDto permissiondetail : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder().id(permissiondetail.getId()).build(), reqUserId));
        }
        return newList;
    }

    @Override
    public List<PermissiondetailDto.DetailResDto> list(PermissiondetailDto.ListReqDto param, Long reqUserId) {
        List<PermissiondetailDto.DetailResDto> list = new ArrayList<>();
        List<PermissiondetailDto.DetailResDto> permissiondetails = permissiondetailMapper.list(param);
        return addlist(permissiondetails, reqUserId);
    }
}