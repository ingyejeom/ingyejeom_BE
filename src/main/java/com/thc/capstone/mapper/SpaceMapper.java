package com.thc.capstone.mapper;

import com.thc.capstone.dto.SpaceDto;

import java.util.List;

public interface SpaceMapper {
    SpaceDto.DetailResDto detail(Long id);
    List<SpaceDto.DetailResDto> list(SpaceDto.ListReqDto param);
}
