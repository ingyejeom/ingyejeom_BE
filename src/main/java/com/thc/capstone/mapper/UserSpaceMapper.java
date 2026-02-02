package com.thc.capstone.mapper;

import com.thc.capstone.dto.UserSpaceDto;

import java.util.List;
import java.util.Map;

public interface UserSpaceMapper {
    UserSpaceDto.DetailResDto detail(Long id);
    List<UserSpaceDto.DetailResDto> list(Map<String, Object> param);
}
