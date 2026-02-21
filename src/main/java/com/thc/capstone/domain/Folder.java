package com.thc.capstone.domain;

import com.thc.capstone.dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Folder extends AuditingFields{
    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    private Long parentId;

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

    public void delete(){
        this.setDeleted(true);
    }

    public void update(String name){
        if(name != null && !name.isEmpty()){
            this.name = name;
        }
    }
}
