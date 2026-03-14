package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter
public class User extends AuditingFields {
    /**
     * 로그인 ID (Unique)
     */
    @Setter
    @Column(nullable = false, unique = true)
    String username;

    /**
     * 로그인 비밀번호 (Encrypted 로 관리)
     */
    @Setter
    @Column(nullable = false)
    String password;

    /**
     * 사용자 이름
     */
    @Setter
    String name;

    /**
     * 사용자 이메일 (Unique)
     * - 향후 이메일 검색 등을 위해 Unique 하게 관리
     */
    @Setter
    @Column(nullable = false, unique = true)
    String email;

    protected User() {}
    private User(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static User of (String username, String password, String name, String email) {
        return new User(username, password, name, email);
    }

    /**
     * 사용자 정보 수정
     * 수정 가능 항목 : 비밀번호, 이름, 이메일
     */
    public void update(UserDto.UpdateReqDto param){
        if(param.getDeleted() != null){
            setDeleted(param.getDeleted());
        }
        if(param.getPassword() != null){
            setPassword(param.getPassword());
        }
        if(param.getName() != null){
            setName(param.getName());
        }
        if(param.getEmail() != null){
            setEmail(param.getEmail());
        }
    }

    public DefaultDto.CreateResDto toCreateResDto(){
        return DefaultDto.CreateResDto.builder()
                .id(getId())
                .build();
    }
}
