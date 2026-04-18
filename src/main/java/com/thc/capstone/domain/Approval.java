package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.ApprovalDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Approval extends AuditingFields {
    /**
     * 진행 상태
     * - ASSIGNOR_TURN
     * - ASSIGNEE_TURN
     * - ADMIN_TURN
     */
    @Setter
    @Enumerated(EnumType.STRING)
    StepStatus stepStatus;

    /**
     * 속한 스페이스 ID (FK)
     */
    @Setter
    Long spaceId;

    protected Approval() {}
    private Approval(StepStatus stepStatus, Long spaceId) {
        this.stepStatus = stepStatus;
        this.spaceId = spaceId;
    }

    public static Approval of (StepStatus stepStatus, Long spaceId) {
        return new Approval(stepStatus, spaceId);
    }

    /**
     * 서명 테이블 수정
     * 수정 항목 : 진행 상태
     */
    public void update(ApprovalDto.UpdateReqDto param){
        if(param.getDeleted() != null){
            setDeleted(param.getDeleted());
        }
        if(param.getStepStatus() != null){
            setStepStatus(param.getStepStatus());
        }
    }

    public DefaultDto.CreateResDto toCreateResDto(){
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }
}
