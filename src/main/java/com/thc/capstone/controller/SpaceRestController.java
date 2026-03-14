package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/space")
@RestController
public class SpaceRestController {
    final SpaceService spaceService;

    // 요청한 사용자의 ID 반환
    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "스페이스 생성",
            description = "스페이스 정보를 받아 새로운 스페이스를 생성합니다.")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody SpaceDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(spaceService.create(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "스페이스 정보 수정",
            description = "스페이스 정보를 수정합니다. (업무명(선택))")
    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody SpaceDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        spaceService.update(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "스페이스 삭제",
            description = "스페이스를 삭제합니다.")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody SpaceDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        spaceService.delete(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "스페이스 정보 조회",
            description = "스페이스의 상세 정보를 조회합니다. (업무명, 스페이스 코드, 그룹 이름)")
    @GetMapping("")
    public ResponseEntity<SpaceDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(spaceService.detail(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "스페이스 리스트",
            description = "모든 스페이스의 정보를 리스트로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<SpaceDto.DetailResDto>> list(SpaceDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(spaceService.list(param, getUserId(principalDetails)));
    }
}
