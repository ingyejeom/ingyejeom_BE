package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SpaceService {
    DefaultDto.CreateResDto create(SpaceDto.CreateReqDto param, Long reqUserId);

    void update(SpaceDto.UpdateReqDto param, Long reqUserId);

    void delete(SpaceDto.UpdateReqDto param, Long reqUserId);

    SpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    List<SpaceDto.DetailResDto> list(SpaceDto.ListReqDto param, Long reqUserId);
}
