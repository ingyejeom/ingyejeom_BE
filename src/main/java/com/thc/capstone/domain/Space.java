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
    /**
     * 업무 이름
     * - 스페이스 이름 : {그룹 이름} {업무 이름}
     */
    @Setter
    String workName;

    /**
     * 스페이스 코드
     * - 형식 :
     */
    @Setter
    String spaceCode;

    /**
     * 속한 그룹 ID (FK)
     */
    @Setter
    Long groupId;

    protected Space() {}
    private Space(String workName, String spaceCode, Long groupId) {
        this.workName = workName;
        this.spaceCode = spaceCode;
        this.groupId = groupId;
    }

    public static Space of (String workName, String spaceCode, Long groupId) {
        return new Space(workName, spaceCode, groupId);
    }

    /**
     * 스페이스 수정
     * 수정 항목 : 업무 이름
     */
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
