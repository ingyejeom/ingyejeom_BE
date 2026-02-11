package com.thc.capstone.dto;

import com.thc.capstone.domain.Group;
import com.thc.capstone.domain.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class GroupDto {
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateReqDto {
        String groupName;

        List<SpaceInfo> spaces;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class SpaceInfo {
            private String workName;
            private String userEmail;
        }

        public Group toEntity(){
            return Group.of(getGroupName());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        String groupName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        String groupName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 이후 검색 기능을 구현할 때 넣을 것
         */
        String groupName;
    }
}
