package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.mapper.FileMapper;
import com.thc.capstone.mapper.UserSpaceMapper;
import com.thc.capstone.repository.FileRepository;
import com.thc.capstone.repository.FolderRepository;
import com.thc.capstone.repository.SpaceRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.FileService;
import com.thc.capstone.service.PermittedService;
import com.thc.capstone.service.SpaceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {
    @Value("${file.dir}")
    private String fileDir;

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final FileMapper fileMapper;

    private final UserSpaceRepository userSpaceRepository;

    @Override
    @Transactional
    public String upload(FileDto.UploadReqDto param, Long reqUserId) {
        UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(reqUserId, param.getSpaceId(), UserSpaceStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("해당 스페이스에 대한 권한이 없습니다"));

        MultipartFile multipartFile = param.getFile();
        if(multipartFile == null || multipartFile.isEmpty()) {
            return "";
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        String fullPath = fileDir + storeFileName;
        java.io.File saveFile = new java.io.File(fullPath);

        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }

        try {
            multipartFile.transferTo(saveFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생" + e);
        }

        File file = File.of(
                originalFilename,
                storeFileName,
                fullPath,
                multipartFile.getSize(),
                userSpace.getId(),
                param.getFolderId()
        );

        fileRepository.save(file);
        return fullPath;
    }

    @Override
    @Transactional
    public void createFolder(FileDto.CreateFolderReqDto param, Long reqUserId) {
        // 권한 체크 (업로드와 동일)
        userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(reqUserId, param.getSpaceId(), UserSpaceStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("권한이 없습니다"));

        Folder folder = Folder.of(
                param.getName(),
                param.getParentId(),
                param.getSpaceId()
        );
        folderRepository.save(folder);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return (pos == -1) ? "" : originalFilename.substring(pos + 1);
    }

    @Override
    public void deleteFile(DefaultDto.UpdateReqDto param, Long reqUserId) {
        File file = fileRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("파일이 존재하지 않음"));

        file.delete();

        fileRepository.save(file);
    }

    @Override
    @Transactional
    public void deleteFolder(DefaultDto.UpdateReqDto param, Long reqUserId) {
        Folder folder = folderRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("폴더 없음"));

        // 주의: 폴더 삭제 시 하위 파일/폴더 처리 정책 필요 (여기선 일단 폴더만 삭제)
        // 실제로는 재귀적으로 하위 항목도 delete() 처리해야 함
        folder.delete();
        folderRepository.save(folder);
    }

    @Override
    public List<FileDto.DetailResDto> list(FileDto.ListReqDto param, Long reqUserId) {
        return fileMapper.listItems(param);
    }

    @Override
    public FileDto.FileResourceDto getFileResource(Long fileId, Long reqUserId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일이 존재하지 않음"));

        try {
            Resource resource = new UrlResource("file:" + file.getFileUrl());

            if (resource.exists() && resource.isReadable()) {
                return FileDto.FileResourceDto.builder()
                        .resource(resource)
                        .originalFileName(file.getOriginalFileName())
                        .build();
            } else {
                throw new RuntimeException("파일을 읽을 수 없습니다.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("경로가 잘못되었습니다.", e);
        }
    }

    @Override
    @Transactional
    public void move(FileDto.MoveReqDto param, Long reqUserId) {
        // 1. 타겟 폴더 검증 (존재하는 폴더인지)
        if (param.getTargetFolderId() != null) {
            folderRepository.findById(param.getTargetFolderId())
                    .orElseThrow(() -> new RuntimeException("목적지 폴더가 존재하지 않습니다."));
        }

        // 2. 타입에 따라 이동 처리 (주소표 변경)
        if ("FILE".equals(param.getType())) {
            File file = fileRepository.findById(param.getId())
                    .orElseThrow(() -> new RuntimeException("파일이 없습니다."));
            // ★ 핵심: 소속 폴더 ID만 쓱 바꿔줌
            file.setFolderId(param.getTargetFolderId());

            // (JPA가 변경 감지하여 자동으로 UPDATE 쿼리 날림)

        } else if ("FOLDER".equals(param.getType())) {
            Folder folder = folderRepository.findById(param.getId())
                    .orElseThrow(() -> new RuntimeException("폴더가 없습니다."));

            // (주의) 자기 자신이나 자신의 하위 폴더로 이동하는 것은 막아야 함 (무한 루프 방지)
            if (param.getId().equals(param.getTargetFolderId())) {
                throw new RuntimeException("자기 자신으로 이동할 수 없습니다.");
            }

            // ★ 핵심: 부모 폴더 ID만 쓱 바꿔줌
            folder.setParentId(param.getTargetFolderId());
        }
    }
}
