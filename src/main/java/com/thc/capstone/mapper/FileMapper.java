package com.thc.capstone.mapper;

import com.thc.capstone.dto.FileDto;

import java.util.List;

public interface FileMapper {
    List<FileDto.DetailResDto> listItems(FileDto.ListReqDto param);
}
