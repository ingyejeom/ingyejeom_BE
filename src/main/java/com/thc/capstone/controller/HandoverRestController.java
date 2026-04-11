package com.thc.capstone.controller;

import com.thc.capstone.dto.ChatbotDto;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import com.thc.capstone.dto.HandoverDto;
import com.thc.capstone.security.PrincipalDetails;
import com.thc.capstone.service.ChatbotService;
import com.thc.capstone.service.FileService;
import com.thc.capstone.service.HandoverService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 인수인계 문서 REST API 컨트롤러
 *
 * 인수인계 문서의 생성, 조회, 수정, 삭제 및 폴더 이동 기능을 제공한다.
 * 모든 엔드포인트는 USER 권한이 필요하며, 문서 소유자만 수정/삭제가 가능하다.
 */
@RequiredArgsConstructor
@RequestMapping("/api/handover")
@RestController
public class HandoverRestController {

    private final HandoverService handoverService;
    private final FileService fileService;
    private final ChatbotService chatbotService;

    /**
     * Spring Security 인증 정보에서 사용자 ID를 추출한다.
     * 권한 검증 시 요청자 식별에 사용된다.
     */
    private Long getUserId(PrincipalDetails principalDetails) {
        if (principalDetails != null && principalDetails.getUser() != null) {
            return principalDetails.getUser().getId();
        }
        return null;
    }

    /**
     * 새 인수인계 문서를 생성한다.
     * 요청자는 해당 UserSpace의 소유자여야 한다.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<DefaultDto.CreateResDto> create(
            @RequestBody HandoverDto.CreateReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(handoverService.create(param, getUserId(principalDetails)));
    }

    /**
     * spaceId로 새 인수인계 문서를 생성한다.
     * 프론트엔드에서 userSpaceId 대신 spaceId를 전달할 때 사용한다.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/bySpace")
    public ResponseEntity<DefaultDto.CreateResDto> createBySpaceId(
            @RequestBody HandoverDto.CreateBySpaceIdReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(handoverService.createBySpaceId(param, getUserId(principalDetails)));
    }

    /**
     * 기존 인수인계 문서의 제목, 역할, 내용을 수정한다.
     * null이 아닌 필드만 업데이트된다.
     */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("")
    public ResponseEntity<Void> update(
            @RequestBody HandoverDto.UpdateReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        handoverService.update(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    /**
     * 인수인계 문서를 논리적으로 삭제한다.
     * 실제 데이터는 유지되며 deleted 플래그만 true로 변경된다.
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(
            @RequestBody HandoverDto.UpdateReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        handoverService.delete(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    /**
     * 단일 인수인계 문서의 상세 정보를 조회한다.
     * 스페이스명, 그룹명, 작성자명 등 연관 정보가 포함된다.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<HandoverDto.DetailResDto> detail(
            DefaultDto.DetailReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(handoverService.detail(param, getUserId(principalDetails)));
    }

    /**
     * 특정 스페이스의 루트 폴더에 있는 인수인계 문서 목록을 조회한다.
     * 하위 폴더의 문서는 포함되지 않는다.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/space/{spaceId}")
    public ResponseEntity<List<HandoverDto.DetailResDto>> listBySpaceId(
            @PathVariable Long spaceId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(handoverService.listBySpaceId(spaceId, getUserId(principalDetails)));
    }

    /**
     * 특정 스페이스의 특정 폴더에 있는 인수인계 문서 목록을 조회한다.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/space/{spaceId}/folder/{folderId}")
    public ResponseEntity<List<HandoverDto.DetailResDto>> listBySpaceIdAndFolderId(
            @PathVariable Long spaceId,
            @PathVariable Long folderId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        return ResponseEntity.ok(handoverService.listBySpaceIdAndFolderId(spaceId, folderId, getUserId(principalDetails)));
    }

    /**
     * 인수인계 문서의 모듈 데이터(JSON)만 부분 업데이트한다.
     * 제목, 역할 등 메타데이터는 변경되지 않는다.
     */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/modules")
    public ResponseEntity<Void> updateModules(
            @RequestBody Map<String, Object> param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long id = Long.valueOf(param.get("id").toString());
        String text = param.get("text").toString();

        handoverService.updateModules(id, text, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    /**
     * 인수인계 문서를 다른 폴더로 이동한다.
     * targetFolderId가 null이면 루트 폴더로 이동한다.
     */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/move")
    public ResponseEntity<Void> move(
            @RequestBody HandoverDto.MoveReqDto param,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        handoverService.move(param, getUserId(principalDetails));
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 UserSpace에 연결된 인수인계 문서를 조회한다.
     * 문서가 존재하면 200 OK, 없으면 404 Not Found를 반환한다.
     * 스페이스 목록에서 인수인계 버튼 클릭 시 기존 문서 존재 여부 확인에 사용된다.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/userSpace/{userSpaceId}")
    public ResponseEntity<HandoverDto.DetailResDto> getByUserSpaceId(
            @PathVariable Long userSpaceId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        HandoverDto.DetailResDto result = handoverService.getByUserSpaceId(userSpaceId, getUserId(principalDetails));
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> handoverUpload(
            @ModelAttribute HandoverDto.SaveReqDto param,
            @AuthenticationPrincipal PrincipalDetails principal
    ) throws IOException {
        Long userId = getUserId(principal);

        // .PDF 인수인계서 처리 (저장 O, 챗봇 ingest X)
        if (param.getPdfFile() != null && !param.getPdfFile().isEmpty()) {
            // FileService가 기존 UploadReqDto를 사용하므로 객체 변환
            FileDto.UploadReqDto pdfReq = FileDto.UploadReqDto.builder()
                    .files(List.of(param.getPdfFile()))
                    .spaceId(param.getSpaceId())
                    .folderId(param.getFolderId())
                    .build();

            fileService.uploadOnly(pdfReq, userId); // 자료실 저장 완료
        }

        // .MD 인수인계서 처리 (저장 X, 챗봇 ingest O)
        if (param.getMdFile() != null && !param.getMdFile().isEmpty()) {
            byte[] mdFileBytes = param.getMdFile().getBytes();

            ChatbotDto.IngestReqDto ingestReqDto = ChatbotDto.IngestReqDto.builder().spaceId(param.getSpaceId()).fileBytes(mdFileBytes).fileName(param.getMdFile().getOriginalFilename()).build();
            chatbotService.ingestRequest(ingestReqDto, userId);
        }

        return ResponseEntity.ok().build();
    }
}
