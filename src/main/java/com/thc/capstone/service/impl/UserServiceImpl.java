package com.thc.capstone.service.impl;

import com.thc.capstone.domain.User;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.PermissionuserDto;
import com.thc.capstone.dto.UserDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.mapper.UserMapper;
import com.thc.capstone.repository.UserRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.PermissionuserService;
import com.thc.capstone.service.PermittedService;
import com.thc.capstone.service.UserService;
import com.thc.capstone.service.UserSpaceService;
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
    final PermittedService permittedService;
    final PermissionuserService permissionuserService;

    final UserSpaceService userSpaceService;
    final UserSpaceRepository userSpaceRepository;

    String target = "user";

    @Override
    public DefaultDto.CreateResDto create(UserDto.CreateReqDto param, Long reqUserId) {
        // 아이디 중복 체크
        User user = userRepository.findByUsername(param.getUsername());
        if(user != null) {
            throw new RuntimeException("이미 존재하는 아이디입니다");
        }

        // bCrypt 를 통한 비밀번호 암호화
        param.setPassword(bCryptPasswordEncoder.encode(param.getPassword()));

        // 새로운 사용자 생성
        User newUser = userRepository.save(param.toEntity());

        return newUser.toCreateResDto();
    }

    @Override
    public void update(UserDto.UpdateReqDto param, Long reqUserId) {
        // 사용자가 특정되지 않으면 요청한 사용자의 정보 수정
        if(param.getId() == 0){
            param.setId(reqUserId);
        }

        // 타인의 정보를 바꾸려고 시도할 경우 권한 검증
        if(!param.getId().equals(reqUserId)){
            permittedService.isPermitted(target, 120, reqUserId);
        }

        // ID 존재 여부 검사
        User user = userRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        // 수정 적용 및 DB 저장
        user.update(param);
        userRepository.save(user);
    }

    @Override
    public void delete(UserDto.UpdateReqDto param, Long reqUserId) {
        Long targetUserId = (param.getId() == null || param.getId() == 0) ? reqUserId : param.getId();

        // update 를 이용하여 삭제
        update(UserDto.UpdateReqDto.builder()
                .id(targetUserId)
                .deleted(true)
                .build(), reqUserId);

        // 유저 탈퇴 시 연관된 UserSpace 데이터도 일괄 삭제 처리
        userSpaceService.deleteByUserId(targetUserId);
    }

    // Mapper 를 이용한 사용자 정보 조회 함수
    public UserDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
//        permittedService.check(target, 200, reqUserId);

        return userMapper.detail(param.getId());
    }

    @Override
    public UserDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        // 사용자가 특정되지 않으면 요청한 사용자의 정보 수정
        if(param.getId() == null){
            param.setId(reqUserId);
        }

        return get(param, reqUserId);
    }


    // Mapper 를 통해 받은 사용자 리스트의 ID 값을 이용해 객체 리스트로 넘김
    public List<UserDto.DetailResDto> addlist(List<UserDto.DetailResDto> list, Long reqUserId){
        List<UserDto.DetailResDto> newList = new ArrayList<>();

        for(UserDto.DetailResDto user : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(user.getId())
                    .build(), reqUserId));
        }

        return newList;
    }

    @Override
    public List<UserDto.DetailResDto> list(UserDto.ListReqDto param, Long reqUserId) {
        return addlist(userMapper.list(param), reqUserId);
    }
}
