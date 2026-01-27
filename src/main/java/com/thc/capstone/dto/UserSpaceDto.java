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
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 이후 검색 기능을 구현할 때 넣을 것
         */
        UserSpaceStatus status;
    }
}
