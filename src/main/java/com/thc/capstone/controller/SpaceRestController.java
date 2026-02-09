package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.SpaceService;
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

    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public ResponseEntity<Void> add(@RequestBody SpaceDto.CreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        spaceService.add(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    /**
     * Request : groupName, workNames (List)
     * 하나의 그룹과 여러 개의 스페이스를 한 번에 생성합니다.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<Void> create(@RequestBody SpaceDto.MultiCreateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        spaceService.create(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody SpaceDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        spaceService.update(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody SpaceDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        spaceService.delete(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("")
    public ResponseEntity<SpaceDto.DetailResDto> detail(DefaultDto.DetailReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(spaceService.detail(param, getUserId(principalDetails)));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/list")
    public ResponseEntity<List<SpaceDto.DetailResDto>> list(SpaceDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(spaceService.list(param, getUserId(principalDetails)));
    }
}
