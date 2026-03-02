package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.HandoverDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.HandoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 인수인계 관련 REST API를 제공하는 컨트롤러
@RequiredArgsConstructor
@RequestMapping("/api/handover")
@RestController
public class HandoverRestController {

    private final HandoverService handoverService;

    // 로그인한 사용자의 ID를 가져오는 메서드
    private Long getUserId(PrincipalDetails principalDetails) {
        if (principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }
        return null;
    }

    // POST /api/handover - 새 인수인계 문서 생성
    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(
            @RequestBody HandoverDto.CreateReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(handoverService.create(param, getUserId(principalDetails)));
    }

    // PUT /api/handover - 인수인계 문서 수정
    @PreAuthorize("hasRole('USER')")
    @PutMapping("")
    public ResponseEntity<Void> update(
            @RequestBody HandoverDto.UpdateReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        handoverService.update(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    // DELETE /api/handover - 인수인계 문서 삭제
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(
            @RequestBody HandoverDto.UpdateReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        handoverService.delete(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    // GET /api/handover?id=1 - 인수인계 문서 1개 상세 조회
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<HandoverDto.DetailResDto> detail(
            DefaultDto.DetailReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(handoverService.detail(param, getUserId(principalDetails)));
    }

    // GET /api/handover/space/1 - 특정 스페이스의 인수인계 문서 목록 조회
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<HandoverDto.DetailResDto>> listBySpaceId(
            @PathVariable Long spaceId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(handoverService.listBySpaceId(spaceId, getUserId(principalDetails)));
    }

    // PUT /api/handover/modules - 모듈 데이터(JSON)만 업데이트
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/modules")
    public ResponseEntity<Void> updateModules(
            @RequestBody Map<String, Object> param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long id = Long.valueOf(param.get("id").toString());
        String text = param.get("text").toString();

        handoverService.updateModules(id, text, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }
}
