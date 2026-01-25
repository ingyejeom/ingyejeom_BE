package com.thc.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class DefaultDto {
    @SuperBuilder
    public static class CreateReqDto {
        Long id;
    }

    @SuperBuilder
    public static class UpdateReqDto {
        Long id;
        Boolean deleted;
    }

    @SuperBuilder
    public static class DetailReqDto {
        Long id;
    }

    @SuperBuilder
    public static class DetailResDto {
        Long id;
        Boolean deleted;
        LocalDateTime createdAt;
        LocalDateTime modifiedAt;
    }

    @SuperBuilder
    public static class ListReqDto {
        Boolean deleted;
    }
}
