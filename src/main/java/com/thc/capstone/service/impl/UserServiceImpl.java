package com.thc.capstone.service.impl;

import com.thc.capstone.domain.User;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserDto;
import com.thc.capstone.mapper.UserMapper;
import com.thc.capstone.repository.UserRepository;
import com.thc.capstone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;
    final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public DefaultDto.CreateResDto create(UserDto.CreateReqDto param, Long reqUserId) {
        User user = userRepository.findByUsername(param.getUsername());
        if(user != null) {
            throw new RuntimeException("이미 존재하는 아이디입니다");
        }

        param.setPassword(bCryptPasswordEncoder.encode(param.getPassword()));
        User newUser = userRepository.save(param.toEntity());

        return newUser.toCreateResDto();
    }

    @Override
    public void update(UserDto.UpdateReqDto param, Long reqUserId) {
        User user = userRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        user.update(param);
        userRepository.save(user);
    }

    @Override
    public void delete(UserDto.UpdateReqDto param, Long reqUserId) {
        update(UserDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    public UserDto.DetailResDto get(DefaultDto.DetailReqDto param) {
        return userMapper.detail(param.getId());
    }

    @Override
    public UserDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param);
    }

    /**
     * 함수를 통해 반환한 리스트의 ID를 재리스트화
     */
    public List<UserDto.DetailResDto> addlist(List<UserDto.DetailResDto> list){
        List<UserDto.DetailResDto> newList = new ArrayList<>();
        for(UserDto.DetailResDto user : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(user.getId())
                    .build()));
        }

        return newList;
    }

    @Override
    public List<UserDto.DetailResDto> list(UserDto.ListReqDto param) {
        return addlist(userMapper.list(param));
    }
}
