package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Group extends AuditingFields {
    @Setter
    String groupName;

    @Setter
    Long spaceId;

    protected Group() {}
    private Group(String groupName, Long spaceId) {
        this.groupName = groupName;
        this.spaceId = spaceId;
    }

    public static Group of (String groupName, Long spaceId) {
        return new Group(groupName, spaceId);
    }

    public void update(GroupDto.UpdateReqDto param){
        if(param.getDeleted() != null){
            setDeleted(param.getDeleted());
        }
        if(param.getGroupName() != null){
            setGroupName(param.getGroupName());
        }
    }

    public DefaultDto.CreateResDto toCreateResDto(){
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }
}
