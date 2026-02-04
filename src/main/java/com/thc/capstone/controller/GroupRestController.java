package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/group")
@RestController
public class GroupRestController {
    final GroupService groupService;

    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody GroupDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(groupService.create(param, getUserId(principalDetails)));
    }

    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody GroupDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        groupService.update(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody GroupDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        groupService.delete(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<GroupDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(groupService.detail(param, getUserId(principalDetails)));
    }

    @GetMapping("/list")
    public ResponseEntity<List<GroupDto.DetailResDto>> list(GroupDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(groupService.list(param, getUserId(principalDetails)));
    }
}
