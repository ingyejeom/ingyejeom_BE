package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SpaceService {
    DefaultDto.CreateResDto create(SpaceDto.CreateReqDto param);

    void update(SpaceDto.UpdateReqDto param);

    void delete(SpaceDto.UpdateReqDto param);

    SpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param);

    List<SpaceDto.DetailResDto> list(SpaceDto.ListReqDto param);
}
