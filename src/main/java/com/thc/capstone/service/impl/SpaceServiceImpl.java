package com.thc.capstone.service.impl;

import com.thc.capstone.domain.Space;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.mapper.SpaceMapper;
import com.thc.capstone.repository.SpaceRepository;
import com.thc.capstone.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SpaceServiceImpl implements SpaceService {
    final SpaceRepository spaceRepository;
    final SpaceMapper spaceMapper;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8; // 코드 길이 (예: 8자리)
    private final SecureRandom random = new SecureRandom();

    @Override
    public DefaultDto.CreateResDto create(SpaceDto.CreateReqDto param) {
        String uniqueSpaceCode;
        // 코드가 Unique 할 때까지 반복
        do {
            uniqueSpaceCode = generateRandomCode();
        } while (spaceRepository.existsBySpaceCode(uniqueSpaceCode));

        Space space = param.toEntity();
        space.setSpaceCode(uniqueSpaceCode);

        return spaceRepository.save(space).toCreateResDto();
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
    public void update(SpaceDto.UpdateReqDto param) {
        Space space = spaceRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        space.update(param);
        spaceRepository.save(space);
    }

    @Override
    public void delete(SpaceDto.UpdateReqDto param) {
        update(SpaceDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build());
    }

    public SpaceDto.DetailResDto get(DefaultDto.DetailReqDto param) {
        SpaceDto.DetailResDto res = spaceMapper.detail(param.getId());

        return res;
    }

    @Override
    public SpaceDto.DetailResDto detail(DefaultDto.DetailReqDto param) {
        return get(param);
    }

    /**
     * 함수를 통해 반환한 리스트의 ID를 재리스트화
     */
    public List<SpaceDto.DetailResDto> addlist(List<SpaceDto.DetailResDto> list){
        List<SpaceDto.DetailResDto> newList = new ArrayList<>();
        for(SpaceDto.DetailResDto space : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(space.getId())
                    .build()));
        }

        return newList;
    }

    @Override
    public List<SpaceDto.DetailResDto> list(SpaceDto.ListReqDto param) {
        return addlist(spaceMapper.list(param));
    }
}
