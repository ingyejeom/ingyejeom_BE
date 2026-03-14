package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserRestController {
    final UserService userService;

    // 요청한 사용자의 ID 반환
    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PreAuthorize("permitAll()")
    @Operation(summary = "회원가입",
            description = "유저 정보를 받아 새로운 유저를 생성합니다.")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody UserDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userService.create(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저 정보 수정",
            description = "유저 정보를 수정합니다. (비밀번호, 이름, 이메일 중 변경할 값만 전달)")
    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody UserDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.update(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저 삭제",
            description = "유저를 삭제합니다.")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody UserDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        userService.delete(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저 정보 조회",
            description = "유저의 상세 정보를 조회합니다. (아이디, 이름, 이메일)")
    @GetMapping("")
    public ResponseEntity<UserDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userService.detail(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "유저 리스트",
            description = "모든 유저의 정보를 리스트로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<UserDto.DetailResDto>> list(UserDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(userService.list(param, getUserId(principalDetails)));
    }
}
