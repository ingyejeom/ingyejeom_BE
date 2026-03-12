package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.HandoverDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

/**
 * 인수인계 문서 엔티티
 *
 * 업무 인수인계 시 작성되는 문서를 저장한다.
 * 각 문서는 하나의 UserSpace에 귀속되며, 해당 UserSpace 소유자만 수정할 수 있다.
 */
@Getter
@Entity
public class Handover extends AuditingFields {

    @Setter
    private String title;

    @Setter
    private String role;

    /**
     * 인수인계 내용을 JSON 형태로 저장한다.
     * 모듈 배열, 제목, 역할 등의 구조화된 데이터가 포함된다.
     */
    @Setter
    @Lob
    @Column(columnDefinition = "TEXT")
    private String text;

    @Setter
    private Long userSpaceId;

    /**
     * 폴더 ID. null이면 루트 폴더에 위치한다.
     */
    @Setter
    private Long folderId;

    protected Handover() {}

    private Handover(String title, String role, String text, Long userSpaceId) {
        this.title = title;
        this.role = role;
        this.text = text;
        this.userSpaceId = userSpaceId;
    }

    /**
     * 인수인계 문서 인스턴스를 생성한다.
     */
    public static Handover of(String title, String role, String text, Long userSpaceId) {
        return new Handover(title, role, text, userSpaceId);
    }

    /**
     * 전달된 파라미터 중 null이 아닌 필드만 업데이트한다.
     * 부분 수정(PATCH) 패턴을 지원한다.
     */
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

    /**
     * 문서 생성 후 클라이언트에게 반환할 응답 DTO를 생성한다.
     */
    public DefaultDto.CreateResDto toCreateResDto() {
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }
}
