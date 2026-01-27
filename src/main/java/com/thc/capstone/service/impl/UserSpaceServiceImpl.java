package com.thc.capstone.service.impl;

import com.thc.capstone.domain.UserSpace;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.mapper.UserSpaceMapper;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.UserSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserSpaceServiceImpl implements UserSpaceService {
    final UserSpaceRepository userSpaceRepository;
    final UserSpaceMapper userSpaceMapper;

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
        UserSpaceDto.DetailResDto res = userSpaceMapper.detail(param.getId());

        return res;
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
    public List<UserSpaceDto.DetailResDto> list(UserSpaceDto.ListReqDto param) {
        return addlist(userSpaceMapper.list(param));
    }
}
