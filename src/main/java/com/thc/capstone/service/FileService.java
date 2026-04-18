package com.thc.capstone.service;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FileService {
    /**
     * 파일을 업로드 합니다.
     * @param param 파일 업로드에 필요한 데이터 (스페이스 ID, 부모 폴더 ID, 파일)
     * @param reqUserId 요청한 사용자 ID
     * @return 파일의 저장 경로
     */
    void upload(FileDto.UploadReqDto param, Long reqUserId);

    void uploadOnly(FileDto.UploadReqDto param, Long reqUserId);

    /**
     * 파일 정보 수정
     * @param param 수정 가능한 파일 정보 (파일 이름)
     * @param reqUserId 요청한 사용자 ID
     */
    void updateFile(FileDto.FileUpdateReqDto param, Long reqUserId);

    /**
     * 폴더를 생성합니다.
     * @param param 폴더 생성에 필요한 데이터 (스페이스 ID, 부모 폴더 ID, 폴더 이름)
     * @param reqUserId 요청한 사용자 ID
     */
    void createFolder(FileDto.CreateFolderReqDto param, Long reqUserId);

    /**
     * 파일을 삭제합니다.
     * @param param 삭제할 파일 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void deleteFile(DefaultDto.UpdateReqDto param, Long reqUserId);

    /**
     * 폴더의 정보를 수정합니다.
     * @param param 수정할 폴더 데이터 (폴더 이름)
     * @param reqUserId 요청한 사용자 ID
     */
    void updateFolder(FileDto.FolderUpdateReqDto param, Long reqUserId);

    /**
     * 폴더를 삭제합니다.
     * @param param 삭제할 폴더 ID
     * @param reqUserId 요청한 사용자 ID
     */
    void deleteFolder(FileDto.FolderUpdateReqDto param, Long reqUserId);

    /**
     * 폴더 및 파일을 리스트로 받습니다.
     * @param param 현재 스페이스의 폴더 및 파일 조회에 필요한 데이터 (스페이스 ID, 폴더 ID)
     * @param reqUserId 요청한 사용자 ID
     * @return 현재 스페이스의 폴더 및 파일 데이터 (타입, 이름, 파일 URL, 크기, 업로드한 사용자 이름)
     */
    List<FileDto.ItemResDto> list(FileDto.ListReqDto param, Long reqUserId);

    /**
     * 파일의 리소스를 반환합니다.
     * @param fileId 파일 ID
     * @param reqUserId 요청한 사용자 ID
     * @return 파일 경로
     */
    FileDto.FileResourceDto getFileResource(Long fileId, Long reqUserId);

    /**
     * 파일의 폴더 구조를 변경합니다.
     * @param param 폴더 구조 변경에 필요한 데이터 (파일 ID, 타입, 옮길 폴더 ID)
     * @param reqUserId 요청한 사용자 ID
     */
    void move(FileDto.MoveReqDto param, Long reqUserId);
}
