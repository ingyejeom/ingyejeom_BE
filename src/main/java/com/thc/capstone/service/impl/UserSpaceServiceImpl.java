package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.mapper.UserSpaceMapper;
import com.thc.capstone.repository.SpaceRepository;
import com.thc.capstone.repository.UserRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.UserSpaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserSpaceServiceImpl implements UserSpaceService {
    final UserSpaceRepository userSpaceRepository;
    final UserSpaceMapper userSpaceMapper;
    final SpaceRepository spaceRepository;
    final UserRepository userRepository;

    // 스페이스 참여
    @Override
    @Transactional
    public void join(UserSpaceDto.JoinReqDto param, Long reqUserId) {
        Space space = spaceRepository.findBySpaceCode(param.getSpaceCode())
                .orElseThrow(() -> new RuntimeException("유효하지 않은 스페이스 코드입니다."));

        // 현재 Space의 주인 찾기
        List<UserSpace> activeUsers = userSpaceRepository.findAllBySpaceIdAndRoleAndStatus(
                space.getId(), Role.USER, UserSpaceStatus.ACTIVE
        );

        for (UserSpace activeUser : activeUsers) {
            // Space 주인이 본인이면 에러
            if (activeUser.getUserId().equals(reqUserId)) {
                throw new RuntimeException("이미 참여 중인 스페이스입니다.");
            }

            // Space 주인이 본인이 아니라면 해당 유저를 INACTIVE
            activeUser.setStatus(UserSpaceStatus.INACTIVE);
        }

        // 이전에 Space에 들어왔던 적이 있는지 확인
        UserSpace myUserSpace = userSpaceRepository.findByUserIdAndSpaceIdAndRole(
                reqUserId, space.getId(), Role.USER
        ).orElse(null);

        if (myUserSpace != null) {
            // Space에 들어왔다가 INACTIVE 됐다면 다시 ACTIVE
            myUserSpace.setStatus(UserSpaceStatus.ACTIVE);
        } else {
            // 처음 들어오는 경우에는 생성
            UserSpace newUserSpace = UserSpace.of(
                    Role.USER,
                    UserSpaceStatus.ACTIVE,
                    reqUserId,
                    space.getId()
            );
            userSpaceRepository.save(newUserSpace);
        }
    }

    // 스페이스 초대
    @Override
    @Transactional
    public void invite(UserSpaceDto.InviteReqDto param, Long reqUserId) {
        User targetUser = userRepository.findByEmail(param.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 이메일을 가진 유저가 없습니다."));

        Space space = spaceRepository.findById(param.getSpaceId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 스페이스입니다."));

        // Space의 주인 찾기
        List<UserSpace> activeUsers = userSpaceRepository.findAllBySpaceIdAndRoleAndStatus(
                space.getId(), Role.USER, UserSpaceStatus.ACTIVE
        );

        for (UserSpace activeUser : activeUsers) {
            // Space의 주인이 초대하려는 유저라면 에러
            if (activeUser.getUserId().equals(targetUser.getId())) {
                throw new RuntimeException("이미 해당 스페이스에 참여 중인 유저입니다.");
            }

            // Space의 주인이 초대하려는 유저가 아니라면 현재 유저 INACTIVE
            activeUser.setStatus(UserSpaceStatus.INACTIVE);
        }

        // 초대하려는 유저가 이전에 Space에 들어온 적 있는지 확인
        UserSpace targetUserSpace = userSpaceRepository.findByUserIdAndSpaceIdAndRole(
                targetUser.getId(), space.getId(), Role.USER
        ).orElse(null);

        if (targetUserSpace != null) {
            // 들어왔던 적 있다면 INACTIVE를 ACTIVE로 변경
            targetUserSpace.setStatus(UserSpaceStatus.ACTIVE);
        } else {
            // 처음이면 생성
            UserSpace newUserSpace = UserSpace.of(
                    Role.USER,
                    UserSpaceStatus.ACTIVE,
                    targetUser.getId(),
                    space.getId()
            );
            userSpaceRepository.save(newUserSpace);
        }
    }

    @Override
    public DefaultDto.CreateResDto create(UserSpaceDto.CreateReqDto param) {
        return userSpaceRepository.save(param.toEntity()).toCreateResDto();
    }

    @Override
    public void update(UserSpaceDto.UpdateReqDto param) {
        UserSpace userSpace = userSpaceRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        userSpace.update(param);
        userSpaceRepository.save(userSpace);
    }

    @Override
    public void delete(UserSpaceDto.UpdateReqDto param) {
        update(UserSpaceDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build());
    }

    public UserSpaceDto.DetailResDto get(DefaultDto.DetailReqDto param) {
        return userSpaceMapper.detail(param.getId());
    }

    @Override
    public UserSpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param) {
        return get(param);
    }

    /**
     * 함수를 통해 반환한 리스트의 ID를 재리스트화
     */
    public List<UserSpaceDto.DetailResDto> addlist(List<UserSpaceDto.DetailResDto> list){
        List<UserSpaceDto.DetailResDto> newList = new ArrayList<>();
        for(UserSpaceDto.DetailResDto userSpace : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(userSpace.getId())
                    .build()));
        }

        return newList;
    }

    @Override
    public List<UserSpaceDto.DetailResDto> list(UserSpaceDto.ListReqDto param, Long reqUserId) {
        Map<String, Object> map = new HashMap<>();
        map.put("reqUserId", reqUserId);
        map.put("deleted", false);
        map.put("status", param.getStatus() != null ? param.getStatus() : "ACTIVE");

        if(param.getRole() != null){
            map.put("role", param.getRole());
        }

        if(param.getGroupId() != null){
            map.put("groupId", param.getGroupId());
        }

        List<UserSpaceDto.DetailResDto> idList = userSpaceMapper.list(map);

        return addlist(idList);
    }
}
