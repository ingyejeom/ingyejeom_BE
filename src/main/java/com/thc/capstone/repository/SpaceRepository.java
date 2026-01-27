package com.thc.capstone.repository;

import com.thc.capstone.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    Space findByWorkName(String workName);
    boolean existsBySpaceCode(String spaceCode);
}
