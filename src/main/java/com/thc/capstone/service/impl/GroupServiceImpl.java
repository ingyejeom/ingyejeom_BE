package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.mapper.GroupMapper;
import com.thc.capstone.mapper.UserSpaceMapper;
import com.thc.capstone.repository.GroupRepository;
import com.thc.capstone.repository.SpaceRepository;
import com.thc.capstone.service.GroupService;
import com.thc.capstone.service.PermittedService;
import com.thc.capstone.service.SpaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupServiceImpl implements GroupService {
    final GroupRepository groupRepository;
    final GroupMapper groupMapper;
    final PermittedService permittedService;

    // 그룹 삭제시 스페이스들도 연쇄 삭제를 위함
    final SpaceService spaceService;
    final SpaceRepository spaceRepository;

    // 그룹 이름 변경 시 권한 체크를 위함
    final UserSpaceMapper userSpaceMapper;

    String target = "group";

    @Override
    public DefaultDto.CreateResDto create(GroupDto.CreateReqDto param, Long reqUserId) {
//        permittedService.check(target, 110, reqUserId);
        if (reqUserId == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        try {
            // Group 생성
            Group group = Group.of(param.getGroupName());
            groupRepository.save(group);

            // Space 목록 생성 (자식)
            for(GroupDto.CreateReqDto.SpaceInfo info : param.getSpaces()){
                SpaceDto.CreateReqDto spaceParam = SpaceDto.CreateReqDto.builder()
                        .groupId(group.getId())
                        .workName(info.getWorkName())
                        .userEmail(info.getUserEmail())
                        .build();

                spaceService.create(spaceParam, reqUserId);
            }

            return group.toCreateResDto();
        } catch (Exception e) {
            log.error("스페이스 생성 중 실패! 원인: {}", e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(GroupDto.UpdateReqDto param, Long reqUserId) {
        Group group = groupRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        if (!userSpaceMapper.isGroupAdmin(Map.of("userId", reqUserId, "groupId", group.getId()))) {
            permittedService.check(target, 120, reqUserId);
        }

        group.update(param);
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void delete(GroupDto.UpdateReqDto param, Long reqUserId) {
        Group group = groupRepository.findById(param.getId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 그룹"));

        List<Space> spaces = spaceRepository.findByGroupId(group.getId());

        for(Space space : spaces) {
            SpaceDto.UpdateReqDto spaceDelete = SpaceDto.UpdateReqDto.builder()
                    .id(space.getId())
                    .build();

            spaceService.delete(spaceDelete, reqUserId);
        }

        update(GroupDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    public GroupDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
//        permittedService.check(target, 200, reqUserId);

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
