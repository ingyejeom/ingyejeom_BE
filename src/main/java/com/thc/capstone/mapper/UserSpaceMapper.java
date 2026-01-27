package com.thc.capstone.mapper;

import com.thc.capstone.dto.UserSpaceDto;

import java.util.List;

public interface UserSpaceMapper {
    UserSpaceDto.DetailResDto detail(Long id);
    List<UserSpaceDto.DetailResDto> list(UserSpaceDto.ListReqDto param);
}
