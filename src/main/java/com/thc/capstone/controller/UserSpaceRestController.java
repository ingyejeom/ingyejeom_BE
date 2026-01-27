package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserSpaceDto;
import com.thc.capstone.service.UserSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/userSpace")
@RestController
public class UserSpaceRestController {
    final UserSpaceService userSpaceService;

    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody UserSpaceDto.CreateReqDto param) {
        return ResponseEntity.ok(userSpaceService.create(param));
    }

    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody UserSpaceDto.UpdateReqDto param) {
        userSpaceService.update(param);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody UserSpaceDto.UpdateReqDto param) {
        userSpaceService.delete(param);

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<UserSpaceDto.DetailResDto> detail(DefaultDto.DetailReqDto param) {
        return ResponseEntity.ok(userSpaceService.detail(param));
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserSpaceDto.DetailResDto>> list(UserSpaceDto.ListReqDto param) {
        return ResponseEntity.ok(userSpaceService.list(param));
    }
}
