package com.thc.capstone.controller;

import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.ChatbotService;
import com.thc.capstone.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
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

    // 파일 업로드 완료 후 파이썬 RAG 서버로 벡터화(Ingest)를 요청하기 위해 ChatbotService를 추가했습니다.
    private final ChatbotService chatbotService;

    // 요청한 사용자 ID 추출
    public Long getUserId(PrincipalDetails principalDetails) {
        if(principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }

        return null;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드",
            description = "파일과 함께 spaceId, folderId(선택)를 FormData 형식으로 전송합니다.")
    public ResponseEntity<Void> upload(
            @ModelAttribute FileDto.UploadReqDto param,
            @AuthenticationPrincipal PrincipalDetails principal
    ) throws IOException {
        byte[] fileBytes = param.getFile().getBytes();// file.getBytes()(파일 입출력 과정) 발생할 수 있는 예외를 처리하기 위해 IOException 추가했습니다.
        String savedFilePath = fileService.upload(param, getUserId(principal));
        // 파일이 저장이 되고 저장된 주소를 확인하여 저장이 잘 됐는지 판단합니다.
        if(savedFilePath != null && !savedFilePath.isEmpty()) {
            // 판단 후 저장이 잘 됐다면 ChatbotDto.IngestReqDto 객체를 생성하고 챗봇 서버로 ingest 요청을 보내기 위해 챗봇서비스 계층에 요청합니다.
            ChatbotDto.IngestReqDto ingestReqDto = ChatbotDto.IngestReqDto.builder().spaceId(param.getSpaceId()).fileBytes(fileBytes).fileName(param.getFile().getOriginalFilename()).build();
            chatbotService.ingestRequest(ingestReqDto, getUserId(principal));
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/folder")
    @Operation(summary = "폴더 생성",
            description = "새로운 폴더를 생성합니다. 부모 폴더 ID를 지정하여 트리 구조를 만들 수 있습니다.")
    public ResponseEntity<Void> createFolder(@RequestBody FileDto.CreateFolderReqDto param, @AuthenticationPrincipal PrincipalDetails principal) {
        fileService.createFolder(param, getUserId(principal));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    @Operation(summary = "파일 삭제",
            description = "지정된 ID의 파일을 삭제합니다. (Soft Delete)")
    public ResponseEntity<Void> deleteFile(@RequestBody DefaultDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        fileService.deleteFile(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/folder")
    @Operation(summary = "폴더 삭제",
            description = "지정된 ID의 폴더를 삭제합니다. (Soft Delete)")
    public ResponseEntity<Void> deleteFolder(@RequestBody FileDto.FolderUpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principal) {
        fileService.deleteFolder(param, getUserId(principal));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    @Operation(summary = "파일 및 폴더 목록 조회",
            description = "특정 스페이스 또는 폴더에 속한 파일과 폴더의 목록을 조회합니다.")
    public ResponseEntity<List<FileDto.ItemResDto>> list(FileDto.ListReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(fileService.list(param, getUserId(principalDetails)));
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "파일 다운로드 또는 보기",
            description = "파일 ID를 통해 파일을 다운로드하거나 브라우저에서 바로 봅니다. mode 에 download 혹은 view 지정 (default: download)")
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
        String contentType = null;

        try {
            // 예: "보고서.pdf" 라는 이름만 보고 "application/pdf" 인지 알아냅니다.
            contentType = Files.probeContentType(Paths.get(resourceDto.getOriginalFileName()));
        } catch (Exception e) {}

        // 타입을 못 알아냈을 경우 기본값 설정
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + encodedUploadFileName + "\"")
                .body(resource);
    }

    @PutMapping("/move")
    @Operation(summary = "파일 또는 폴더 이동",
            description = "파일 또는 폴더를 다른 폴더로 이동시킵니다. type 에 file 인지 folder 인지 지정합니다.")
    public ResponseEntity<Void> move(@RequestBody FileDto.MoveReqDto param, @AuthenticationPrincipal PrincipalDetails principal) {
        fileService.move(param, getUserId(principal));
        return ResponseEntity.ok().build();
    }
}
