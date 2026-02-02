package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.PermissiondetailDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.PermissiondetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/permissiondetail")
@RestController
public class PermissiondetailRestController {

    final PermissiondetailService permissiondetailService;

    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/toggle")
    public ResponseEntity<Void> toggle(@RequestBody PermissiondetailDto.ToggleReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        permissiondetailService.toggle(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    /**/

//    @PreAuthorize("hasRole('USER')")
     @PreAuthorize("permitAll()")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody PermissiondetailDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(permissiondetailService.create(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody PermissiondetailDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        permissiondetailService.update(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody PermissiondetailDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        permissiondetailService.delete(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("")
    public ResponseEntity<PermissiondetailDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(permissiondetailService.detail(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<List<PermissiondetailDto.DetailResDto>> list(PermissiondetailDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(permissiondetailService.list(param, getUserId(principalDetails)));
    }
}
