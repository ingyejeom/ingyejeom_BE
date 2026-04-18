package com.thc.capstone.service.impl;

import com.thc.capstone.domain.UserApproval;
import com.thc.capstone.dto.UserApprovalDto;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.mapper.UserApprovalMapper;
import com.thc.capstone.repository.UserApprovalRepository;
import com.thc.capstone.repository.SpaceRepository;
import com.thc.capstone.repository.UserRepository;
import com.thc.capstone.service.UserApprovalService;
import com.thc.capstone.service.PermittedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserApprovalServiceImpl implements UserApprovalService {
    final UserApprovalRepository userApprovalRepository;
    final UserApprovalMapper userApprovalMapper;
    final PermittedService permittedService;

    String target = "userApproval";

    @Override
    @Transactional
    public DefaultDto.CreateResDto create(UserApprovalDto.CreateReqDto param, Long reqUserId) {
//        permittedService.check(target, 110, reqUserId);

        return userApprovalRepository.save(param.toEntity()).toCreateResDto();
    }

    @Override
    public void update(UserApprovalDto.UpdateReqDto param, Long reqUserId) {
        // 존재하는 유저-서명인지 검증
        UserApproval userApproval = userApprovalRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        // 수정 적용 및 DB 저장
        userApproval.update(param);
        userApprovalRepository.save(userApproval);
    }

    @Override
    public void delete(UserApprovalDto.UpdateReqDto param, Long reqUserId) {
        update(UserApprovalDto.UpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    @Override
    public void deleteByApprovalId(Long approvalId, Long reqUserId) {
        List<UserApproval> userApprovals = userApprovalRepository.findAllByApprovalId(approvalId);
        for (UserApproval userApproval : userApprovals) {
            delete(UserApprovalDto.UpdateReqDto.builder()
                    .id(userApproval.getId())
                    .build(), reqUserId);
        }
    }

    // Mapper 를 이용한 사용자 정보 조회 함수
    public UserApprovalDto.DetailResDto get(DefaultDto.DetailReqDto param, Long reqUserId) {
//        permittedService.check(target, 200, reqUserId);

        return userApprovalMapper.detail(param.getId());
    }

    @Override
    public UserApprovalDto.DetailResDto detail(DefaultDto.DetailReqDto param, Long reqUserId) {
        return get(param, reqUserId);
    }

    // Mapper 를 통해 받은 사용자 리스트의 ID 값을 이용해 객체 리스트로 넘김
    public List<UserApprovalDto.DetailResDto> addlist(List<UserApprovalDto.DetailResDto> list, Long reqUserId){
        List<UserApprovalDto.DetailResDto> newList = new ArrayList<>();
        for(UserApprovalDto.DetailResDto userApproval : list) {
            newList.add(get(DefaultDto.DetailReqDto.builder()
                    .id(userApproval.getId())
                    .build(), reqUserId));
        }

        return newList;
    }

    @Override
    public List<UserApprovalDto.DetailResDto> list(UserApprovalDto.ListReqDto param, Long reqUserId) {
        List<UserApprovalDto.DetailResDto> userApprovals = userApprovalMapper.list(param);

        return addlist(userApprovals, reqUserId);
    }
}
