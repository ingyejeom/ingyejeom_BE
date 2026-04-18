package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.UserSpaceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/userSpace")
@RestController
public class UserSpaceRestController {
    final UserSpaceService userSpaceService;

    // 요청한 사용자 ID 추출
    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }
        return null;
    }

    /*
    @Operation(summary = "스페이스 참여",
            description = "스페이스 코드를 입력하여 스페이스에 참여합니다.")
    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody UserSpaceDto.JoinReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userSpaceService.join(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }
    */

    @Operation(summary = "스페이스 초대",
            description = "이메일을 통해 해당 유저를 스페이스에 초대합니다.")
    @PostMapping("/invite")
    public ResponseEntity<Void> invite(@RequestBody UserSpaceDto.InviteReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userSpaceService.invite(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저-스페이스 관계 생성",
            description = "스페이스 생성 시 내부적으로 유저-스페이스 관계가 생성됩니다.")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody UserSpaceDto.CreateReqDto param) {
        return ResponseEntity.ok(userSpaceService.create(param));
    }

    @Operation(summary = "유저-스페이스 관계 정보 수정",
            description = "그룹 생성, 스페이스 인계 등의 상황에서 내부적으로 스페이스의 현재 관리자 및 인계자의 정보를 수정합니다.")
    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody UserSpaceDto.UpdateReqDto param) {
        userSpaceService.update(param);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저-스페이스 관계 삭제",
            description = "유저-스페이스 관계를 삭제합니다. 하지만 현재 사용 되는 곳이 없습니다.")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody UserSpaceDto.UpdateReqDto param) {
        userSpaceService.delete(param);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저-스페이스 정보 조회",
            description = "유저-스페이스 정보를 조회합니다. (역할, 상태, 유저 ID, 스페이스 ID, 그룹 ID, 그룹 이름, 업무 이름, 스페이스 코드)")
    @GetMapping("")
    public ResponseEntity<UserSpaceDto.DetailResDto> detail(DefaultDto.DetailReqDto param) {
        return ResponseEntity.ok(userSpaceService.detail(param));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "스페이스 리스트 조회",
            description = "status, role, groupId 등을 통한 필터링으로 필요한 스페이스 리스트를 조회합니다.<br>본 서비스에서는 보통 프로필에서 스페이스 리스트를 볼 때 사용합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<UserSpaceDto.DetailResDto>> list(UserSpaceDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userSpaceService.list(param));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "프로필의 스페이스 조회",
            description = "프로필에 현재 사용자가 속한 스페이스 모두 조회 (관리자, 담당자)")
    @GetMapping("/getProfileSpaces")
    public ResponseEntity<List<UserSpaceDto.DetailResDto>> getProfileSpaces(UserSpaceDto.ScrollListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails){
        return ResponseEntity.ok(userSpaceService.getProfileSpaces(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "대시보드의 스페이스 조회",
            description = "대시보드에 현재 사용자가 USER 로 존재하는 스페이스 리스트를 페이지 리스트로 조회합니다.")
    @GetMapping("/getDashboardSpaces")
    public ResponseEntity<DefaultDto.PagedListResDto<UserSpaceDto.DetailResDto>> getDashboardSpaces(UserSpaceDto.PagedListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userSpaceService.getDashboardSpaces(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "그룹 관리 페이지의 스페이스 조회 (스크롤 리스트)",
            description = "groupId를 통해 해당 그룹에서 관리자로 존재하는 스페이스 리스트를 스크롤 리스트로 조회합니다.")
    @GetMapping("/getAdminSpaces")
    public ResponseEntity<List<UserSpaceDto.DetailResDto>> getAdminSpaces(UserSpaceDto.ScrollListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userSpaceService.getAdminSpaces(param, getUserId(principalDetails)));
    }
}
