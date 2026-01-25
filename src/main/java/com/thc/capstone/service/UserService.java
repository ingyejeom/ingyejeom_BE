package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    DefaultDto.CreateResDto login(UserDto.LoginReqDto param);

    DefaultDto.CreateResDto create(UserDto.CreateReqDto param);

    void update(UserDto.UpdateReqDto param);

    void delete(UserDto.UpdateReqDto param);

    UserDto.DetailResDto detail(DefaultDto.DetailReqDto param);

    List<UserDto.DetailResDto> list(UserDto.ListReqDto param);
}
