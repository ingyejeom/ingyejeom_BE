package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/group")
@RestController
public class GroupRestController {
    final GroupService groupService;

    // 요청한 사용자의 ID 반환
    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @Operation(summary = "그룹 생성",
            description = "그룹 이름과 스페이스 목록을 받아 새로운 그룹을 생성합니다.")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody GroupDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(groupService.create(param, getUserId(principalDetails)));
    }

    @PutMapping("")
    @Operation(summary = "그룹 정보 수정",
            description = "그룹 정보를 수정합니다. (그룹 이름(선택))")
    public ResponseEntity<Void> update(@RequestBody GroupDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        groupService.update(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    @Operation(summary = "그룹 삭제",
            description = "속한 스페이스를 모두 삭제한 후 그룹을 삭제합니다.")
    public ResponseEntity<Void> delete(@RequestBody GroupDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        groupService.delete(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    @Operation(summary = "그룹 정보 조회",
            description = "그룹의 상세 정보를 조회합니다. (그룹 이름)")
    public ResponseEntity<GroupDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(groupService.detail(param, getUserId(principalDetails)));
    }

    @GetMapping("/list")
    @Operation(summary = "그룹 리스트",
            description = "모든 그룹의 정보를 리스트로 조회합니다.")
    public ResponseEntity<List<GroupDto.DetailResDto>> list(GroupDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(groupService.list(param, getUserId(principalDetails)));
    }

    @GetMapping("/getProfileGroups")
    @Operation(summary = "그룹 스크롤 리스트",
            description = "필터링을 통한 그룹 리스트를 스크롤 리스트로 조회")
    public ResponseEntity<List<GroupDto.DetailResDto>> getProfileGroups(GroupDto.ScrollListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(groupService.getProfileGroups(param, getUserId(principalDetails)));
    }
}
