package com.thc.capstone.repository;

import com.thc.capstone.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {
    boolean existsBySpaceCode(String spaceCode);

    Optional<Space> findBySpaceCode(String spaceCode);

    List<Space> findByGroupId(Long groupId);
}
