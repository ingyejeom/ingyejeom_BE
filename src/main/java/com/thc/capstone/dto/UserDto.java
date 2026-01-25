package com.thc.capstone.dto;

import com.thc.capstone.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

public class UserDto {
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginReqDto {
        String username;
        String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateReqDto {
        String username;
        String password;
        String name;
        String email;

        public User toEntity(){
            return User.of(getUsername(), getPassword(), getName(), getEmail());
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class UpdateReqDto extends DefaultDto.UpdateReqDto {
        String password;
        String name;
        String email;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class DetailResDto extends DefaultDto.DetailResDto {
        String username;
        String name;
        String email;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
    public static class ListReqDto extends DefaultDto.ListReqDto {
        /**
         * 이후 검색 기능을 구현할 때 넣을 것
         */
        String name;
    }
}
