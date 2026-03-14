package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserSpaceDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class UserSpace extends AuditingFields {
    /**
     * 사용자 권한
     * 요소 : ADMIN, USER
     */
    @Setter
    @Enumerated(EnumType.STRING)
    Role role;

    /**
     * 사용자-스페이스 상태
     * 요소 : ACTIVE, INACTIVE
     */
    @Setter
    @Enumerated(EnumType.STRING)
    UserSpaceStatus status;

    /**
     * 사용자 ID (FK)
     */
    @Setter
    Long userId;

    /**
     * 스페이스 ID (FK)
     */
    @Setter
    Long spaceId;

    protected UserSpace() {}
    private UserSpace(Role role, UserSpaceStatus status, Long userId, Long spaceId) {
        this.role = role;
        this.status = status;
        this.userId = userId;
        this.spaceId = spaceId;
    }

    public static UserSpace of (Role role, UserSpaceStatus status, Long userId, Long spaceId) {
        return new UserSpace(role, status, userId, spaceId);
    }

    public void update(UserSpaceDto.UpdateReqDto param){
        if(param.getDeleted() != null){
            setDeleted(param.getDeleted());
        }
        if(param.getRole() != null){
            setRole(param.getRole());
        }
        if(param.getStatus() != null){
            setStatus(param.getStatus());
        }
    }

    public DefaultDto.CreateResDto toCreateResDto(){
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }
}
