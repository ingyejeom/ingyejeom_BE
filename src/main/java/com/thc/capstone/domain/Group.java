package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.GroupDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "tb_group")
public class Group extends AuditingFields {
    /**
     * 그룹 이름
     */
    @Setter
    String groupName;

    protected Group() {}
    private Group(String groupName) {
        this.groupName = groupName;
    }

    public static Group of (String groupName) {
        return new Group(groupName);
    }

    /**
     * 그룹 수정
     * 수정 항목 : 그룹 이름
     */
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
