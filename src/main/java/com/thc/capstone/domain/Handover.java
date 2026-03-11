package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.HandoverDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

// 인수인계 문서를 저장하는 데이터베이스 테이블과 연결된 클래스
@Getter
@Entity
public class Handover extends AuditingFields {

    @Setter
    private String title; // 인수인계서 제목 (예: "총무 인수인계서")

    @Setter
    private String role; // 역할명 (예: "총무", "회장")

    @Setter
    @Lob
    @Column(columnDefinition = "TEXT")
    private String text; // 인수인계 내용을 JSON 형태로 저장

    @Setter
    private Long userSpaceId; // 이 문서가 속한 UserSpace의 ID

    @Setter
    private Long folderId; // 이 문서가 속한 폴더의 ID (null이면 루트)

    // JPA에서 필요한 기본 생성자 (외부에서 직접 호출 불가)
    protected Handover() {}

    // 모든 필드를 받아서 객체를 만드는 생성자 (of 메서드에서만 사용)
    private Handover(String title, String role, String text, Long userSpaceId) {
        this.title = title;
        this.role = role;
        this.text = text;
        this.userSpaceId = userSpaceId;
    }

    // Handover 객체를 만들 때 사용하는 메서드 (new 대신 이것 사용)
    public static Handover of(String title, String role, String text, Long userSpaceId) {
        return new Handover(title, role, text, userSpaceId);
    }

    // 인수인계 문서 내용을 수정할 때 사용하는 메서드
    public void update(HandoverDto.UpdateReqDto param) {
        if (param.getDeleted() != null) {
            setDeleted(param.getDeleted());
        }
        if (param.getTitle() != null) {
            setTitle(param.getTitle());
        }
        if (param.getRole() != null) {
            setRole(param.getRole());
        }
        if (param.getText() != null) {
            setText(param.getText());
        }
    }

    // 문서 생성 후 클라이언트에게 ID를 반환할 때 사용하는 메서드
    public DefaultDto.CreateResDto toCreateResDto() {
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }
}
