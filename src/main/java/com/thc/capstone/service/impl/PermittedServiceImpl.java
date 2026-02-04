package com.thc.capstone.service.impl;

import com.thc.capstone.dto.PermissionDto;
import com.thc.capstone.exception.NoPermissionException;
import com.thc.capstone.mapper.PermissionMapper;
import com.thc.capstone.service.PermittedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PermittedServiceImpl implements PermittedService {
    final PermissionMapper permissionMapper;

    @Override
    public void check(String target, Integer func, Long userId) {
        boolean isAble = isPermitted(target, func, userId);
        if (!isAble) {
            throw new NoPermissionException("No Permission");
        }
    }

    @Override
    public boolean isPermitted(String target, Integer func, Long userId) {
        if(userId != null && userId == (long) -200) {
            // 무조건 승인
            return true;
        }

        int listCount = permissionMapper.isPermitted(PermissionDto.IsPermittedReqDto.builder()
                .target(target)
                .func(func)
                .userId(userId)
                .build());

        return listCount > 0;
    }
}