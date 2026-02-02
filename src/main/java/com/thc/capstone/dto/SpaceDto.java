package com.thc.capstone.dto;

import com.thc.capstone.domain.Role;
import com.thc.capstone.domain.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class SpaceDto {
    /**
     * 한 번에 그룹 1개 + 스페이스 N개를 생성하기 위한 DTO
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class MultiCreateReqDto {
        String groupName;
        List<String> workNames;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateReqDto {
        String workName;
        String spaceCode;
        Long groupId;

        public Space toEntity(){
            return Space.of(getWorkName(), getSpaceCode(), getGroupId());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateResDto extends DefaultDto.CreateResDto {
        String workName;
        String spaceCode;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        String workName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        String workName;
        String spaceCode;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 이후 검색 기능을 구현할 때 넣을 것
         */
        String workName;
    }
}
