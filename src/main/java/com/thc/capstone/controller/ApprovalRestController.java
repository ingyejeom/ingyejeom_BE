package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.ApprovalDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.ApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/approval")
@RestController
public class ApprovalRestController {
    final ApprovalService approvalService;

    // 요청한 사용자의 ID 반환
    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "서명 테이블 생성",
            description = "서명 테이블 정보를 받아 새로운 서명 테이블를 생성합니다.")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody ApprovalDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(approvalService.create(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "서명 및 다음단계로 이동",
            description = "서명 시 요청 유저의 차례인지 검증 후 다음 단계로 이동, 모든 서명 완료되면 스페이스 권한 인계")
    @PostMapping("/sign")
    public ResponseEntity<Void> sign(@RequestBody ApprovalDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        approvalService.sign(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "서명 취소",
            description = "서명 취소 시 Approval 삭제 후 UserApproval 연쇄 삭제")
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancel(@RequestBody ApprovalDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        approvalService.cancel(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "서명 테이블 정보 수정",
            description = "서명 테이블 정보를 수정합니다. (현재 상태)")
    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody ApprovalDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        approvalService.update(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "서명 테이블 삭제",
            description = "서명 테이블를 삭제합니다.")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody ApprovalDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        approvalService.delete(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "서명 테이블 정보 조회",
            description = "서명 테이블의 상세 정보를 조회합니다. (현재 상태, 스페이스 ID)")
    @GetMapping("")
    public ResponseEntity<ApprovalDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(approvalService.detail(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "서명 테이블 리스트",
            description = "모든 서명 테이블의 정보를 리스트로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<ApprovalDto.DetailResDto>> list(ApprovalDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(approvalService.list(param, getUserId(principalDetails)));
    }
}
