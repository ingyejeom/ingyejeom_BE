package com.thc.capstone.repository;

import com.thc.capstone.domain.Role;
import com.thc.capstone.domain.UserSpace;
import com.thc.capstone.domain.UserSpaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSpaceRepository extends JpaRepository<UserSpace, Long> {
    Optional<UserSpace> findByUserIdAndSpaceIdAndRole(Long id, Long spaceId, Role role);

    List<UserSpace> findAllBySpaceIdAndRoleAndStatus(Long spaceId, Role role, UserSpaceStatus status);

    Optional<UserSpace> findFirstByUserIdAndSpaceIdAndStatus(Long userId, Long spaceId, UserSpaceStatus status);

    // userId + spaceId로 조회 (status 무관)
    Optional<UserSpace> findFirstByUserIdAndSpaceId(Long userId, Long spaceId);

    List<UserSpace> findAllByUserId(Long userId);

    List<UserSpace> findAllBySpaceId(Long spaceId);
}
