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

        // 로그인이 되어있지 않을 시 예외 발생
        if (reqUserId == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        // 그룹 생성과 스페이스 목록 생성을 한 묶음으로 처리
        try {
            // Group 생성
            Group group = Group.of(param.getGroupName());
            groupRepository.save(group);

            // Space 목록 생성
            for(GroupDto.CreateReqDto.SpaceInfo info : param.getSpaces()){
                SpaceDto.CreateReqDto spaceParam = SpaceDto.CreateReqDto.builder()
                        .groupId(group.getId())
                        .workName(info.getWorkName())
                        .userEmail(info.getUserEmail())
                        .build();

                // SpaceService 의 create 사용
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
        // 존재하는 그룹인지 검증
        Group group = groupRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        // 그룹 정보 수정 권한이 있는지 검증
        if (!userSpaceMapper.isGroupAdmin(Map.of("userId", reqUserId, "groupId", group.getId()))) {
            permittedService.check(target, 120, reqUserId);
        }

        // 그룹 정보 수정 적용 및 DB 저장
        group.update(param);
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void delete(GroupDto.UpdateReqDto param, Long reqUserId) {
        // 존재하는 그룹인지 검증
        Group group = groupRepository.findById(param.getId())
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 그룹"));

        // 그룹에 속해있는 스페이스 삭제
        List<Space> spaces = spaceRepository.findByGroupId(group.getId());
        for(Space space : spaces) {
            SpaceDto.UpdateReqDto spaceDelete = SpaceDto.UpdateReqDto.builder()
                    .id(space.getId())
                    .build();

            spaceService.delete(spaceDelete, reqUserId);
        }

        // 그룹 삭제
        update(GroupDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    // Mapper 를 이용한 그룹 정보 조회 함수
    public GroupDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
//        permittedService.check(target, 200, reqUserId);

        GroupDto.DetailResDto res = groupMapper.detail(param.getId());

        return res;
    }

    @Override
    public GroupDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    // Mapper 를 통해 받은 그룹 리스트의 ID 값을 이용해 객체 리스트로 넘김
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
