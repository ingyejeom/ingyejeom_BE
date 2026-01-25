package com.thc.capstone.domain;

import com.thc.capstone.dto.DefaultDto;
import com.thc.capstone.dto.UserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter
public class User extends AuditingFields {
    @Setter
    @Column(nullable = false, unique = true)
    String username;

    @Setter
    @Column(nullable = false)
    String password;

    @Setter
    String name;

    @Setter
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
