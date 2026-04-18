package com.thc.capstone.dto;

import com.thc.capstone.domain.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileDto {
    /* FILE */

    /**
     * REQUEST
     * 파일 업로드 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UploadReqDto {
        Long spaceId;
        Long folderId;
        List<MultipartFile> files;
    }
    /**
     * REQUEST
     * 파일 정보 수정 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class FileUpdateReqDto extends DefaultDto.UpdateReqDto {
        String originalFileName;
    }

    /**
     * REQUEST
     * 파일 목록 조회 시 필요 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        Long spaceId;
        Long folderId;
    }

    /**
     * RESPONSE
     * 파일 목록 조회 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ItemResDto extends DefaultDto.DetailResDto {
        String type;

        String name;
        String fileUrl;
        Long size;
        String uploaderName;
    }

    /**
     * RESPONSE
     * 파일 다운로드 시 사용
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class FileResourceDto {
        private Resource resource; // 실제 파일 데이터
        private String originalFileName; // 다운로드 될 때 파일명
    }

    /* FOLDER */

    /**
     * REQUEST
     * 폴더 생성 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class CreateFolderReqDto {
        Long spaceId;

        Long parentId;

        String name;
    }

    /**
     * REQUEST
     * 파일 및 폴더 이동 데이터
     * 파일 관리 시 파일 및 폴더의 위치를 바꿀 때 사용
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class MoveReqDto {
        Long id;
        String type;

        Long targetFolderId;
    }

    /**
     * REQUEST
     * 폴더 정보 수정 데이터
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class FolderUpdateReqDto extends DefaultDto.UpdateReqDto {
        String name;
    }
}
