package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.PermissionuserDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.PermissionuserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/permissionuser")
@RestController
public class PermissionuserRestController {

    final PermissionuserService permissionuserService;

    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

//    @PreAuthorize("hasRole('USER')")
     @PreAuthorize("permitAll()")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody PermissionuserDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(permissionuserService.create(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody PermissionuserDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        permissionuserService.update(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody PermissionuserDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        permissionuserService.delete(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("")
    public ResponseEntity<PermissionuserDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(permissionuserService.detail(param, getUserId(principalDetails)));
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("permitAll()")
    @GetMapping("/list")
    public ResponseEntity<List<PermissionuserDto.DetailResDto>> list(PermissionuserDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(permissionuserService.list(param, getUserId(principalDetails)));
    }
}
