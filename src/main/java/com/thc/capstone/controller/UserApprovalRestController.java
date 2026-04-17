package com.thc.capstone.controller;

import com.thc.capstone.dto.UserApprovalDto;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.UserApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/userApproval")
@RestController
public class UserApprovalRestController {
    final UserApprovalService userApprovalService;

    // 요청한 사용자의 ID 반환
    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저-서명 생성",
            description = "유저-서명 정보를 받아 새로운 유저-서명를 생성합니다.")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody UserApprovalDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userApprovalService.create(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저-서명 정보 수정",
            description = "유저-서명 정보를 수정합니다. (없음)")
    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody UserApprovalDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userApprovalService.update(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저-서명 삭제",
            description = "유저-서명를 삭제합니다.")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody UserApprovalDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userApprovalService.delete(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "유저-서명 정보 조회",
            description = "유저-서명의 상세 정보를 조회합니다. (인수인계 시 역할, 유저 ID, 서명 테이블 ID)")
    @GetMapping("")
    public ResponseEntity<UserApprovalDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userApprovalService.detail(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저-서명 리스트",
            description = "모든 유저-서명의 정보를 리스트로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<UserApprovalDto.DetailResDto>> list(UserApprovalDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userApprovalService.list(param, getUserId(principalDetails)));
    }
}
