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
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UploadReqDto {
        Long spaceId;
        MultipartFile file;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        String originalFileName;
        String fileUrl;
        Long size;

        String uploaderName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 이후 검색 기능을 구현할 때 넣을 것
         */
        Long spaceId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class FileResourceDto {
        private Resource resource; // 실제 파일 데이터
        private String originalFileName; // 다운로드 될 때 파일명
    }
}
