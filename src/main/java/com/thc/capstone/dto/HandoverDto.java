package com.thc.capstone.dto;

import com.thc.capstone.domain.Handover;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 인수인계 문서 데이터 전송 객체 모음
 *
 * API 요청/응답 시 사용되는 DTO 클래스들을 정의한다.
 */
public class HandoverDto {

    /**
     * 인수인계 문서 생성 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class CreateReqDto {
        private String title;
        private String role;
        private String text;
        private Long userSpaceId;

        public Handover toEntity() {
            return Handover.of(getTitle(), getRole(), getText(), getUserSpaceId());
        }
    }

    /**
     * 인수인계 문서 생성 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResDto extends DefaultDto.CreateResDto {
        private String title;
        private String role;
    }

    /**
     * 인수인계 문서 수정 요청 DTO
     * null인 필드는 수정되지 않는다.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        private String title;
        private String role;
        private String text;
    }

    /**
     * 인수인계 문서 상세 조회 응답 DTO
     * 연관 테이블에서 JOIN한 데이터(스페이스명, 그룹명, 작성자명)가 포함된다.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        private String title;
        private String role;
        private String text;
        private Long userSpaceId;
        private Long spaceId;
        private Long folderId;
        private String workName;
        private String groupName;
        private String userName;
    }

    /**
     * 인수인계 문서 폴더 이동 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoveReqDto {
        private Long id;
        /** null이면 루트 폴더로 이동한다. */
        private Long targetFolderId;
    }
}
