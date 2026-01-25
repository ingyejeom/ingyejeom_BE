package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserDto;
import com.thc.capstone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserRestController {
    final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<DefaultDto.CreateResDto> login(@RequestBody UserDto.LoginReqDto param) {
        return ResponseEntity.ok(userService.login(param));
    }

    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(@RequestBody UserDto.CreateReqDto param) {
        return ResponseEntity.ok(userService.create(param));
    }

    @PutMapping("")
    public ResponseEntity<Void> update(@RequestBody UserDto.UpdateReqDto param) {
        userService.update(param);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody UserDto.UpdateReqDto param) {
        userService.delete(param);

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<UserDto.DetailResDto> detail(DefaultDto.DetailReqDto param) {
        return ResponseEntity.ok(userService.detail(param));
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDto.DetailResDto>> list(UserDto.ListReqDto param) {
        return ResponseEntity.ok(userService.list(param));
    }
}
