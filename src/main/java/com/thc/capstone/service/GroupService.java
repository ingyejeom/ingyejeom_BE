package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {
    DefaultDto.CreateResDto create(GroupDto.CreateReqDto param);

    void update(GroupDto.UpdateReqDto param);

    void delete(GroupDto.UpdateReqDto param);

    GroupDto.DetailResDto detail(DefaultDto.DetailReqDto param);

    List<GroupDto.DetailResDto> list(GroupDto.ListReqDto param);
}
