package com.thc.capstone.controller;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/file")
@RestController
public class FileRestController {
    final FileService fileService;

    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("spaceId") String spaceId, // FormData는 문자열로 옴
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        FileDto.UploadReqDto req = FileDto.UploadReqDto.builder()
                .file(file)
                .spaceId(Long.parseLong(spaceId))
                .build();

        fileService.upload(req, principal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> delete(@RequestBody DefaultDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        fileService.delete(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDto.DetailResDto>> list(FileDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(fileService.list(param, getUserId(principalDetails)));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> download(
            @PathVariable Long fileId,
            @RequestParam(required = false, defaultValue = "download") String mode,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws IOException {
        // 리소스 가져오기
        FileDto.FileResourceDto resourceDto = fileService.getFileResource(fileId, getUserId(principalDetails));
        Resource resource = resourceDto.getResource();

        // 한글 파일명 깨짐 방지 인코딩
        String encodedUploadFileName = UriUtils.encode(resourceDto.getOriginalFileName(), StandardCharsets.UTF_8);

        // 헤더 설정
        String contentDisposition = "attachment"; // 기본값: 다운로드
        if ("view".equals(mode)) {
            contentDisposition = "inline"; // 브라우저에서 열기 (이미지, PDF 등)
        }

        // 파일의 타입을 추론
        String contentType = Files.probeContentType(Paths.get(resource.getFile().getAbsolutePath()));

        // 타입을 못 알아냈을 경우 기본값 설정
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + encodedUploadFileName + "\"")
                .body(resourceDto.getResource());
    }
}
