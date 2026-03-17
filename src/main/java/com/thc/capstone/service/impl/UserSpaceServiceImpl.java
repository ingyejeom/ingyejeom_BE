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

        // 현재 Space 의 인계자 찾기
        List<UserSpace> activeUsers = userSpaceRepository.findAllBySpaceIdAndRoleAndStatus(
                space.getId(), Role.USER, UserSpaceStatus.ACTIVE
        );

        for (UserSpace activeUser : activeUsers) {
            // Space 주인이 본인이면 예외 발생
            if (activeUser.getUserId().equals(reqUserId)) {
                throw new RuntimeException("이미 참여 중인 스페이스입니다.");
            }

            // Space 인계자가 본인이 아니라면 해당 유저를 INACTIVE
            activeUser.update(UserSpaceDto.UpdateReqDto.builder()
                    .status(UserSpaceStatus.INACTIVE)
                    .build());
        }

        // 이전에 Space 에 들어왔던 적이 있는지 확인
        UserSpace myUserSpace = userSpaceRepository.findByUserIdAndSpaceIdAndRole(
                reqUserId, space.getId(), Role.USER
        ).orElse(null);

        if (myUserSpace != null) {
            // Space 에 들어왔던 적 있으면 INACTIVE 에서 다시 ACTIVE 로 변경
            myUserSpace.update(UserSpaceDto.UpdateReqDto.builder()
                    .status(UserSpaceStatus.ACTIVE)
                    .build());
        } else {
            // 처음 들어오는 경우에는 새로운 유저-스페이스 생성
            create(UserSpaceDto.CreateReqDto.builder()
                    .role(Role.USER)
                    .status(UserSpaceStatus.ACTIVE)
                    .userId(reqUserId)
                    .spaceId(space.getId())
                    .build());
        }
    }

    // 스페이스 초대
    @Override
    @Transactional
    public void invite(UserSpaceDto.InviteReqDto param, Long reqUserId) {
        // 사용자 이메일 존재 여부 검증
        User targetUser = userRepository.findByEmail(param.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 이메일을 가진 유저가 없습니다."));

        // 스페이스 존재 여부 검증
        Space space = spaceRepository.findById(param.getSpaceId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 스페이스입니다."));

        // Space 의 현재 인계자 찾기
        List<UserSpace> activeUsers = userSpaceRepository.findAllBySpaceIdAndRoleAndStatus(
                space.getId(), Role.USER, UserSpaceStatus.ACTIVE
        );

        for (UserSpace activeUser : activeUsers) {
            // Space 의 인계자가 초대하려는 유저라면 에러
            if (activeUser.getUserId().equals(targetUser.getId())) {
                throw new RuntimeException("이미 해당 스페이스에 참여 중인 유저입니다.");
            }

            // Space 의 인계자가 초대하려는 유저가 아니라면 현재 유저 INACTIVE
            activeUser.update(UserSpaceDto.UpdateReqDto.builder()
                            .status(UserSpaceStatus.INACTIVE)
                            .build());
        }

        // 초대하려는 유저가 이전에 Space 에 들어온 적 있는지 확인
        UserSpace targetUserSpace = userSpaceRepository.findByUserIdAndSpaceIdAndRole(
                targetUser.getId(), space.getId(), Role.USER
        ).orElse(null);

        if (targetUserSpace != null) {
            // 들어왔던 적 있다면 INACTIVE 를 ACTIVE 로 변경
            targetUserSpace.update(UserSpaceDto.UpdateReqDto.builder()
                    .status(UserSpaceStatus.ACTIVE)
                    .build());
        } else {
            // 처음이면 생성
            create(UserSpaceDto.CreateReqDto.builder()
                    .role(Role.USER)
                    .status(UserSpaceStatus.ACTIVE)
                    .userId(targetUser.getId())
                    .spaceId(space.getId())
                    .build());
        }
    }

    @Override
    public DefaultDto.CreateResDto create(UserSpaceDto.CreateReqDto param) {
        return userSpaceRepository.save(param.toEntity()).toCreateResDto();
    }

    @Override
    public void update(UserSpaceDto.UpdateReqDto param) {
        // 유저-스페이스 검증
        UserSpace userSpace = userSpaceRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        // 변경된 정보 적용 및 DB 저장
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

    // Mapper 를 이용한 사용자 정보 조회 함수
    public UserSpaceDto.DetailResDto get(DefaultDto.DetailReqDto param) {
        return userSpaceMapper.detail(param.getId());
    }

    @Override
    public UserSpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param) {
        return get(param);
    }

    // Mapper 를 통해 받은 사용자 리스트의 ID 값을 이용해 객체 리스트로 넘김
    public List<UserSpaceDto.DetailResDto> addlist(List<UserSpaceDto.DetailResDto> list){
        List<UserSpaceDto.DetailResDto> newList = new ArrayList<>();
        for(UserSpaceDto.DetailResDto userSpace : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(userSpace.getId())
                    .build()));
        }

        return newList;
    }

    // 프로필에서 본인이 속한 그룹 및 스페이스를 모두 띄우기
    @Override
    public List<UserSpaceDto.DetailResDto> list(UserSpaceDto.ListReqDto param, Long reqUserId) {
        // Mapper 쿼리에 전달할 파라미너 맵 구성
        Map<String, Object> map = new HashMap<>();

        // 필수 필터 조건 설정
        map.put("reqUserId", reqUserId);
        map.put("deleted", false);

        // 요청한 상태 적용 (기본 : ACTIVE)
        map.put("status", param.getStatus() != null ? param.getStatus() : "ACTIVE");

        // N + 1 문제 방지를 위해 ID 목록 조회
        List<UserSpaceDto.DetailResDto> idList = userSpaceMapper.list(map);

        return addlist(idList);
    }

    // 대시보드에 현재 사용자가 속한 스페이스
    @Override
    public List<UserSpaceDto.DetailResDto> getDashboardSpaces(Long reqUserId) {
        // Mapper 쿼리에 전달할 파라미너 맵 구성
        Map<String, Object> map = new HashMap<>();

        // 필수 필터 조건 설정
        map.put("reqUserId", reqUserId);
        map.put("deleted", false);

        map.put("status", "ACTIVE");

        // USER 로 존재하는 스페이스만 필터링
        map.put("role", "USER");

        // N + 1 문제 방지를 위해 ID 목록 조회
        List<UserSpaceDto.DetailResDto> idList = userSpaceMapper.list(map);

        return addlist(idList);
    }

    // 그룹관리를 위해 해당 그룹에서 관리자로 할당되어 있는 스페이스
    @Override
    public List<UserSpaceDto.DetailResDto> getAdminSpaces(UserSpaceDto.ListReqDto param, Long reqUserId) {
        // Mapper 쿼리에 전달할 파라미너 맵 구성
        Map<String, Object> map = new HashMap<>();

        // 필수 필터 조건 설정
        map.put("reqUserId", reqUserId);
        map.put("deleted", false);

        map.put("status", "ACTIVE");

        map.put("role", "ADMIN");

        if(param.getGroupId() != null){
            map.put("groupId", param.getGroupId());
        } else {
            map.put("groupId", null);
        }

        // N + 1 문제 방지를 위해 ID 목록 조회
        List<UserSpaceDto.DetailResDto> idList = userSpaceMapper.list(map);

        return addlist(idList);
    }
}
