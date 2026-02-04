package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {
    DefaultDto.CreateResDto create(GroupDto.CreateReqDto param, Long reqUserId);

    void update(GroupDto.UpdateReqDto param, Long reqUserId);

    void delete(GroupDto.UpdateReqDto param, Long reqUserId);

    GroupDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    List<GroupDto.DetailResDto> list(GroupDto.ListReqDto param, Long reqUserId);
}
