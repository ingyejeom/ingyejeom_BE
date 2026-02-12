package com.thc.capstone.dto;

import com.thc.capstone.domain.Permission;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class PermissionDto {
    public static String[][] targets = {
            {"permission", "권한"}
            , {"user", "사용자"}
            , {"space", "업무"}
            , {"group", "소속"}
    };

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class IsPermittedReqDto {
        Long userId;
        String target;
        Integer func; // 110 Create, 120 Update, 200 Read
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateReqDto{
        String title;
        String content;

        public Permission toEntity(){
            return Permission.of(getTitle(), getContent());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto{
        String title;
        String content;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        String title;
        String content;

        // 이 권한이 가지고 있는 전체 권한 상세 목록
        List<PermissiondetailDto.DetailResDto> details;

        // 이 프로젝트에서 다루고 있는 권한 테이블 전체 목록
        String[][] targets;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        String title;
    }
}
