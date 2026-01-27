package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserSpaceDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserSpaceService {
    DefaultDto.CreateResDto create(UserSpaceDto.CreateReqDto param);

    void update(UserSpaceDto.UpdateReqDto param);

    void delete(UserSpaceDto.UpdateReqDto param);

    UserSpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param);

    List<UserSpaceDto.DetailResDto> list(UserSpaceDto.ListReqDto param);
}
