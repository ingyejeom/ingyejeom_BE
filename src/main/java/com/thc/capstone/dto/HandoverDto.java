package com.thc.capstone.dto;

import com.thc.capstone.domain.Handover;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// 인수인계 관련 데이터를 주고받을 때 사용하는 클래스들
public class HandoverDto {

    // 인수인계 문서를 새로 만들 때 클라이언트가 보내는 데이터
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class CreateReqDto {
        private String title;       // 인수인계서 제목
        private String role;        // 역할명 (총무, 회장 등)
        private String text;        // 인수인계 내용 (JSON 문자열)
        private Long userSpaceId;   // 어떤 UserSpace에 속하는지

        // DTO를 Handover 엔티티로 변환하는 메서드
        public Handover toEntity() {
            return Handover.of(getTitle(), getRole(), getText(), getUserSpaceId());
        }
    }

    // 인수인계 문서 생성 후 클라이언트에게 돌려주는 데이터
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResDto extends DefaultDto.CreateResDto {
        private String title;   // 생성된 문서의 제목
        private String role;    // 생성된 문서의 역할명
    }

    // 인수인계 문서를 수정할 때 클라이언트가 보내는 데이터
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        private String title;   // 수정할 제목 (null이면 수정 안 함)
        private String role;    // 수정할 역할명 (null이면 수정 안 함)
        private String text;    // 수정할 내용 (null이면 수정 안 함)
    }

    // 인수인계 문서 상세 정보를 클라이언트에게 돌려줄 때 사용하는 데이터
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        private String title;       // 인수인계서 제목
        private String role;        // 역할명
        private String text;        // 인수인계 내용 (JSON)
        private Long userSpaceId;   // UserSpace ID
        private String workName;    // 스페이스 이름 (DB에서 JOIN해서 가져옴)
        private String groupName;   // 그룹 이름 (DB에서 JOIN해서 가져옴)
        private String userName;    // 작성자 이름 (DB에서 JOIN해서 가져옴)
    }
}
