package com.thc.capstone.mapper;

import com.thc.capstone.dto.UserDto;

import java.util.List;

public interface UserMapper {
    UserDto.DetailResDto detail(Long id);
    List<UserDto.DetailResDto> list(UserDto.ListReqDto param);
}
