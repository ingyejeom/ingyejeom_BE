package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileService {
    String upload(FileDto.UploadReqDto param, Long reqUserId);

    void createFolder(FileDto.CreateFolderReqDto param, Long reqUserId);

    void deleteFile(DefaultDto.UpdateReqDto param, Long reqUserId);

    void deleteFolder(DefaultDto.UpdateReqDto param, Long reqUserId);

    List<FileDto.DetailResDto> list(FileDto.ListReqDto param, Long reqUserId);

    FileDto.FileResourceDto getFileResource(Long fileId, Long reqUserId);

    void move(FileDto.MoveReqDto param, Long reqUserId);
}
