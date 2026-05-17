package com.thc.capstone.repository;

import com.thc.capstone.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File>  findAllByUserSpaceId(Long userSpaceId);
}
