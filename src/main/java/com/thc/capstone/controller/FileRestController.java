package com.thc.capstone.controller;

import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.ChatbotService;
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

    // 파일 업로드 완료 후 파이썬 RAG 서버로 벡터화(Ingest)를 요청하기 위해 ChatbotService를 추가했습니다.
    private final ChatbotService chatbotService;

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
            @RequestParam(value = "folderId", required = false) String folderId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) throws IOException { // file.getBytes()(파일 입출력 과정) 발생할 수 있는 예외를 처리하기 위해 IOException 추가했습니다.
        Long fId = (folderId == null || folderId.equals("null") || folderId.isEmpty()) ? null : Long.parseLong(folderId);
        Long sId = Long.parseLong(spaceId);

        // Service 계층에서 transferTo()로 파일 스트림을 소모하기 전에,
        // 파이썬 서버로 직접 쏘기 위한 순수 바이트 배열과 원본 파일명을 미리 메모리에 추출해 둡니다.
        byte[] fileBytes = file.getBytes();
        String originalFileName = file.getOriginalFilename();

        FileDto.UploadReqDto req = FileDto.UploadReqDto.builder()
                .file(file)
                .spaceId(sId)
                .folderId(fId)
                .build();

        // 파일이 저장이 되고 저장된 주소를 확인하여 저장이 잘 됐는지 판단합니다.
        String savedFilePath = fileService.upload(req, principal.getUser().getId());
        if(savedFilePath != null && !savedFilePath.isEmpty()) {
            // 판단 후 저장이 잘 됐다면 ChatbotDto.IngestReqDto 객체를 생성하고 챗봇 서버로 ingest 요청을 보내기 위해 챗봇서비스 계층에 요청합니다.
            ChatbotDto.IngestReqDto ingestReqDto = ChatbotDto.IngestReqDto.builder().spaceId(sId).fileBytes(fileBytes).fileName(originalFileName).build();
            chatbotService.ingestRequest(ingestReqDto, getUserId(principal));
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/folder")
    public ResponseEntity<Void> createFolder(@RequestBody FileDto.CreateFolderReqDto param, @AuthenticationPrincipal PrincipalDetails principal) {
        fileService.createFolder(param, getUserId(principal));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteFile(@RequestBody DefaultDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        fileService.deleteFile(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/folder")
    public ResponseEntity<Void> deleteFolder(@RequestBody DefaultDto.UpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principal) {
        fileService.deleteFolder(param, getUserId(principal));
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

    @PutMapping("/move")
    public ResponseEntity<Void> move(@RequestBody FileDto.MoveReqDto param, @AuthenticationPrincipal PrincipalDetails principal) {
        fileService.move(param, getUserId(principal));
        return ResponseEntity.ok().build();
    }
}
