package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/space")
@RestController
public class SpaceRestController {
    final SpaceService spaceService;

    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody SpaceDto.CreateReqDto param) {
        return ResponseEntity.ok(spaceService.create(param));
    }

    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody SpaceDto.UpdateReqDto param) {
        spaceService.update(param);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody SpaceDto.UpdateReqDto param) {
        spaceService.delete(param);

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<SpaceDto.DetailResDto> detail(DefaultDto.DetailReqDto param) {
        return ResponseEntity.ok(spaceService.detail(param));
    }

    @GetMapping("/list")
    public ResponseEntity<List<SpaceDto.DetailResDto>> list(SpaceDto.ListReqDto param) {
        return ResponseEntity.ok(spaceService.list(param));
    }
}
