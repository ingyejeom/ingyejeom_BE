package com.thc.capstone.service.impl;

import com.thc.capstone.domain.*;
import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import com.thc.capstone.mapper.FileMapper;
import com.thc.capstone.repository.FileRepository;
import com.thc.capstone.repository.FolderRepository;
import com.thc.capstone.repository.UserSpaceRepository;
import com.thc.capstone.service.FileService;
import com.thc.capstone.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final S3Service s3Service;

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final FileMapper fileMapper;

    private final UserSpaceRepository userSpaceRepository;

    @Override
    @Transactional
    public String upload(FileDto.UploadReqDto param, Long reqUserId) {
        // 스페이스에 파일을 올릴 권한이 있는지 확인 (ACTIVE 한 사람)
        UserSpace userSpace = userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(reqUserId, param.getSpaceId(), UserSpaceStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("해당 스페이스에 대한 권한이 없습니다"));

        // 파일 추출
        MultipartFile multipartFile = param.getFile();
        if(multipartFile == null || multipartFile.isEmpty()) {
            return "";
        }

        // 표시용 파일명
        String originalFilename = multipartFile.getOriginalFilename();
        // UUID 가 적용된 파일명 (UNIQUE)
        String storeFileName = createStoreFileName(originalFilename);

        // 파일이 S3에 저장된 경로
        String fileUrl = s3Service.upload(multipartFile, storeFileName);

        // 파일 정보 생성 및 DB에 저장
        File file = File.of(
                originalFilename,
                storeFileName,
                fileUrl,
                multipartFile.getSize(),
                userSpace.getId(),
                param.getFolderId()
        );

        fileRepository.save(file);

        return fileUrl;
        // 파일 저장이 성공한 경우 파이썬에 넘기기 위해 저장 경로를 반환하는 로직을 추가했습니다.
    }

    @Override
    @Transactional
    public void createFolder(FileDto.CreateFolderReqDto param, Long reqUserId) {
        // 권한 체크 (업로드와 동일)
        userSpaceRepository.findFirstByUserIdAndSpaceIdAndStatus(reqUserId, param.getSpaceId(), UserSpaceStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("권한이 없습니다"));

        // DB에 저장
        Folder folder = Folder.of(
                param.getName(),
                param.getParentId(),
                param.getSpaceId()
        );
        folderRepository.save(folder);
    }

    // 파일명 UNIQUE 하게 저장
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 저장될 파일명을 만들기 위한 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return (pos == -1) ? "" : originalFilename.substring(pos + 1);
    }

    @Override
    public void deleteFile(DefaultDto.UpdateReqDto param, Long reqUserId) {
        // 파일 존재여부 검증
        File file = fileRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("파일이 존재하지 않음"));

        // S3에서도 파일 삭제
        s3Service.delete(file.getStoreFileName());

        // 파일 삭제 및 DB에 저장
        file.delete();
        fileRepository.save(file);
    }

    @Override
    public void updateFolder(FileDto.FolderUpdateReqDto param, Long reqUserId) {
        // 폴더 존재여부 검증
        Folder folder = folderRepository.findById(param.getId())
                .orElseThrow(() -> new RuntimeException("데이터가 없습니다"));

        // 변경된 파일 정보 적용 및 DB 저장
        folder.update(param);
        folderRepository.save(folder);
    }

    @Override
    public void deleteFolder(FileDto.FolderUpdateReqDto param, Long reqUserId) {
        // 폴더 삭제
        updateFolder(FileDto.FolderUpdateReqDto.builder()
                .id(param.getId())
                .deleted(true)
                .build(), reqUserId);
    }

    // 폴더 및 파일을 리스트로 전달
    @Override
    public List<FileDto.ItemResDto> list(FileDto.ListReqDto param, Long reqUserId) {
        return fileMapper.listItems(param);
    }

    // 컨트롤러에 파일의 리소스를 전달하기 위함
    @Override
    public FileDto.FileResourceDto getFileResource(Long fileId, Long reqUserId) {
        // 파일 존재 여부 검증
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일이 존재하지 않음"));

        try {
            // DB에 저장된 파일 경로를 이용해 실제 파일에 접근할 수 있는 Resource 객체를 생성합니다.
            Resource resource = new UrlResource(file.getFileUrl());

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
        if (param.getTargetFolderId() != null) {
            folderRepository.findById(param.getTargetFolderId())
                    .orElseThrow(() -> new RuntimeException("목적지 폴더가 존재하지 않습니다."));
        }

        // 타입에 따라 이동 처리 (주소표 변경)
        if ("FILE".equals(param.getType())) {
            File file = fileRepository.findById(param.getId())
                    .orElseThrow(() -> new RuntimeException("파일이 없습니다."));
            // 소속 폴더 ID만 바꿔줌
            file.setFolderId(param.getTargetFolderId());

        } else if ("FOLDER".equals(param.getType())) {
            Folder folder = folderRepository.findById(param.getId())
                    .orElseThrow(() -> new RuntimeException("폴더가 없습니다."));

            // 자기 자신이나 자신의 하위 폴더로 이동하는 것은 막아야 함 (무한 루프 방지)
            if (param.getId().equals(param.getTargetFolderId())) {
                throw new RuntimeException("자기 자신으로 이동할 수 없습니다.");
            }

            // 부모 폴더 ID만 바꿔줌
            folder.setParentId(param.getTargetFolderId());
        }
    }
}
