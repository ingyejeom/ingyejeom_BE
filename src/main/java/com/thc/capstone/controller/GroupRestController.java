package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import com.thc.capstone.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/group")
@RestController
public class GroupRestController {
    final GroupService groupService;

    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody GroupDto.CreateReqDto param) {
        return ResponseEntity.ok(groupService.create(param));
    }

    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody GroupDto.UpdateReqDto param) {
        groupService.update(param);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody GroupDto.UpdateReqDto param) {
        groupService.delete(param);

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<GroupDto.DetailResDto> detail(DefaultDto.DetailReqDto param) {
        return ResponseEntity.ok(groupService.detail(param));
    }

    @GetMapping("/list")
    public ResponseEntity<List<GroupDto.DetailResDto>> list(GroupDto.ListReqDto param) {
        return ResponseEntity.ok(groupService.list(param));
    }
}
