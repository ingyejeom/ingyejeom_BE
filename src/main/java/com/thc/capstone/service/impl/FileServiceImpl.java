package com.thc.capstone.service.impl;

import com.thc.capstone.domain.File;
import com.thc.capstone.domain.Space;
import com.thc.capstone.domain.UserSpace;
import com.thc.capstone.domain.UserSpaceStatus;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.mapper.FileMapper;
import com.thc.capstone.mapper.UserSpaceMapper;
import com.thc.capstone.repository.FileRepository;
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
    private final FileMapper fileMapper;

    private final UserSpaceRepository userSpaceRepository;

    @Override
    @Transactional
    public void upload(FileDto.UploadReqDto param, Long reqUserId) {
        UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(reqUserId, param.getSpaceId(), UserSpaceStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("해당 스페이스에 대한 권한이 없습니다"));

        MultipartFile multipartFile = param.getFile();
        if(multipartFile == null || multipartFile.isEmpty()) {
            return;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        String fullPath = fileDir + storeFileName;

        try {
            multipartFile.transferTo(new java.io.File(fullPath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생" + e);
        }

        File file = File.of(
                originalFilename,
                storeFileName,
                fullPath,
                multipartFile.getSize(),
                userSpace.getId()
        );

        fileRepository.save(file);
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
    public void delete(DefaultDto.UpdateReqDto param, Long reqUserId) {
        File file = fileRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("파일이 존재하지 않음"));

        file.delete();

        fileRepository.save(file);
    }

    @Override
    public List<FileDto.DetailResDto> list(FileDto.ListReqDto param, Long reqUserId) {
        return fileMapper.list(param);
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


}
