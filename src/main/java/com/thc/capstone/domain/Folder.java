package com.thc.capstone.domain;

import com.thc.capstone.dto.FileDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Folder extends AuditingFields{
    /**
     * 폴더 이름
     */
    @Setter
    @Column(nullable = false)
    private String name;

    /**
     * 부모 폴더 ID
     * - 트리형 구조
     */
    @Setter
    private Long parentId;

    /**
     * 속한 스페이스 ID (FK)
     */
    @Column(nullable = false)
    private Long spaceId;

    protected Folder() {}

    private Folder(Long parentId, String name, Long spaceId) {
        this.name = name;
        this.parentId = parentId;
        this.spaceId = spaceId;
    }

    public static Folder of (String name, Long parentId, Long spaceId) {
        return new Folder(parentId, name, spaceId);
    }

    /**
     * 폴더 수정
     * 수정 항목 : 이름
     */
    public void update(FileDto.FolderUpdateReqDto param){
        if(param.getName() != null){
            setName(param.getName());
        }
        if(param.getDeleted() != null){
            setDeleted(param.getDeleted());
        }
    }
}
