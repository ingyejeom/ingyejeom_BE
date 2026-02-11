package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.mapper.SpaceMapper;
import com.thc.capstone.mapper.UserSpaceMapper;
import com.thc.capstone.repository.GroupRepository;
import com.thc.capstone.repository.SpaceRepository;
import com.thc.capstone.repository.UserRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.PermittedService;
import com.thc.capstone.service.SpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 스페이스 수정 시 권한 체크를 위함
    final UserSpaceMapper userSpaceMapper;

    String target = "space";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8; // 코드 길이 (예: 8자리)
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public DefaultDto.CreateResDto create(SpaceDto.CreateReqDto param, Long reqUserId) {
//        permittedService.check(target, 110, reqUserId);

        if (reqUserId == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        try{
            Group group = groupRepository.findById(param.getGroupId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 그룹"));

            String uniqueSpaceCode;
            do {
                uniqueSpaceCode = generateRandomCode();
            } while (spaceRepository.existsBySpaceCode(uniqueSpaceCode));

             Space space = Space.of(param.getWorkName(), uniqueSpaceCode, group.getId());
             spaceRepository.save(space);

            UserSpace userSpace = UserSpace.of(
                    Role.ADMIN,
                    UserSpaceStatus.ACTIVE,
                    reqUserId,
                    space.getId()
            );
            userSpaceRepository.save(userSpace);

            if(param.getUserEmail() != null && !param.getUserEmail().trim().isEmpty()){
                userRepository.findByEmail(param.getUserEmail())
                        .ifPresent(assignee -> {
                            UserSpace assigneeUserSpace = UserSpace.of(
                                    Role.USER,
                                    UserSpaceStatus.ACTIVE,
                                    assignee.getId(),
                                    space.getId()
                            );
                            userSpaceRepository.save(assigneeUserSpace);
                        });
            }

            return space.toCreateResDto();
        } catch (Exception e) {
            log.error("스페이스 추가 실패 : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 코드를 랜덤으로 생성
    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    @Override
    public void update(SpaceDto.UpdateReqDto param, Long reqUserId) {
        Space space = spaceRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        if (!userSpaceMapper.isSpaceAdmin(Map.of("userId", reqUserId, "spaceId", space.getId()))) {
            permittedService.check(target, 120, reqUserId);
        }

        space.update(param);
        spaceRepository.save(space);
    }

    @Override
    public void delete(SpaceDto.UpdateReqDto param, Long reqUserId) {
        update(SpaceDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    public SpaceDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
//        permittedService.check(target, 200, reqUserId);

        SpaceDto.DetailResDto res = spaceMapper.detail(param.getId());

        return res;
    }

    @Override
    public SpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    /**
     * 함수를 통해 반환한 리스트의 ID를 재리스트화
     */
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
