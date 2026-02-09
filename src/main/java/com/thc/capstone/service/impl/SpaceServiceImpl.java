package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.mapper.SpaceMapper;
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
    final UserRepository userRepository;
    final UserSpaceRepository userSpaceRepository;
    final GroupRepository groupRepository;
    final SpaceMapper spaceMapper;
    final PermittedService permittedService;

    String target = "space";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8; // 코드 길이 (예: 8자리)
    private final SecureRandom random = new SecureRandom();

    /**
     * 그룹 관리 페이지에서 이미 만들어진 그룹 내에 스페이스를 하나만 추가할 경우
     * groupId, spaceName으로 새로운 space를 생성합니다.
     */
    @Override
    @Transactional
    public void add(SpaceDto.CreateReqDto param, Long reqUserId) {
        permittedService.check(target, 110, reqUserId);

        try{
            User user = userRepository.findById(reqUserId)
                    .orElseThrow(() -> new RuntimeException("유저 없음"));

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
                    user.getId(),
                    space.getId()
            );
            userSpaceRepository.save(userSpace);
        } catch (Exception e) {
            log.error("스페이스 추가 실패 : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Space를 생성할 때,
     * 요청을 한 사람의 ID와 생성된 Space ID를 통해 UserSpace를 생성합니다
     * Default : "ADMIN", "ACTIVE"
     *
     * CreateReqDto에 GroupName도 같이 받아 Group도 생성합니다
     */
    @Override
    @Transactional
    public void create(SpaceDto.MultiCreateReqDto param, Long reqUserId) {
        permittedService.check(target, 110, reqUserId);

        try {
            User user = userRepository.findById(reqUserId)
                    .orElseThrow(() -> new RuntimeException("유저 없음"));

            // Group 생성
            Group group = Group.of(param.getGroupName());
            groupRepository.save(group);

            // Space 목록 생성 (자식)
            for(String workName : param.getWorkNames()){
                // 코드 생성
                String uniqueSpaceCode;
                // Unique한 코드가 생성될 때까지 반복
                do {
                    uniqueSpaceCode = generateRandomCode();
                } while (spaceRepository.existsBySpaceCode(uniqueSpaceCode));

                // Space 생성 (groupId 주입)
                Space space = Space.of(workName, uniqueSpaceCode, group.getId());
                spaceRepository.save(space);

                // UserSpace 생성
                UserSpace userSpace = UserSpace.of(
                        Role.ADMIN,
                        UserSpaceStatus.ACTIVE,
                        user.getId(),
                        space.getId()
                );
                userSpaceRepository.save(userSpace);
            }
        } catch (Exception e) {
            log.error("스페이스 생성 중 실패! 원인: {}", e.getMessage());

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
        permittedService.check(target, 120, reqUserId);

        Space space = spaceRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

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
        permittedService.check(target, 200, reqUserId);

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
