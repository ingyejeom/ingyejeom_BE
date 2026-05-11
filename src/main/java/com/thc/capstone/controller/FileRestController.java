package com.thc.capstone.controller;

import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.ChatbotService;
import com.thc.capstone.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayOutputStream;
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
    
    // JODConverter 빈을 주입받습니다. (jodconverter-spring-boot-starter 덕분에 자동 설정됨)
    // 참고: 실행 환경(운영체제)에 LibreOffice 또는 OpenOffice가 설치되어 있어야 합니다.
    final DocumentConverter documentConverter;

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
        fileService.upload(param, getUserId(principal));
        return ResponseEntity.ok().build();
    }

    @PutMapping("")
    @Operation(summary = "파일 정보 수정",
            description = "파일 정보를 수정합니다. (파일 이름)")
    public ResponseEntity<Void> updateFile(@RequestBody FileDto.FileUpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        fileService.updateFile(param, getUserId(principalDetails));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/folder")
    @Operation(summary = "폴더 생성",
            description = "새로운 폴더를 생성합니다. 부모 폴더 ID를 지정하여 트리 구조를 만들 수 있습니다.")
    public ResponseEntity<Void> createFolder(@RequestBody FileDto.CreateFolderReqDto param, @AuthenticationPrincipal PrincipalDetails principal) {
        fileService.createFolder(param, getUserId(principal));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/folder")
    @Operation(summary = "폴더 정보 수정",
            description = "폴더 정보를 수정합니다. (폴더 이름)")
    public ResponseEntity<Void> updateFolder(@RequestBody FileDto.FolderUpdateReqDto param, @AuthenticationPrincipal PrincipalDetails principalDetails){
        fileService.updateFolder(param, getUserId(principalDetails));

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
        String originalFileName = resourceDto.getOriginalFileName();
        String encodedUploadFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);

        // 헤더 설정
        String contentDisposition = "attachment"; // 기본값: 다운로드
        if ("view".equals(mode)) {
            contentDisposition = "inline"; // 브라우저에서 열기 (이미지, PDF 등)
            
            // [Office 문서 변환 로직 추가]
            // mode가 view이고, 오피스 문서인 경우 PDF로 변환하여 응답합니다.
            String ext = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                ext = originalFileName.substring(dotIndex + 1).toLowerCase();
            }
            
            if (ext.matches("ppt|pptx|xls|xlsx|doc|docx")) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    
                    // S3에서 읽어온 InputStream을 PDF로 변환
                    documentConverter
                            .convert(resource.getInputStream())
                            .as(DefaultDocumentFormatRegistry.getFormatByExtension(ext))
                            .to(outputStream)
                            .as(DefaultDocumentFormatRegistry.PDF)
                            .execute();
                            
                    // 변환된 PDF 리소스로 교체
                    resource = new ByteArrayResource(outputStream.toByteArray());
                    
                    // 파일명과 Content-Type을 PDF에 맞게 변경
                    encodedUploadFileName = UriUtils.encode(originalFileName.substring(0, dotIndex) + ".pdf", StandardCharsets.UTF_8);
                    
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_PDF)
                            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition + "; filename=\"" + encodedUploadFileName + "\"")
                            .body(resource);
                            
                } catch (OfficeException e) {
                    // 변환 실패 시 로그 출력 및 500 에러 처리
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().build();
                }
            }
        }

        // 파일의 타입을 추론 (오피스 문서가 아니거나 download 모드인 경우)
        String contentType = null;

        try {
            contentType = Files.probeContentType(Paths.get(originalFileName));
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
