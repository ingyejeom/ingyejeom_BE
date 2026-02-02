package com.thc.capstone.service.impl;

import com.thc.capstone.domain.Group;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import com.thc.capstone.mapper.GroupMapper;
import com.thc.capstone.repository.GroupRepository;
import com.thc.capstone.service.GroupService;
import com.thc.capstone.service.PermittedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupServiceImpl implements GroupService {
    final GroupRepository groupRepository;
    final GroupMapper groupMapper;
    final PermittedService permittedService;

    String target = "group";

    @Override
    public DefaultDto.CreateResDto create(GroupDto.CreateReqDto param, Long reqUserId) {
        // permittedService.check(target, 110, reqUserId);

        return groupRepository.save(param.toEntity()).toCreateResDto();
    }

    @Override
    public void update(GroupDto.UpdateReqDto param, Long reqUserId) {
        // permittedService.check(target, 120, reqUserId);

        Group group = groupRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        group.update(param);
        groupRepository.save(group);
    }

    @Override
    public void delete(GroupDto.UpdateReqDto param, Long reqUserId) {
        update(GroupDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    public GroupDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
        // permittedService.check(target, 200, reqUserId);

        GroupDto.DetailResDto res = groupMapper.detail(param.getId());

        return res;
    }

    @Override
    public GroupDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    /**
     * 함수를 통해 반환한 리스트의 ID를 재리스트화
     */
    public List<GroupDto.DetailResDto> addlist(List<GroupDto.DetailResDto> list, Long reqUserId){
        List<GroupDto.DetailResDto> newList = new ArrayList<>();
        for(GroupDto.DetailResDto group : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(group.getId())
                    .build(), reqUserId));
        }

        return newList;
    }

    @Override
    public List<GroupDto.DetailResDto> list(GroupDto.ListReqDto param, Long reqUserId) {
        return addlist(groupMapper.list(param), reqUserId);
    }
}
