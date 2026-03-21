package com.thc.capstone.mapper;

import com.thc.capstone.dto.UserSpaceDto;

import java.util.List;
import java.util.Map;

public interface UserSpaceMapper {
    UserSpaceDto.DetailResDto detail(Long id);
    List<UserSpaceDto.DetailResDto> list(UserSpaceDto.ListReqDto param);

    // 그룹 수정 권한 확인
    boolean isGroupAdmin(Map<String, Object> param);

    // 스페이스 수정 권한 확인
    boolean isSpaceActive(Map<String, Object> param);
}
