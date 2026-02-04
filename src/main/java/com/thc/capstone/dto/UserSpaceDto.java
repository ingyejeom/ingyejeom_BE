package com.thc.capstone.dto;

import com.thc.capstone.domain.Role;
import com.thc.capstone.domain.UserSpace;
import com.thc.capstone.domain.UserSpaceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

public class UserSpaceDto {
    // 스페이스 참여를 위한 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class JoinReqDto {
        private String spaceCode;
    }

    // 스페이스 초대를 위한 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class InviteReqDto {
        private String email;
        private Long spaceId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateReqDto {
        Role role;
        UserSpaceStatus status;
        Long userId;
        Long spaceId;

        public UserSpace toEntity(){
            return UserSpace.of(getRole(), getStatus(), getUserId(), getSpaceId());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        Role role;
        UserSpaceStatus status;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        Role role;
        UserSpaceStatus status;
        Long userId;
        Long spaceId;

        String groupName;
        String workName;
        String spaceCode;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 이후 검색 기능을 구현할 때 넣을 것
         */
        UserSpaceStatus status;
    }
}
