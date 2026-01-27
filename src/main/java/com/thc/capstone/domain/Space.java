package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.SpaceDto;
import com.thc.capstone.dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Space extends AuditingFields {
    @Setter
    String workName;

    @Setter
    String spaceCode;

    protected Space() {}
    private Space(String workName, String spaceCode) {
        this.workName = workName;
        this.spaceCode = spaceCode;
    }

    public static Space of (String workName, String spaceCode) {
        return new Space(workName, spaceCode);
    }

    public void update(SpaceDto.UpdateReqDto param){
        if(param.getDeleted() != null){
            setDeleted(param.getDeleted());
        }
        if(param.getWorkName() != null){
            setWorkName(param.getWorkName());
        }
    }

    public DefaultDto.CreateResDto toCreateResDto(){
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }
}
