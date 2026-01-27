package com.thc.capstone.mapper;

import com.thc.capstone.dto.GroupDto;

import java.util.List;

public interface GroupMapper {
    GroupDto.DetailResDto detail(Long id);
    List<GroupDto.DetailResDto> list(GroupDto.ListReqDto param);
}
