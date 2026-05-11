package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.exception.HandoverInProgressException;
import com.thc.capstone.mapper.ApprovalMapper;
import com.thc.capstone.mapper.SpaceMapper;
import com.thc.capstone.mapper.UserSpaceMapper;
import com.thc.capstone.repository.GroupRepository;
import com.thc.capstone.repository.SpaceRepository;
import com.thc.capstone.repository.UserRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.PermittedService;
import com.thc.capstone.service.SpaceService;
import com.thc.capstone.service.UserSpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpaceServiceImpl implements SpaceService {
    final SpaceRepository spaceRepository;
    final UserSpaceRepository userSpaceRepository;
    final GroupRepository groupRepository;
    final SpaceMapper spaceMapper;
    final PermittedService permittedService;
    final UserRepository userRepository;

    final UserSpaceService userSpaceService;

    // 스페이스 수정 시 권한 체크를 위함
    final UserSpaceMapper userSpaceMapper;

    final ApprovalMapper approvalMapper;

    String target = "space";

    @Override
    @Transactional
    public DefaultDto.CreateResDto create(SpaceDto.CreateReqDto param, Long reqUserId) {
//        permittedService.check(target, 110, reqUserId);

        // 로그인이 되어있지 않을 시 예외 발생
        if (reqUserId == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        // 스페이스 생성과 유저 스페이스 생성을 한 묶음으로 처리
        try{
            // 존재하는 그룹인지 검증
            Group group = groupRepository.findById(param.getGroupId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 그룹"));

            // 스페이스 코드를 UUID 로 Unique 하게 생성
            String uniqueSpaceCode = UUID.randomUUID().toString();

            // 스페이스 생성 및 DB 저장
            Space space = Space.of(param.getWorkName(), uniqueSpaceCode, group.getId());
            spaceRepository.save(space);

            // 관리자 역할의 유저 스페이스 생성 및 DB 저장
            userSpaceService.create(UserSpaceDto.CreateReqDto.builder()
                    .role(Role.ADMIN)
                    .status(UserSpaceStatus.ACTIVE)
                    .userId(reqUserId)
                    .spaceId(space.getId())
                    .build());

            // 사용자 역할의 유저 스페이스 생성 및 DB 저장
                // 입력한 유저 이메일이 있다면 해당 유저에게 할당
                // 그렇지 않다면 요청한 유저에게 할당
            Long targetUserId = reqUserId;

            // 이메일이 입력된 경우에만 조회를 시도합니다.
            if (param.getUserEmail() != null && !param.getUserEmail().trim().isEmpty()) {
                targetUserId = userRepository.findByEmail(param.getUserEmail())
                        .map(User::getId) // 유저가 존재하면 해당 유저의 ID를 추출합니다.
                        .orElse(reqUserId); // 유저가 존재하지 않으면 다시 기본값(reqUserId)을 사용합니다.
            }

            // 결정된 targetUserId를 바탕으로 유저-스페이스 관계를 한 번만 생성합니다.
            userSpaceService.create(UserSpaceDto.CreateReqDto.builder()
                    .role(Role.USER)
                    .status(UserSpaceStatus.ACTIVE)
                    .userId(targetUserId)
                    .spaceId(space.getId())
                    .build());

            return space.toCreateResDto();
        } catch (Exception e) {
            log.error("스페이스 추가 실패 : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(SpaceDto.UpdateReqDto param, Long reqUserId) {
        // 존재하는 스페이스인지 검증
        Space space = spaceRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        if (approvalMapper.isHandoverInProgress(space.getId())) {
            throw new HandoverInProgressException("현재 인수인계가 진행 중이므로 스페이스 정보를 변경할 수 없습니다.");
        }

        // 스페이스 관리 권한 검증
        if (!userSpaceMapper.isSpaceActive(Map.of("userId", reqUserId, "spaceId", space.getId()))) {
            permittedService.check(target, 120, reqUserId);
        }

        // 수정 적용 및 DB 저장
        space.update(param);
        spaceRepository.save(space);
    }

    @Override
    public void delete(SpaceDto.UpdateReqDto param, Long reqUserId) {
        update(SpaceDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);

        // 스페이스 삭제 시 연관된 모든 UserSpace 데이터도 일괄 삭제 처리
        userSpaceService.deleteBySpaceId(param.getId());
    }

    // Mapper 를 이용한 사용자 정보 조회 함수
    public SpaceDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
//        permittedService.check(target, 200, reqUserId);

        SpaceDto.DetailResDto res = spaceMapper.detail(param.getId());

        return res;
    }

    @Override
    public SpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    // Mapper 를 통해 받은 사용자 리스트의 ID 값을 이용해 객체 리스트로 넘김
    public List<SpaceDto.DetailResDto> addlist(List<SpaceDto.DetailResDto> list, Long reqUserId){
        List<SpaceDto.DetailResDto> newList = new ArrayList<>();
        for(SpaceDto.DetailResDto space : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(space.getId())
                    .build(), reqUserId));
        }

        return newList;
    }

    @Override
    public List<SpaceDto.DetailResDto> list(SpaceDto.ListReqDto param, Long reqUserId) {
        List<SpaceDto.DetailResDto> spaces = spaceMapper.list(param);

        return addlist(spaces, reqUserId);
    }
}
