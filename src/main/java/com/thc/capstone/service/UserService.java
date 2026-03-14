package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    /**
     * 유저 생성
     * @param param 가입할 사용자 정보 (username, password, name, email)
     * @param reqUserId 요청한 사용자 ID
     * @return DB에 저장된 사용자의 고유 ID
     */
    DefaultDto.CreateResDto create(UserDto.CreateReqDto param, Long reqUserId);

    /**
     * 유저 정보 수정
     * @param param 수정 가능한 사용자 정보 (password, name, email)
     * @param reqUserId 요청한 사용자 ID
     */
    void update(UserDto.UpdateReqDto param, Long reqUserId);

    /**
     * 유저 삭제 (Soft Delete)
     * @param param 삭제할 사용자 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void delete(UserDto.UpdateReqDto param, Long reqUserId);

    /**
     * 유저 상세 정보
     * @param param 조회할 사용자의 ID
     * @param reqUserId 요청한 사용자 ID
     * @return 사용자의 상세 데이터 (username, name, email)
     */
    UserDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId);

    /**
     * 유저 조회
     * @param param 필터 검색 조건 (name)
     * @param reqUserId 요청한 사용자 ID
     * @return 사용자의 상세 데이터 리스트 (username, name, email)
     */
    List<UserDto.DetailResDto> list(UserDto.ListReqDto param, Long reqUserId);
}
