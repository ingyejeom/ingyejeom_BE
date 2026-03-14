package com.thc.capstone.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "tb_file")
public class File extends AuditingFields {
    /**
     * 원본 파일명 (화면 표시용)
     */
    @Setter
    @Column(nullable = false)
    private String originalFileName;

    /**
     * 저장된 파일명 (UUID) (Unique)
     */
    @Setter
    @Column(nullable = false, unique = true)
    private String storeFileName;

    /**
     * 저장 경로
     */
    @Setter
    private String fileUrl;

    /**
     * 파일 크기
     * - application.yaml 을 통해 파일 크기 제한 수정 (현재 20MB)
     */
    @Setter
    private Long size;

    /**
     * 파일 업로드 시점의 UserSpaceId (FK)
     */
    private Long userSpaceId;

    /**
     * 파일이 속한 폴더 ID (FK)
     */
    @Setter
    private Long folderId;

    protected File() {}
    private File(String originalFileName, String storeFileName, String fileUrl, Long size, Long userSpaceId, Long folderId) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
        this.fileUrl = fileUrl;
        this.size = size;
        this.userSpaceId = userSpaceId;
        this.folderId = folderId;
    }

    public static File of (String originalFileName, String storeFileName, String fileUrl, Long size, Long userSpaceId, Long folderId) {
        return new File(originalFileName, storeFileName, fileUrl, size, userSpaceId, folderId);
    }

    /**
     * 기존에 Update 를 이용하여 진행하던 삭제 기능을 직접 구현
     */
    public void delete() {
        this.setDeleted(true);
    }
}
