package com.thc.capstone.domain;

import com.thc.capstone.dto.UserApprovalDto;
import com.thc.capstone.dto.DefaultDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class UserApproval extends AuditingFields {
    /**
     * 인수인계 시 역할
     * - ASSIGNOR : 인계자
     * - ASSIGNEE : 인수자
     * - ADMIN : 관리자
     */
    @Setter
    @Enumerated(EnumType.STRING)
    ApprovalRole approvalRole;

    /**
     * 해당 유저 ID (FK)
     */
    @Setter
    Long userId;
    
    /**
     * 해당 유저-서명 ID (FK)
     */
    @Setter
    Long approvalId;

    protected UserApproval() {}
    private UserApproval(ApprovalRole approvalRole, Long userId, Long approvalId) {
        this.approvalRole = approvalRole;
        this.userId = userId;
        this.approvalId = approvalId;
    }

    public static UserApproval of (ApprovalRole approvalRole, Long userId, Long approvalId) {
        return new UserApproval(approvalRole, userId, approvalId);
    }

    /**
     * 유저-서명 수정
     * 수정 항목 :
     */
    public void update(UserApprovalDto.UpdateReqDto param){
        if(param.getDeleted() != null){
            setDeleted(param.getDeleted());
        }
    }

    public DefaultDto.CreateResDto toCreateResDto(){
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }
}
