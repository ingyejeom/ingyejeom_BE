package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.FileDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "tb_file")
public class File extends AuditingFields {
    @Setter
    @Column(nullable = false)
    private String originalFileName; // 원본 파일명 (화면 표시용)

    @Setter
    @Column(nullable = false, unique = true)
    private String storeFileName;    // 저장된 파일명 (UUID)

    @Setter
    private String fileUrl;          // 저장 경로

    @Setter
    private Long size;

    private Long userSpaceId;

    protected File() {}
    private File(String originalFileName, String storeFileName, String fileUrl, Long size, Long userSpaceId) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
        this.fileUrl = fileUrl;
        this.size = size;
        this.userSpaceId = userSpaceId;
    }

    public static File of (String originalFileName, String storeFileName, String fileUrl, Long size, Long userSpaceId) {
        return new File(originalFileName, storeFileName, fileUrl, size, userSpaceId);
    }

    // Update가 없어 delete 메서드 생성
    public void delete() {
        this.setDeleted(true);
    }
}
