package com.thc.capstone.dto;

import com.thc.capstone.domain.Permissionuser;
import lombok.*;
import lombok.experimental.SuperBuilder;

public class PermissionuserDto {
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateReqDto{
        Long permissionId;
        Long userId;
        String userUsername;

        public Permissionuser toEntity(){
            return Permissionuser.of(getPermissionId(), getUserId());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto{
        Long permissionId;
        Long userId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        Long permissionId;
        Long userId;

        String userUsername;
        String userNick;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        Long permissionId;
        Long userId;
    }
}
