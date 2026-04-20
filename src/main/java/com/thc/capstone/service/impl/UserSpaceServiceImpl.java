package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.ApprovalDto;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.mapper.UserSpaceMapper;
import com.thc.capstone.repository.SpaceRepository;
import com.thc.capstone.repository.UserRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.ApprovalService;
import com.thc.capstone.service.UserSpaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    final UserRepository userRepository;

/*
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
    }*/

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

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        List<UserSpace> userSpaces = userSpaceRepository.findAllByUserId(userId);
        for (UserSpace userSpace : userSpaces) {
            delete(UserSpaceDto.UpdateReqDto.builder().id(userSpace.getId()).build());
        }
    }

    @Override
    @Transactional
    public void deleteBySpaceId(Long spaceId) {
        List<UserSpace> userSpaces = userSpaceRepository.findAllBySpaceId(spaceId);
        for (UserSpace userSpace : userSpaces) {
            delete(UserSpaceDto.UpdateReqDto.builder().id(userSpace.getId()).build());
        }
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

    // 기본 유저-스페이스 목록 조회 (Mapper 직접 호출용)
    @Override
    public List<UserSpaceDto.DetailResDto> list(UserSpaceDto.ListReqDto param) {
        // N + 1 문제 방지를 위해 ID 목록 조회
        List<UserSpaceDto.DetailResDto> idList = userSpaceMapper.list(param);

        return addlist(idList);
    }
    @Override
    public DefaultDto.PagedListResDto<UserSpaceDto.DetailResDto> pagedList(UserSpaceDto.PagedListReqDto param) {
        // 조건에 맞는 전체 데이터의 개수를 조회합니다. (페이지 계산용)
        int listCount = userSpaceMapper.listCount(param);

        // offset 과 perPage 에 맞게 잘라온 해당 페이지의 ID 목록을 조회합니다.
        List<UserSpaceDto.DetailResDto> idList = userSpaceMapper.pagedList(param);

        // 기존 흐름대로 ID 목록을 기반으로 조인된 상세 데이터 리스트를 완성합니다.
        List<UserSpaceDto.DetailResDto> dtoList = addlist(idList);

        // 제네릭이 적용된 DTO 의 정적 팩토리 메서드(of)를 호출하여 최종 응답 객체를 깔끔하게 조립합니다.
        return DefaultDto.PagedListResDto.of(param, listCount, dtoList);
    }
    @Override
    public List<UserSpaceDto.DetailResDto> scrollList(UserSpaceDto.ScrollListReqDto param) {
        return addlist(userSpaceMapper.scrollList(param));
    }

    // 프로필에서 본인이 속한 스페이스를 모두 띄우기
    @Override
    public List<UserSpaceDto.DetailResDto> getProfileSpaces(UserSpaceDto.ScrollListReqDto param, Long reqUserId) {
        param.setReqUserId(reqUserId);
        param.setDeleted(false);
        if(param.getStatus() == null) {
//            param.setStatus(UserSpaceStatus.ACTIVE);
            param.setRole(Role.USER);
        }

        return scrollList(param);
    }

    // 대시보드에 현재 사용자가 속한 스페이스
    @Override
    public DefaultDto.PagedListResDto<UserSpaceDto.DetailResDto> getDashboardSpaces(UserSpaceDto.PagedListReqDto param, Long reqUserId) {
        param.setReqUserId(reqUserId);
        param.setDeleted(false);
//        param.setStatus(UserSpaceStatus.ACTIVE);
        param.setRole(Role.USER);
        param.setPerPage(7); // 대시보드는 7개로 강제 고정

        return pagedList(param);
    }

    // 그룹관리를 위해 해당 그룹에서 관리자로 할당되어 있는 스페이스
    @Override
    public List<UserSpaceDto.DetailResDto> getAdminSpaces(UserSpaceDto.ScrollListReqDto param, Long reqUserId) {
        // 프론트에서 넘어온 param 객체(groupId 등 포함)에 서버 필수 조건만 세팅
        param.setReqUserId(reqUserId);
        param.setDeleted(false);
        param.setStatus(UserSpaceStatus.ACTIVE);
        param.setRole(Role.ADMIN);
        
        return scrollList(param);
    }
}
