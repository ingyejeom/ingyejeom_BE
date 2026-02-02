package com.thc.capstone.dto;

import com.thc.capstone.domain.Permissiondetail;
import lombok.*;
import lombok.experimental.SuperBuilder;

public class PermissiondetailDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToggleReqDto{
        Long permissionId;
        String target;
        Integer func;
        Boolean flag; // 입력 true, 삭제 false
    }

    /**/

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateReqDto{
        Long permissionId;
        String target;
        Integer func;

        public Permissiondetail toEntity(){
            return Permissiondetail.of(getPermissionId(), getTarget(), getFunc());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto{
        Long permissionId;
        String target;
        Long func;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        Long permissionId;
        String target;
        Long func;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        Long permissionId;
        String target;
        Long func;
    }
}